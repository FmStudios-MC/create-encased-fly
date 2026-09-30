package fr.iglee42.createcasing.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer.BracketedKineticRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual.rotationOffset;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual.shouldOffset;

/**
 * Create Fly's {@code BracketedKineticBlockEntityRenderer} for the mod's shafts and cogwheels: small
 * ones turn their block model, large cogwheels their own shaftless partial plus Create's shaft
 * (upstream's {@code WoodenCogwheelBlockEntityRenderer}).
 */
public class TransmissionRenderer implements BlockEntityRenderer<KineticBlockEntity, BracketedKineticRenderState> {
    public TransmissionRenderer(Context context) {
    }

    @Override
    public BracketedKineticRenderState createRenderState() {
        return new BracketedKineticRenderState();
    }

    @Override
    public void extractRenderState(KineticBlockEntity be, BracketedKineticRenderState state, float tickProgress, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        BlockState blockState = state.blockState;
        Axis axis = getRotationAxisOf(blockState);
        Direction direction = axis.getPositive();
        int color = getTintColor(be);
        float progress = getProgress(be, level);
        float offset;
        SuperByteBuffer model;
        state.shaft = null;
        state.shaftAngle = null;
        if (ICogWheel.isLargeCog(blockState)) {
            float shaftOffset;
            if (shouldOffset(axis, state.blockPos)) {
                offset = shaftOffset = 22.5f;
            } else {
                offset = 11.25f;
                shaftOffset = 0;
            }
            model = CachedBuffers.partialFacingVertical(TransmissionModels.shaftlessLargeCog(blockState.getBlock()), blockState, direction);
            state.shaftAngle = getRotateAngle(progress, shaftOffset, direction);
            state.shaft = CachedBuffers.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, blockState, direction)
                .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
        } else {
            offset = rotationOffset(blockState, axis, state.blockPos);
            model = CachedBuffers.block(KINETIC_BLOCK, blockState);
        }
        state.angle = getRotateAngle(progress, offset, direction);
        state.model = model.cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
    }

    @Override
    public void submit(BracketedKineticRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.shaft != null) {
            matrices.pushPose();
            if (state.shaftAngle != null)
                matrices.rotateAround(state.shaftAngle, 0.5f, 0.5f, 0.5f);
            state.shaft.submit(matrices, queue);
            matrices.popPose();
        }
        if (state.angle != null)
            matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
        state.model.submit(matrices, queue);
    }
}
