package fr.iglee42.createcasing.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import fr.iglee42.createcasing.utils.SneakUse;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** See {@link SneakUse}. */
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSecondaryUseActive()Z"))
    private boolean createcasing$shaftOnConfigurableGearbox(ServerPlayer player, Operation<Boolean> original, @Local(argsOnly = true) Level level,
                                                           @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) BlockHitResult hit) {
        return original.call(player) && !SneakUse.bypassesSneak(level, stack, hit);
    }
}
