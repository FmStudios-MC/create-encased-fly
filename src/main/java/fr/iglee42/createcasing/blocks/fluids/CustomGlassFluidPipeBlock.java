package fr.iglee42.createcasing.blocks.fluids;

import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import com.zurrtum.create.content.fluids.pipes.GlassFluidPipeBlock;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Map;

/** A glass pipe that turns back into the regular pipe of its own set. */
public class CustomGlassFluidPipeBlock extends GlassFluidPipeBlock {
    public CustomGlassFluidPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState toRegularPipe(LevelAccessor world, BlockPos pos, BlockState state) {
        FluidPipeBlock pipe = regularPipeOf(state);
        Direction side = Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(AXIS));
        Map<Direction, BooleanProperty> facingToPropertyMap = FluidPipeBlock.PROPERTY_BY_DIRECTION;
        return pipe.updateBlockState(pipe.defaultBlockState()
            .setValue(facingToPropertyMap.get(side), true)
            .setValue(facingToPropertyMap.get(side.getOpposite()), true), side, null, world, pos);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(regularPipeOf(state));
    }

    private static FluidPipeBlock regularPipeOf(BlockState state) {
        for (FluidSet set : FluidSets.getSets()) {
            if (set.get(FluidSet.Part.FLUID_PIPE) instanceof FluidPipeBlock pipe && set.isInSet(state.getBlock()))
                return pipe;
        }
        return (FluidPipeBlock) FluidSets.COPPER.get(FluidSet.Part.FLUID_PIPE);
    }
}
