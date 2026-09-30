package fr.iglee42.createcasing.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer.BracketedKineticRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual.shouldOffset;

/**
 * Renderers for casings around the mod's shafts and cogwheels: Create Fly's encased shaft and
 * encased cogwheel renderers, drawing the inner shaft or cogwheel in its own material (upstream's
 * {@code CustomEncasedShaftRenderer} and {@code EncasedCustomCogRenderer}).
 */
public final class EncasedCustomRenderers {
    private EncasedCustomRenderers() {
    }

    public static class Shaft implements BlockEntityRenderer<KineticBlockEntity, SingleKineticRenderState> {
        public Shaft(Context context) {
        }

        @Override
        public SingleKineticRenderState createRenderState() {
            return new SingleKineticRenderState();
        }

        @Override
        public void extractRenderState(KineticBlockEntity be, SingleKineticRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
            Level level = SmartBlockEntityRenderer.extractBase(be, state, breakProgress);
            Axis axis = getRotationAxisOf(state.blockState);
            state.model = CachedBuffers.partialFacingVertical(TransmissionModels.shaft(state.blockState.getBlock()), state.blockState, axis.getPositive())
                .cardinalLighting(level).light(state.lightCoords).color(getTintColor(be)).extractRenderState();
            state.angle = getRotateAngleWithoutBeOffset(axis, be, state, level);
        }

        @Override
        public void submit(SingleKineticRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
            state.submit(matrices, queue);
        }
    }
}
