package fr.iglee42.createcasing.mixins;

import com.zurrtum.create.content.kinetics.chainDrive.ChainDriveBlock;
import fr.iglee42.createcasing.blocks.customs.CasingTyped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Chain drives only link up with chain drives of the same casing. Upstream re-implemented the
 * "no chain drive next to me" branch of {@code updateShape}; here that branch is reached by
 * asking again with air as the neighbour.
 */
@Mixin(ChainDriveBlock.class)
public abstract class ChainDriveBlockMixin {
    @Shadow
    public abstract BlockState updateShape(BlockState stateIn, LevelReader worldIn, ScheduledTickAccess tickView, BlockPos currentPos,
                                           Direction face, BlockPos facingPos, BlockState neighbour, RandomSource random);

    @Unique
    private static String createcasing$casing(Block block) {
        return block instanceof CasingTyped typed ? typed.getCasingType() : "andesite";
    }

    @Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
    private void createcasing$onlySameCasing(BlockState stateIn, LevelReader worldIn, ScheduledTickAccess tickView, BlockPos currentPos,
                                             Direction face, BlockPos facingPos, BlockState neighbour, RandomSource random,
                                             CallbackInfoReturnable<BlockState> cir) {
        if (neighbour.getBlock() instanceof ChainDriveBlock
            && !createcasing$casing(stateIn.getBlock()).equals(createcasing$casing(neighbour.getBlock())))
            cir.setReturnValue(updateShape(stateIn, worldIn, tickView, currentPos, face, facingPos, Blocks.AIR.defaultBlockState(), random));
    }

    @Inject(method = "areBlocksConnected", at = @At("HEAD"), cancellable = true)
    private static void createcasing$connectedOnlySameCasing(BlockState state, BlockState other, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (!createcasing$casing(state.getBlock()).equals(createcasing$casing(other.getBlock())))
            cir.setReturnValue(false);
    }
}
