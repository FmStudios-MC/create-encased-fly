package fr.iglee42.createcasing.utils;

import fr.iglee42.createcasing.registries.EncasedBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Create checks many blocks by identity ({@code state.is(AllBlocks.DEPOT)}). The mixins wrap those
 * checks so that the mod's variants count as the block they were made from. Upstream patched each
 * such check with its own condition ({@code instanceof}, "is in a casing set"); here they share
 * the base block lookup of {@link EncasedBlocks#BASE}.
 */
public final class BaseBlockChecks {
    private BaseBlockChecks() {
    }

    /** {@code state.is(target)} as Create wrote it, or the state's block is a variant of {@code target}. */
    public static boolean is(BlockState state, Object target, boolean original) {
        return original || target instanceof Block block && EncasedBlocks.getBase(state.getBlock()) == block;
    }

    /** {@code stack.is(target)} for a block item, or the stack holds a variant of that block. */
    public static boolean is(ItemStack stack, Object target, boolean original) {
        if (original)
            return true;
        if (!(target instanceof BlockItem targetItem) || !(stack.getItem() instanceof BlockItem item))
            return false;
        return EncasedBlocks.getBase(item.getBlock()) == targetItem.getBlock();
    }
}
