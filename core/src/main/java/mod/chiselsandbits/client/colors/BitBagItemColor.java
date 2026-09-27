package mod.chiselsandbits.client.colors;

import mod.chiselsandbits.item.BitBagItem;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BitBagItemColor implements ItemColor
{
    @Override
    public int getColor(final @NotNull ItemStack stack, final int vertexColor)
    {
        if ( vertexColor == 1 )
        {
            DyeColor color = BitBagItem.getDyedColor( stack );
            if ( color != null )
                return color.getTextureDiffuseColor();
        }

        return -1;
    }
}
