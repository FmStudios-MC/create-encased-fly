package fr.iglee42.createcasing.items;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.kinetics.base.IRotate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/** Create Fly's {@code VerticalGearboxItem}, which is tied to Create's gearbox, for any gearbox block. */
public class CustomVerticalGearboxItem extends BlockItem {
    public CustomVerticalGearboxItem(Properties builder, Block block) {
        super(block, builder);
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item item) {
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state) {
        Axis prefferedAxis = null;
        for (Direction side : Iterate.horizontalDirections) {
            BlockState blockState = world.getBlockState(pos.relative(side));
            if (blockState.getBlock() instanceof IRotate rotate) {
                if (rotate.hasShaftTowards(world, pos.relative(side), blockState, side.getOpposite())) {
                    if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                        prefferedAxis = null;
                        break;
                    }
                    prefferedAxis = side.getAxis();
                }
            }
        }

        Axis axis = prefferedAxis == null ? (player == null ? Axis.X : player.getDirection().getClockWise().getAxis())
            : prefferedAxis == Axis.X ? Axis.Z : Axis.X;
        world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AXIS, axis));
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }
}
