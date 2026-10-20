package fr.iglee42.createcasing.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.decoration.steamWhistle.WhistleExtenderBlock;
import fr.iglee42.createcasing.utils.BaseBlockChecks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Whistle extensions stand on whistles of every material and grow with whistles of every material. */
@Mixin(WhistleExtenderBlock.class)
public class WhistleExtenderBlockMixin {
    @WrapOperation(method = "canSurvive", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean createcasing$variants(BlockState state, Block target, Operation<Boolean> original) {
        return BaseBlockChecks.is(state, target, original.call(state, target));
    }

    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean createcasing$whistleItems(ItemStack stack, Item target, Operation<Boolean> original) {
        return BaseBlockChecks.is(stack, target, original.call(stack, target));
    }
}
