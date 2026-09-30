package fr.iglee42.createcasing.mixins;

import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Belts take the casings of every casing set that makes belt casings, where Create checks for its own two. */
@Mixin(BeltBlock.class)
public abstract class BeltBlockMixin {
    @Shadow
    public abstract void updateCoverProperty(LevelReader world, BlockPos pos, BlockState state);

    @Inject(method = "useItemOn", at = @At(value = "FIELD", target = "Lcom/zurrtum/create/AllItems;BRASS_CASING:Lnet/minecraft/world/item/BlockItem;"), cancellable = true)
    private void createcasing$otherCasings(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                          BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        for (CasingSet set : CasingSets.getSets()) {
            Block casing = set.getCasing();
            BeltBlockEntity.CasingType type = set.getBeltCasingType();
            if (casing == null || type == null || !stack.is(casing.asItem()))
                continue;
            if (level.getBlockEntity(pos) instanceof BeltBlockEntity be)
                be.setCasingType(type);
            updateCoverProperty(level, pos, level.getBlockState(pos));
            SoundType soundType = casing.defaultBlockState().getSoundType();
            level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
    }
}
