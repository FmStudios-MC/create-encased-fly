package fr.iglee42.createcasing.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createcasing.client.EncasedPartialSwaps;
import fr.iglee42.createcasing.utils.BaseBlockChecks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Portable fluid interfaces of every material draw the fluid interface middle, and their own top. */
@Mixin(PortableStorageInterfaceRenderer.class)
public class PortableStorageInterfaceRendererMixin {
    @WrapOperation(method = {"getMiddleForState", "getTopForState"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean createcasing$variants(BlockState state, Object target, Operation<Boolean> original) {
        return BaseBlockChecks.is(state, target, original.call(state, target));
    }

    @ModifyReturnValue(method = "getTopForState", at = @At("RETURN"))
    private static PartialModel createcasing$top(PartialModel top, BlockState state) {
        return EncasedPartialSwaps.swap(top, state);
    }
}
