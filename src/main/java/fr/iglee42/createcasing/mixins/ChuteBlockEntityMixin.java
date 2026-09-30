package fr.iglee42.createcasing.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.logistics.chute.ChuteBlockEntity;
import fr.iglee42.createcasing.utils.BaseBlockChecks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Fans of every casing push and pull items through chutes. */
@Mixin(ChuteBlockEntity.class)
public class ChuteBlockEntityMixin {
    @WrapOperation(method = {"calculatePull", "calculatePush"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private boolean createcasing$variants(BlockState state, Object target, Operation<Boolean> original) {
        return BaseBlockChecks.is(state, target, original.call(state, target));
    }
}
