package fr.iglee42.createcasing.blocks.fluids;

import com.zurrtum.create.content.decoration.steamWhistle.WhistleBlock;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** A steam whistle that grows when clicked with a whistle of its own set. */
public class CustomWhistleBlock extends WhistleBlock {
    public CustomWhistleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if (player == null)
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        for (FluidSet set : FluidSets.getSets()) {
            Block whistle = set.get(FluidSet.Part.WHISTLE);
            if (whistle == null || !set.generates(FluidSet.Part.WHISTLE) || !set.isInSet(state.getBlock()))
                continue;
            if (stack.is(whistle.asItem())) {
                incrementSize(level, pos);
                return InteractionResult.SUCCESS;
            }
            break;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}
