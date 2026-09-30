package fr.iglee42.createcasing.blocks.fluids;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour;
import com.zurrtum.create.content.fluids.pipes.EncasedPipeBlock;
import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/** A casing around one of the mod's own fluid pipes. */
public class EncasedCustomPipeBlock extends EncasedPipeBlock {
    private final Supplier<? extends FluidPipeBlock> pipe;

    public EncasedCustomPipeBlock(Properties properties, Block casing, Supplier<? extends FluidPipeBlock> pipe) {
        super(properties, casing);
        this.pipe = pipe;
    }

    public FluidPipeBlock getPipe() {
        return pipe.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (world.isClientSide())
            return InteractionResult.SUCCESS;

        context.getLevel().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, context.getClickedPos(), getId(state));
        BlockState equivalentPipe = transferSixWayProperties(state, pipe.get().defaultBlockState());

        Direction firstFound = Direction.UP;
        for (Direction d : Iterate.directions)
            if (state.getValue(FACING_TO_PROPERTY_MAP.get(d))) {
                firstFound = d;
                break;
            }

        FluidTransportBehaviour.cacheFlows(world, pos);
        world.setBlockAndUpdate(pos, pipe.get().updateBlockState(equivalentPipe, firstFound, null, world, pos));
        FluidTransportBehaviour.loadFlows(world, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity be) {
        return ItemRequirement.of(pipe.get().defaultBlockState(), be);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(pipe.get());
    }
}
