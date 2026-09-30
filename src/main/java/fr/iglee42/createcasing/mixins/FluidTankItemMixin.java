package fr.iglee42.createcasing.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.fluids.tank.FluidTankItem;
import fr.iglee42.createcasing.blocks.fluids.CustomFluidTankBlock;
import fr.iglee42.createcasing.registries.EncasedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Placing one of the mod's tanks against a tank grows it like Create's; the tank is found by the mod's own type. */
@Mixin(FluidTankItem.class)
public abstract class FluidTankItemMixin {
    @WrapOperation(method = "tryMultiPlace", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/api/connectivity/ConnectivityHandler;partAt(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity createcasing$ownTankType(BlockEntityType<?> type, BlockGetter level, BlockPos pos, Operation<BlockEntity> original) {
        if (((BlockItem) (Object) this).getBlock() instanceof CustomFluidTankBlock)
            type = EncasedBlockEntities.FLUID_TANK;
        return original.call(type, level, pos);
    }
}
