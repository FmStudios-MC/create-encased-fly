package fr.iglee42.createcasing.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fr.iglee42.createcasing.utils.BaseBlockChecks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Mechanical arms can use depots, deployers and saws of every casing. */
@Mixin(targets = {"com.zurrtum.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes$DepotType", "com.zurrtum.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes$DeployerType", "com.zurrtum.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes$SawType"})
public class ArmInteractionPointTypesMixin {
    @WrapOperation(method = "canCreatePoint", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private boolean createcasing$variants(BlockState state, Object target, Operation<Boolean> original) {
        return BaseBlockChecks.is(state, target, original.call(state, target));
    }
}
