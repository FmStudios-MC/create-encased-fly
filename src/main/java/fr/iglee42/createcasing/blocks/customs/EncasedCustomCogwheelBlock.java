package fr.iglee42.createcasing.blocks.customs;

import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import fr.iglee42.createcasing.registries.EncasedBlockEntities;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/** A casing around one of the mod's own cogwheels. See {@code EncasedCustomShaftBlock} for pick-block. */
public class EncasedCustomCogwheelBlock extends EncasedCogwheelBlock {
    private final Supplier<? extends Block> cogwheel;

    public EncasedCustomCogwheelBlock(Properties properties, boolean large, Block casing, Supplier<? extends Block> cogwheel) {
        super(properties, large, casing);
        this.cogwheel = cogwheel;
    }

    public Block getCogwheel() {
        return cogwheel.get();
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide())
            return InteractionResult.SUCCESS;
        context.getLevel().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, context.getClickedPos(), getId(state));
        KineticBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(),
            cogwheel.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS)));
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntityType<? extends SimpleKineticBlockEntity> getBlockEntityType() {
        return isLargeCog() ? EncasedBlockEntities.ENCASED_CUSTOM_LARGE_COGWHEEL : EncasedBlockEntities.ENCASED_CUSTOM_COGWHEEL;
    }
}
