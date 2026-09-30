package fr.iglee42.createcasing.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import fr.iglee42.createcasing.utils.SneakUse;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** The client half of {@code ServerPlayerGameModeMixin}. */
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @WrapOperation(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSecondaryUseActive()Z"))
    private boolean createcasing$shaftOnConfigurableGearbox(LocalPlayer player, Operation<Boolean> original, @Local(argsOnly = true) InteractionHand hand,
                                                           @Local(argsOnly = true) BlockHitResult hit) {
        return original.call(player) && !SneakUse.bypassesSneak(player.level(), player.getItemInHand(hand), hit);
    }
}
