package fr.iglee42.createcasing.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import fr.iglee42.createcasing.blockEntities.ConfigurableGearboxBlockEntity;
import fr.iglee42.createcasing.blocks.ConfigurableGearboxBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;

/** Draws a half shaft on each open face, turning like a gearbox's; only runs without Flywheel. */
public class ConfigurableGearboxRenderer extends KineticBlockEntityRenderer<ConfigurableGearboxBlockEntity> {
    public ConfigurableGearboxRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ConfigurableGearboxBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;
        BlockPos pos = be.getBlockPos();
        float time = AnimationTickHolder.getRenderTime(be.getLevel()) * be.getSpeed() * 3f / 10;
        for (Direction direction : Iterate.directions) {
            if (!be.getBlockState().getValue(ConfigurableGearboxBlock.getPropertyByDirection(direction)))
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
            angle = (angle + getRotationOffsetForPosition(be, pos, axis)) / 180f * (float) Math.PI;
            SuperByteBuffer shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), direction);
            kineticRotationTransform(shaft, be, axis, angle, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }
}
