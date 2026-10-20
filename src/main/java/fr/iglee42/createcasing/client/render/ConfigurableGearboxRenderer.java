package fr.iglee42.createcasing.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.catnip.theme.Color;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import fr.iglee42.createcasing.blockEntities.ConfigurableGearboxBlockEntity;
import fr.iglee42.createcasing.blocks.ConfigurableGearboxBlock;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;

/** Draws a half shaft on each open face, turning like a gearbox's; only runs without Flywheel. */
public class ConfigurableGearboxRenderer implements BlockEntityRenderer<ConfigurableGearboxBlockEntity, ConfigurableGearboxRenderer.State> {
    public ConfigurableGearboxRenderer(Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ConfigurableGearboxBlockEntity be, State state, float tickProgress, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        state.shafts.clear();
        state.angles.clear();
        state.axes.clear();
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;
        BlockEntityRenderState.extractBase(be, state, crumblingOverlay);
        BlockPos pos = state.blockPos;
        state.color = getColor(be);
        float time = AnimationTickHolder.getRenderTime(be.getLevel()) * be.getSpeed() * 3f / 10;
        for (Direction direction : Iterate.directions) {
            if (!state.blockState.getValue(ConfigurableGearboxBlock.getPropertyByDirection(direction)))
                continue;
            Axis axis = direction.getAxis();
            float angle = time % 360;
            if (be.getSpeed() != 0 && be.hasSource()) {
                BlockPos source = be.source.subtract(pos);
                Direction sourceFacing = Direction.getApproximateNearest(source.getX(), source.getY(), source.getZ());
                if (sourceFacing.getAxis() == direction.getAxis())
                    angle *= sourceFacing == direction ? 1 : -1;
                else if (sourceFacing.getAxisDirection() == direction.getAxisDirection())
                    angle *= -1;
            }
            state.angles.add((angle + getRotationOffsetForPosition(be, pos, axis)) / 180f * (float) Math.PI);
            state.axes.add(axis.getPositive());
            state.shafts.add(CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState, direction));
        }
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (!state.shafts.isEmpty())
            queue.submitCustomGeometry(matrices, RenderTypes.solidMovingBlock(), state);
    }

    public static class State extends BlockEntityRenderState implements SubmitNodeCollector.CustomGeometryRenderer {
        final List<SuperByteBuffer> shafts = new ArrayList<>();
        final List<Float> angles = new ArrayList<>();
        final List<Direction> axes = new ArrayList<>();
        Color color;

        @Override
        public void render(PoseStack.Pose matricesEntry, VertexConsumer vertexConsumer) {
            for (int i = 0; i < shafts.size(); i++) {
                SuperByteBuffer shaft = shafts.get(i);
                shaft.light(lightCoords);
                shaft.rotateCentered(angles.get(i), axes.get(i));
                shaft.color(color);
                shaft.renderInto(matricesEntry, vertexConsumer);
            }
        }
    }
}
