package fr.iglee42.createcasing.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Create Fly's {@code BracketedKineticBlockEntityRenderer} for the mod's shafts and cogwheels: small
 * ones turn their block model, large cogwheels their own shaftless partial plus Create's shaft
 * (upstream's {@code WoodenCogwheelBlockEntityRenderer}).
 */
public class TransmissionRenderer extends KineticBlockEntityRenderer<KineticBlockEntity> {
    public TransmissionRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(KineticBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState state = be.getBlockState();
        if (VisualizationManager.supportsVisualization(be.getLevel()) || !ICogWheel.isLargeCog(state))
            return;
        Axis axis = getRotationAxisOf(be);
        SuperByteBuffer shaft = CachedBuffers.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, state,
            Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE));
        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float offset = BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, be.getBlockPos());
        float angle = ((time * be.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
        kineticRotationTransform(shaft, be, axis, angle, light).renderInto(ms, buffer.getBuffer(getRenderType(be, state)));
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity be, BlockState state) {
        if (ICogWheel.isLargeCog(state))
            return CachedBuffers.partialFacingVertical(TransmissionModels.shaftlessLargeCog(state.getBlock()), state,
                Direction.fromAxisAndDirection(getRotationAxisOf(be), AxisDirection.POSITIVE));
        return super.getRotatedModel(be, state);
    }
}
