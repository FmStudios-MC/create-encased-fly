package fr.iglee42.createcasing.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.decoration.steamWhistle.WhistleExtenderBlock;
import fr.iglee42.createcasing.utils.BaseBlockChecks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Whistle extensions stand on whistles of every material and grow with whistles of every material. */
@Mixin(WhistleExtenderBlock.class)
public class WhistleExtenderBlockMixin {
    @WrapOperation(method = "canSurvive", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private boolean createcasing$variants(BlockState state, Object target, Operation<Boolean> original) {
        return BaseBlockChecks.is(state, target, original.call(state, target));
    }

    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean createcasing$whistleItems(ItemStack stack, Object target, Operation<Boolean> original) {
        return BaseBlockChecks.is(stack, target, original.call(stack, target));
    }
}
