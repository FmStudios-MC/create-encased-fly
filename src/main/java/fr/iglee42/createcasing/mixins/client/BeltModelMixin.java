package fr.iglee42.createcasing.mixins.client;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import com.zurrtum.create.client.foundation.model.BakedModelHelper;
import com.zurrtum.create.client.infrastructure.model.BeltModel;
import com.zurrtum.create.client.infrastructure.model.WrapperBlockStateModel;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.client.CasingSetVisuals;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Belts with one of the mod's casings: Create Fly models a cased belt with the brass casing, so
 * the brass casing sprite is shifted onto the set's belt casing texture and the set's own cover is
 * added, as upstream's {@code BeltModelMixin} did for Create's baked model.
 */
@Mixin(BeltModel.class)
public abstract class BeltModelMixin extends WrapperBlockStateModel {
    @Unique
    private static @Nullable CasingSetVisuals createcasing$visuals(BlockAndTintGetter world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof BeltBlockEntity be) || be.casing == null)
            return null;
        for (CasingSet set : CasingSets.getSets())
            if (set.getBeltCasingType() == be.casing)
                return CasingSetVisuals.of(set);
        return null;
    }

    @Inject(method = "particleMaterialWithInfo", at = @At("HEAD"), cancellable = true)
    private void createcasing$particle(BlockAndTintGetter world, BlockPos pos, BlockState state, CallbackInfoReturnable<Material.Baked> cir) {
        CasingSetVisuals visuals = createcasing$visuals(world, pos);
        if (visuals == null || visuals.belt() == null)
            return;
        TextureAtlasSprite sprite = visuals.casing() != null ? visuals.casing().getOriginal() : visuals.belt().getTarget();
        cir.setReturnValue(new Material.Baked(sprite, false));
    }

    @Inject(method = "addPartsWithInfo", at = @At("HEAD"), cancellable = true)
    private void createcasing$parts(BlockAndTintGetter world, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts, CallbackInfo ci) {
        CasingSetVisuals visuals = createcasing$visuals(world, pos);
        if (visuals == null || visuals.belt() == null)
            return;
        if (world.getBlockEntity(pos) instanceof BeltBlockEntity be && be.covered) {
            boolean alongX = state.getValue(BeltBlock.HORIZONTAL_FACING).getAxis() == Direction.Axis.X;
            (alongX ? visuals.beltCoverX() : visuals.beltCoverZ()).get().collectParts(random, parts);
        }
        SpriteShiftEntry shift = visuals.belt();
        List<BlockStateModelPart> modelParts = new ObjectArrayList<>();
        model.collectParts(random, modelParts);
        for (BlockStateModelPart part : modelParts)
            parts.add(createcasing$shift(shift, part));
        ci.cancel();
    }

    @Unique
    private static BlockStateModelPart createcasing$shift(SpriteShiftEntry shift, BlockStateModelPart part) {
        QuadCollection.Builder builder = new QuadCollection.Builder();
        for (BakedQuad quad : part.getQuads(null))
            builder.addUnculledFace(createcasing$shift(shift, quad));
        for (Direction direction : Iterate.directions)
            for (BakedQuad quad : part.getQuads(direction))
                builder.addCulledFace(direction, createcasing$shift(shift, quad));
        return new SimpleModelWrapper(builder.build(), part.useAmbientOcclusion(), part.particleMaterial());
    }

    @Unique
    private static BakedQuad createcasing$shift(SpriteShiftEntry shift, BakedQuad quad) {
        BakedQuad.MaterialInfo info = quad.materialInfo();
        if (info.sprite() != shift.getOriginal())
            return quad;
        return BakedModelHelper.replaceBakedQuadUV(quad, createcasing$uv(shift, quad.packedUV0()), createcasing$uv(shift, quad.packedUV1()),
            createcasing$uv(shift, quad.packedUV2()), createcasing$uv(shift, quad.packedUV3()), info);
    }

    @Unique
    private static long createcasing$uv(SpriteShiftEntry shift, long packedUv) {
        return UVPair.pack(shift.getTargetU(UVPair.unpackU(packedUv)), shift.getTargetV(UVPair.unpackV(packedUv)));
    }
}
