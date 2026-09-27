package mod.chiselsandbits.utils;

import com.communi.suggestu.scena.core.registries.IPlatformRegistryManager;
import mod.chiselsandbits.api.block.state.id.IBlockStateIdManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class StringStateUtils
{

    private static final Logger LOGGER = LogManager.getLogger();

    private StringStateUtils()
    {
        throw new IllegalStateException("Can not instantiate an instance of: StringStateUtils. This is a utility class");
    }

    public static int getStateIDFromName(
      final String name )
    {
        final String[] parts = name.split( "[?&]" );

        parts[0] = URLDecoder.decode( parts[0], StandardCharsets.UTF_8);

        final Optional<Block> blk = IPlatformRegistryManager.getInstance().getBlockRegistry().getValue(ResourceLocation.parse(parts[0]));

        if ( blk.isEmpty() || blk.get() instanceof AirBlock)
        {
            return 0;
        }

        BlockState state = blk.get().defaultBlockState();

        // rebuild state...
        for ( int x = 1; x < parts.length; ++x )
        {
            try
            {
                if (!parts[x].isEmpty())
                {
                    final String[] nameValues = parts[x].split( "[=]" );
                    if ( nameValues.length == 2 )
                    {
                        nameValues[0] = URLDecoder.decode( nameValues[0], StandardCharsets.UTF_8);
                        nameValues[1] = URLDecoder.decode( nameValues[1], StandardCharsets.UTF_8);

                        state = withState( state, blk.get(), nameValues );
                    }
                }
            }
            catch ( final Exception err )
            {
                LOGGER.error( "Failed to reload Property from store data : " + name, err );
            }
        }

        return IBlockStateIdManager.getInstance().getIdFrom( state );
    }

    private static BlockState withState(
      final BlockState state,
      final Block blk,
      final String[] nameval )
    {
        final Property<?> prop = blk.getStateDefinition().getProperty(nameval[0]);
        if ( prop == null )
        {
            LOGGER.info( nameval[0] + " is not a valid property for " + IPlatformRegistryManager.getInstance().getBlockRegistry().getKey(blk) );
            return state;
        }

        return setPropValue(state, prop, nameval[1]);
    }

    public static <T extends Comparable<T>> BlockState setPropValue(BlockState blockState, Property<T> property, String value) {
        final Optional<T> pv = property.getValue( value );
        if ( pv.isPresent() )
        {
            return blockState.setValue( property, pv.get());
        }
        else
        {
            LOGGER.info( value + " is not a valid value of " + property.getName() + " for " + IPlatformRegistryManager.getInstance().getBlockRegistry().getKey(blockState.getBlock()) );
            return blockState;
        }
    }

    public static String getNameFromStateID(
      final int key )
    {
        final BlockState state = IBlockStateIdManager.getInstance().getBlockStateFrom( key );
        final Block blk = state.getBlock();

        String sname = "air?";

        final StringBuilder stateName = new StringBuilder( URLEncoder.encode( Objects.requireNonNull(IPlatformRegistryManager.getInstance().getBlockRegistry().getKey(blk)).toString(), StandardCharsets.UTF_8) );
        stateName.append( '?' );

        boolean first = true;
        for ( final Property<?> p : state.getBlock().getStateDefinition().getProperties() )
        {
            if ( !first )
            {
                stateName.append( '&' );
            }

            first = false;

            final Comparable<?> propVal = state.getValue(p);

            String saveAs;
            if ( propVal instanceof StringRepresentable)
            {
                saveAs = ( (StringRepresentable) propVal ).getSerializedName();
            }
            else
            {
                saveAs = propVal.toString();
            }

            stateName.append( URLEncoder.encode( p.getName(), StandardCharsets.UTF_8) );
            stateName.append( '=' );
            stateName.append( URLEncoder.encode( saveAs, StandardCharsets.UTF_8) );
        }

        sname = stateName.toString();

        return sname;
    }
}
