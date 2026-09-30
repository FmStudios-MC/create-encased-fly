package fr.iglee42.createcasing.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.logistics.packager.PackagerBlock;
import fr.iglee42.createcasing.utils.BaseBlockChecks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Packagers placed against portable storage interfaces of every casing face them like Create's. */
@Mixin(PackagerBlock.class)
public class PackagerBlockMixin {
    @WrapOperation(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private boolean createcasing$variants(BlockState state, Object target, Operation<Boolean> original) {
        return BaseBlockChecks.is(state, target, original.call(state, target));
    }
}
