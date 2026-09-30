package fr.iglee42.createcasing.blocks.fluids;

import com.zurrtum.create.AllAdvancements;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.fluids.FluidPropagator;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour;
import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import com.zurrtum.create.content.fluids.pipes.GlassFluidPipeBlock;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

/** A fluid pipe that turns into the glass pipe of its own set when wrenched. */
public class CustomFluidPipeBlock extends FluidPipeBlock {
    public CustomFluidPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (tryRemoveBracket(context))
            return InteractionResult.SUCCESS;

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();

        Axis axis = FluidPropagator.getStraightPipeAxis(state);
        if (axis == null) {
            Vec3 clickLocation = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            double closest = Float.MAX_VALUE;
            Direction argClosest = Direction.UP;
            for (Direction direction : Iterate.directions) {
                if (clickedFace.getAxis() == direction.getAxis())
                    continue;
                Vec3 centerOf = Vec3.atCenterOf(direction.getUnitVec3i());
                double distance = centerOf.distanceToSqr(clickLocation);
                if (distance < closest) {
                    closest = distance;
                    argClosest = direction;
                }
            }
            axis = argClosest.getAxis();
        }

        if (clickedFace.getAxis() == axis)
            return InteractionResult.PASS;
        if (!world.isClientSide()) {
            withBlockEntityDo(world, pos, fpte -> fpte.getBehaviour(FluidTransportBehaviour.TYPE).interfaces.values().stream()
                .filter(pc -> pc != null && pc.hasFlow()).findAny()
                .ifPresent($ -> {
                    if (context.getPlayer() instanceof ServerPlayer player)
                        AllAdvancements.GLASS_PIPE.trigger(player);
                }));

            FluidTransportBehaviour.cacheFlows(world, pos);
            world.setBlockAndUpdate(pos, glassPipeOf(state.getBlock()).defaultBlockState()
                .setValue(GlassFluidPipeBlock.AXIS, axis)
                .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED)));
            FluidTransportBehaviour.loadFlows(world, pos);
        }
        return InteractionResult.SUCCESS;
    }

    private static Block glassPipeOf(Block pipe) {
        for (FluidSet set : FluidSets.getSets()) {
            Block glass = set.get(FluidSet.Part.GLASS_FLUID_PIPE);
            if (glass != null && set.isInSet(pipe))
                return glass;
        }
        return FluidSets.COPPER.get(FluidSet.Part.GLASS_FLUID_PIPE);
    }
}
