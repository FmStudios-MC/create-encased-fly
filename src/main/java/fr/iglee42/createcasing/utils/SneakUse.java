package fr.iglee42.createcasing.utils;

import com.zurrtum.create.AllItems;
import fr.iglee42.createcasing.blocks.ConfigurableGearboxBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Sneaking with a shaft still uses the configurable gearbox (it puts the shaft on the far face).
 * Upstream overrode NeoForge's {@code doesSneakBypassUse}; Fabric has no such hook, so the
 * game mode mixins ask this instead.
 */
public final class SneakUse {
    private SneakUse() {
    }

    public static boolean bypassesSneak(Level level, ItemStack stack, BlockHitResult hit) {
        return stack.is(AllItems.SHAFT) && level.getBlockState(hit.getBlockPos()).getBlock() instanceof ConfigurableGearboxBlock;
    }
}
