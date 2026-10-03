package mod.chiselsandbits.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Applies user-provided block ID remaps while upgrading legacy (1.16.5) chiseled block data.
 * <p>
 * Remap files live in {@code config/chiselsandbits/remaps/*.json} and, as a fallback,
 * in RemapIDs' own {@code config/remapids/remaps/*.json} directory, and use the same shape as
 * RemapIDs remap files, so existing RemapIDs files work verbatim. When both directories define
 * a remap for the same source, the {@code chiselsandbits} directory wins. Only entries
 * applicable to blocks are honored (no {@code types} array, or one containing {@code "block"}):
 * <pre>
 * {
 *   "remaps": [
 *     { "source": "oldmod:copper_ore", "target": "minecraft:copper_ore" },
 *     { "source": "oldmod:*_ore", "target": "newmod:*_ore", "types": ["block"] }
 *   ]
 * }
 * </pre>
 * Exact entries support chain flattening ({@code A -> B}, {@code B -> C} resolves {@code A -> C},
 * capped at 10 hops, circular chains are dropped). {@code *} wildcards capture and substitute in
 * order. Tag ({@code #}) sources, numerical IDs, and property maps are not supported; entries
 * using them are skipped with a warning. The remapper works standalone: RemapIDs does not need
 * to be installed, since its runtime interception only rewrites vanilla chunk/item NBT and never
 * reaches mod block entity data.
 */
public final class LegacyBlockRemapper
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String REMAP_SUBDIR = "chiselsandbits/remaps";
    private static final int MAX_CHAIN_DEPTH = 10;

    private static volatile LegacyBlockRemapper instance;

    public static LegacyBlockRemapper getInstance()
    {
        LegacyBlockRemapper result = instance;
        if (result == null)
        {
            synchronized (LegacyBlockRemapper.class)
            {
                result = instance;
                if (result == null)
                {
                    result = new LegacyBlockRemapper(remapDirectory());
                    instance = result;
                }
            }
        }
        return result;
    }

    public static Path remapDirectory()
    {
        return Paths.get(System.getProperty("user.dir", "."), "config", REMAP_SUBDIR);
    }

    public static Path remapIdsDirectory()
    {
        return Paths.get(System.getProperty("user.dir", "."), "config", "remapids", "remaps");
    }

    private final Map<String, String> exactRemaps = new HashMap<>();
    private final List<WildcardRemap> wildcardRemaps = new ArrayList<>();

    private LegacyBlockRemapper(final Path remapDirectory)
    {
        load(remapDirectory, remapIdsDirectory());
    }

    public boolean hasRemaps()
    {
        return !exactRemaps.isEmpty() || !wildcardRemaps.isEmpty();
    }

    /**
     * Rewrites a block ID from legacy data to its replacement, or returns the input unchanged.
     */
    public String remapBlockId(final String namespacedId)
    {
        if (namespacedId == null || namespacedId.isEmpty())
        {
            return namespacedId;
        }

        final String exact = exactRemaps.get(namespacedId);
        if (exact != null)
        {
            return exact;
        }

        for (final WildcardRemap wildcardRemap : wildcardRemaps)
        {
            final String rewritten = wildcardRemap.apply(namespacedId);
            if (rewritten != null)
            {
                return rewritten;
            }
        }

        return namespacedId;
    }

    private void load(final Path remapDirectory, final Path remapIdsDirectory)
    {
        final List<Path> files = new ArrayList<>();
        files.addAll(listRemapFiles(remapIdsDirectory));
        files.addAll(listRemapFiles(remapDirectory));

        if (files.isEmpty())
        {
            LOGGER.debug("No legacy block remap files found in {} or {}. Chiseled block upgrades will not remap IDs. Create RemapIDs-compatible remap files there to migrate removed mod blocks.", remapIdsDirectory, remapDirectory);
            return;
        }

        final Map<String, String> rawExact = new HashMap<>();
        final List<WildcardRemap> rawWildcards = new ArrayList<>();
        int skipped = 0;
        for (final Path file : files)
        {
            skipped += loadFile(file, rawExact, rawWildcards);
        }

        rawWildcards.forEach(wildcard -> wildcardRemaps.add(wildcard));
        exactRemaps.putAll(flattenChains(rawExact));

        if (hasRemaps())
        {
            LOGGER.info("Loaded {} legacy block remaps ({} exact, {} wildcard) from {} file(s) in {} and {}.", exactRemaps.size() + wildcardRemaps.size(), exactRemaps.size(), wildcardRemaps.size(), files.size(), remapIdsDirectory, remapDirectory);
        }
        else if (skipped > 0)
        {
            LOGGER.warn("Found {} remap file(s) in {} and {} but no usable block remaps ({} entries skipped).", files.size(), remapIdsDirectory, remapDirectory, skipped);
        }
    }

    private List<Path> listRemapFiles(final Path directory)
    {
        final List<Path> files = new ArrayList<>();
        if (!Files.isDirectory(directory))
        {
            return files;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.json"))
        {
            for (final Path file : stream)
            {
                if (Files.isRegularFile(file))
                {
                    files.add(file);
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.warn("Failed to list legacy block remap directory {}. Its remaps will be ignored.", directory, e);
            return files;
        }
        files.sort(Comparator.comparing(path -> path.getFileName().toString()));
        return files;
    }

    private int loadFile(final Path file, final Map<String, String> rawExact, final List<WildcardRemap> rawWildcards)
    {
        final JsonObject root;
        try (Reader reader = Files.newBufferedReader(file))
        {
            final JsonElement parsed = JsonParser.parseReader(reader);
            if (parsed == null || !parsed.isJsonObject())
            {
                LOGGER.warn("Skipping remap file {}: expected an object with a \"remaps\" array.", file.getFileName());
                return 0;
            }
            root = parsed.getAsJsonObject();
        }
        catch (Exception e)
        {
            LOGGER.warn("Skipping unreadable remap file {}: {}", file.getFileName(), e.toString());
            return 0;
        }

        if (!root.has("remaps") || !root.get("remaps").isJsonArray())
        {
            LOGGER.warn("Skipping remap file {}: expected an object with a \"remaps\" array.", file.getFileName());
            return 0;
        }

        int skipped = 0;
        final JsonArray remaps = root.getAsJsonArray("remaps");
        for (final JsonElement element : remaps)
        {
            if (!element.isJsonObject() || !addEntry(file, element.getAsJsonObject(), rawExact, rawWildcards))
            {
                skipped++;
            }
        }
        return skipped;
    }

    private boolean addEntry(final Path file, final JsonObject entry, final Map<String, String> rawExact, final List<WildcardRemap> rawWildcards)
    {
        if (!entry.has("source") || !entry.get("source").isJsonPrimitive() || !entry.getAsJsonPrimitive("source").isString()
            || !entry.has("target") || !entry.get("target").isJsonPrimitive() || !entry.getAsJsonPrimitive("target").isString())
        {
            LOGGER.warn("Skipping remap entry in {}: \"source\" and \"target\" strings are required.", file.getFileName());
            return false;
        }

        final String source = entry.get("source").getAsString().trim();
        final String target = entry.get("target").getAsString().trim();

        if (entry.has("types"))
        {
            if (!entry.get("types").isJsonArray())
            {
                LOGGER.warn("Skipping remap entry {} -> {} in {}: \"types\" must be an array.", source, target, file.getFileName());
                return false;
            }
            boolean blockApplies = false;
            for (final JsonElement type : entry.getAsJsonArray("types"))
            {
                if (type.isJsonPrimitive() && type.getAsJsonPrimitive().isString() && type.getAsString().equalsIgnoreCase("block"))
                {
                    blockApplies = true;
                    break;
                }
            }
            if (!blockApplies)
            {
                return true;
            }
        }

        if (source.isEmpty() || target.isEmpty() || !source.contains(":") || !target.contains(":"))
        {
            LOGGER.warn("Skipping remap entry {} -> {} in {}: IDs must be non-empty and namespaced.", source, target, file.getFileName());
            return false;
        }

        final boolean sourceWildcard = source.contains("*");
        final boolean targetWildcard = target.contains("*");
        if (sourceWildcard != targetWildcard)
        {
            LOGGER.warn("Skipping remap entry {} -> {} in {}: wildcards must appear in both source and target.", source, target, file.getFileName());
            return false;
        }

        if (sourceWildcard)
        {
            try
            {
                rawWildcards.add(new WildcardRemap(source, target));
            }
            catch (IllegalArgumentException e)
            {
                LOGGER.warn("Skipping remap entry {} -> {} in {}: {}", source, target, file.getFileName(), e.getMessage());
                return false;
            }
            return true;
        }

        if (source.equals(target))
        {
            return true;
        }

        rawExact.put(source, target);
        return true;
    }

    private Map<String, String> flattenChains(final Map<String, String> rawExact)
    {
        final Map<String, String> flattened = new HashMap<>();
        for (final Map.Entry<String, String> entry : rawExact.entrySet())
        {
            final Set<String> visited = new HashSet<>();
            visited.add(entry.getKey());
            String current = entry.getValue();
            int depth = 0;
            while (rawExact.containsKey(current) && depth < MAX_CHAIN_DEPTH)
            {
                if (!visited.add(current))
                {
                    LOGGER.warn("Dropping circular block remap chain at {}.", entry.getKey());
                    current = null;
                    break;
                }
                current = rawExact.get(current);
                depth++;
            }
            if (current == null || current.equals(entry.getKey()))
            {
                continue;
            }
            if (depth >= MAX_CHAIN_DEPTH)
            {
                LOGGER.warn("Dropping overly deep block remap chain at {} (deeper than {}).", entry.getKey(), MAX_CHAIN_DEPTH);
                continue;
            }
            flattened.put(entry.getKey(), current);
        }
        return flattened;
    }

    private static final class WildcardRemap
    {
        private final Pattern pattern;
        private final String replacement;

        WildcardRemap(final String source, final String target)
        {
            final String[] sourceParts = source.split("\\*", -1);
            final String[] targetParts = target.split("\\*", -1);
            if (sourceParts.length != targetParts.length)
            {
                throw new IllegalArgumentException("source and target must contain the same number of wildcards");
            }

            final StringBuilder patternBuilder = new StringBuilder("^");
            for (int i = 0; i < sourceParts.length; i++)
            {
                patternBuilder.append(Pattern.quote(sourceParts[i]));
                if (i < sourceParts.length - 1)
                {
                    patternBuilder.append("(.*)");
                }
            }
            patternBuilder.append("$");
            this.pattern = Pattern.compile(patternBuilder.toString());

            final StringBuilder replacementBuilder = new StringBuilder();
            for (int i = 0; i < targetParts.length; i++)
            {
                replacementBuilder.append(Matcher.quoteReplacement(targetParts[i]));
                if (i < targetParts.length - 1)
                {
                    replacementBuilder.append("$").append(i + 1);
                }
            }
            this.replacement = replacementBuilder.toString();
        }

        String apply(final String namespacedId)
        {
            final Matcher matcher = pattern.matcher(namespacedId);
            if (!matcher.matches())
            {
                return null;
            }
            return matcher.replaceFirst(replacement);
        }
    }
}
