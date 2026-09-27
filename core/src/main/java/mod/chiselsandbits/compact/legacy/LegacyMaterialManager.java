package mod.chiselsandbits.compact.legacy;

import com.google.common.collect.Lists;

import java.util.List;

@Deprecated
public class LegacyMaterialManager
{
    private static final LegacyMaterialManager INSTANCE = new LegacyMaterialManager();
    private final List<String> materialNames     = Lists.newArrayList();

    private LegacyMaterialManager()
    {
        this.registerMapping("wood");
        this.registerMapping("rock");
        this.registerMapping("iron");
        this.registerMapping("cloth");
        this.registerMapping("ice");
        this.registerMapping("packed_ice");
        this.registerMapping("clay");
        this.registerMapping("glass");
        this.registerMapping("sand");
        this.registerMapping("ground");
        this.registerMapping("grass");
        this.registerMapping("snow");
        this.registerMapping("fluid");
        this.registerMapping("leaves");
        this.registerMapping("plant");
        this.registerMapping("wool");
        this.registerMapping("nether_wood");
        this.registerMapping("froglight");
        this.registerMapping("amethyst");
    }

    public void registerMapping(final String name)
    {
        materialNames.add(name);
    }

    public static LegacyMaterialManager getInstance()
    {
        return INSTANCE;
    }

    public List<String> getMaterialNames()
    {
        return materialNames;
    }
}
