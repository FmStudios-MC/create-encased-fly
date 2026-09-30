package fr.iglee42.createcasing.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import fr.iglee42.createcasing.blockEntities.ConfigurableGearboxBlockEntity;
import fr.iglee42.createcasing.blocks.ConfigurableGearboxBlock;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
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
        Level level = be.getLevel();
        if (VisualizationManager.supportsVisualization(level))
            return;
        level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        BlockPos pos = state.blockPos;
        int color = getTintColor(be);
        float time = getProgress(be.getSpeed(), level);
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
            state.angles.add(getRotateAngle(angle + getRotationOffsetForPosition(be, pos, axis), axis.getPositive()));
            state.shafts.add(CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState, direction)
                .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState());
        }
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        for (int i = 0; i < state.shafts.size(); i++) {
            Quaternionf angle = state.angles.get(i);
            matrices.pushPose();
            if (angle != null)
                matrices.rotateAround(angle, 0.5f, 0.5f, 0.5f);
            state.shafts.get(i).submit(matrices, queue);
            matrices.popPose();
        }
    }

    public static class State extends BlockEntityRenderState {
        final List<SuperByteBufferRenderState> shafts = new ArrayList<>();
        final List<@Nullable Quaternionf> angles = new ArrayList<>();
    }
}
