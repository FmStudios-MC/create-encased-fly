package fr.iglee42.createcasing.blockEntities;

import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import fr.iglee42.createcasing.blocks.shafts.EncasedCustomShaftBlock;
import fr.iglee42.createcasing.blocks.shafts.WoodenShaftBlock;
import fr.iglee42.createcasing.config.EncasedConfigs;
import fr.iglee42.createcasing.transmissions.TransmissionSets;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * The breaking rules of upstream's glass, wooden and encased custom shaft block entities, which
 * repeated them in each class.
 */
final class ShaftBreaking {
    private ShaftBreaking() {
    }

    static boolean isGlass(Block block) {
        Block glass = TransmissionSets.GLASS.getShaft();
        return glass != null && (block == glass || block instanceof EncasedCustomShaftBlock encased && encased.getShaft() == glass);
    }

    static boolean isWooden(Block block) {
        return block instanceof WoodenShaftBlock || block instanceof EncasedCustomShaftBlock encased && encased.getShaft() instanceof WoodenShaftBlock;
    }

    /** Glass shafts shatter when overstressed, unless the stress comes through another glass shaft. */
    static void tickGlass(KineticBlockEntity be) {
        Level level = be.getLevel();
        if (level == null || level.isClientSide() || !EncasedConfigs.common().kinetics.shouldGlassShaftBreak.get())
            return;
        if (!be.isOverStressed())
            return;
        if (be.source != null && isGlass(level.getBlockState(be.source).getBlock()))
            return;
        level.destroyBlock(be.getBlockPos(), false);
    }

    /** Wooden shafts break above the configured speed, unless driven by another wooden shaft. */
    static void tickWooden(KineticBlockEntity be) {
        Level level = be.getLevel();
        if (level == null || level.isClientSide() || !EncasedConfigs.common().kinetics.shouldWoodenShaftBreak.get())
            return;
        int max = EncasedConfigs.common().kinetics.maxSpeedWoodenShaft.get();
        if (Math.abs(be.getSpeed()) <= max)
            return;
        if (be.source != null && isWooden(level.getBlockState(be.source).getBlock()))
            return;
        level.destroyBlock(be.getBlockPos(), false);
    }
}
