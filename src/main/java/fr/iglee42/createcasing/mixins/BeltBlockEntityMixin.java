package fr.iglee42.createcasing.mixins;

import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Removing one of the mod's belt casings breaks with that casing's particles, not brass casing's. */
@Mixin(BeltBlockEntity.class)
public class BeltBlockEntityMixin {
    @Shadow
    public BeltBlockEntity.CasingType casing;

    @ModifyArg(method = "setCasingType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;levelEvent(ILnet/minecraft/core/BlockPos;I)V"), index = 2)
    private int createcasing$casingParticles(int blockId) {
        for (CasingSet set : CasingSets.getSets()) {
            if (set.getBeltCasingType() == casing && set.getCasing() != null)
                return Block.getId(set.getCasing().defaultBlockState());
        }
        return blockId;
    }
}
