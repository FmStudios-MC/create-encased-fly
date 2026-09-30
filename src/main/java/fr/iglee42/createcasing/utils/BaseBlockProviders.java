package fr.iglee42.createcasing.utils;

import com.zurrtum.create.api.behaviour.display.DisplaySource;
import com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.zurrtum.create.api.contraption.storage.item.MountedItemStorageType;
import com.zurrtum.create.api.registry.SimpleRegistry;
import com.zurrtum.create.api.stress.BlockStressValues;
import fr.iglee42.createcasing.config.CCStress;
import fr.iglee42.createcasing.config.EncasedConfigs;
import fr.iglee42.createcasing.registries.EncasedBlocks;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.function.DoubleSupplier;

/**
 * Makes every block of the mod behave like its Create counterpart in Create's block-keyed
 * registries: movement and interaction on contraptions, mounted storage, display sources, and
 * stress, capacity and generated speed.
 * <p>
 * Upstream got these from the Registrate builder of each block (and, for stress, a mixin into
 * {@code BlockStressValues} that redirected to upstream's "stress key" block). Providers look the
 * counterpart up when Create first asks, so the order in which Create and this mod register does
 * not matter, and Create's own config changes carry over.
 */
public final class BaseBlockProviders {
    private BaseBlockProviders() {
    }

    public static void register() {
        delegate(MovementBehaviour.REGISTRY);
        delegate(MovingInteractionBehaviour.REGISTRY);
        delegate(MountedItemStorageType.REGISTRY);
        delegate(MountedFluidStorageType.REGISTRY);
        delegate(BlockStressValues.RPM);
        DisplaySource.BY_BLOCK.registerProvider(block -> {
            Block base = EncasedBlocks.getBase(block);
            return base == null ? null : DisplaySource.BY_BLOCK.get(base);
        });

        BlockStressValues.IMPACTS.registerProvider(block -> stress(block, true));
        BlockStressValues.CAPACITIES.registerProvider(block -> stress(block, false));
    }

    private static <V> void delegate(SimpleRegistry<Block, V> registry) {
        registry.registerProvider(block -> {
            Block base = EncasedBlocks.getBase(block);
            return base == null ? null : registry.get(base);
        });
    }

    private static @Nullable DoubleSupplier stress(Block block, boolean impact) {
        CCStress config = EncasedConfigs.common().kinetics.stressValues;
        DoubleSupplier own = impact ? config.getImpact(block) : config.getCapacity(block);
        Block base = EncasedBlocks.getBase(block);
        if (base == null)
            return own;
        DoubleSupplier fromBase = impact ? BlockStressValues.IMPACTS.get(base) : BlockStressValues.CAPACITIES.get(base);
        if (own == null && fromBase == null)
            return null;
        // Decided on each call, so switching encasedBlocksUsesOwnKeys needs no restart.
        return () -> {
            if (own != null && EncasedConfigs.common().kinetics.encasedBlocksUsesOwnKeys.get())
                return own.getAsDouble();
            return fromBase == null ? 0 : fromBase.getAsDouble();
        };
    }
}
