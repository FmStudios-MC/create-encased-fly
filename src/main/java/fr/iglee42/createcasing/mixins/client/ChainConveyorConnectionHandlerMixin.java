package fr.iglee42.createcasing.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;
import fr.iglee42.createcasing.utils.BaseBlockChecks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Chains can be attached to chain conveyors of every casing. */
@Mixin(ChainConveyorConnectionHandler.class)
public class ChainConveyorConnectionHandlerMixin {
    @WrapOperation(method = "onItemUsedOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean createcasing$variants(BlockState state, Object target, Operation<Boolean> original) {
        return BaseBlockChecks.is(state, target, original.call(state, target));
    }
}
