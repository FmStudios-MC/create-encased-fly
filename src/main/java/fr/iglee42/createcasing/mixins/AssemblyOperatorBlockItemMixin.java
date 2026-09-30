package fr.iglee42.createcasing.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.processing.AssemblyOperatorBlockItem;
import fr.iglee42.createcasing.utils.BaseBlockChecks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Presses, mixers, deployers and spouts place onto depots of every casing like onto Create's. */
@Mixin(AssemblyOperatorBlockItem.class)
public class AssemblyOperatorBlockItemMixin {
    @WrapOperation(method = "operatesOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private boolean createcasing$variants(BlockState state, Object target, Operation<Boolean> original) {
        return BaseBlockChecks.is(state, target, original.call(state, target));
    }
}
