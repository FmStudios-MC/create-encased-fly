package fr.iglee42.createcasing.blocks.shafts;

import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import fr.iglee42.createcasing.registries.EncasedBlockEntities;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A casing around one of the mod's own shafts. Upstream's pick-block returned the shaft when the
 * shaft's end was targeted; 26.2 no longer passes the hit to {@code getCloneItemStack}, so it
 * returns the casing, as Create's encased shafts do.
 */
public class EncasedCustomShaftBlock extends EncasedShaftBlock {
    private final Supplier<? extends Block> shaft;

    public EncasedCustomShaftBlock(Properties properties, Block casing, Supplier<? extends Block> shaft) {
        super(properties, casing);
        this.shaft = shaft;
    }

    public Block getShaft() {
        return shaft.get();
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return EncasedBlockEntities.ENCASED_CUSTOM_SHAFT;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide())
            return InteractionResult.SUCCESS;
        context.getLevel().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, context.getClickedPos(), getId(state));
        KineticBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(),
            shaft.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS)));
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity be) {
        return ItemRequirement.of(shaft.get().defaultBlockState(), be);
    }
}
