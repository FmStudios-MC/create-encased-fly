package fr.iglee42.createcasing.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer.BracketedKineticRenderState;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Create Fly's {@code BracketedKineticBlockEntityRenderer} for the mod's shafts and cogwheels: small
 * ones turn their block model, large cogwheels their own shaftless partial plus Create's shaft
 * (upstream's {@code WoodenCogwheelBlockEntityRenderer}).
 */
public class TransmissionRenderer extends KineticBlockEntityRenderer<KineticBlockEntity, BracketedKineticRenderState> {
    public TransmissionRenderer(Context context) {
        super(context);
    }

    @Override
    public BracketedKineticRenderState createRenderState() {
        return new BracketedKineticRenderState();
    }

    @Override
    public void extractRenderState(KineticBlockEntity be, BracketedKineticRenderState state, float tickProgress, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        state.large = ICogWheel.isLargeCog(be.getBlockState());
        super.extractRenderState(be, state, tickProgress, cameraPos, crumblingOverlay);
        state.shaft = null;
        if (state.support || !state.large)
            return;
        state.shaft = CachedBuffers.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, state.blockState, state.direction);
        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float offset = BracketedKineticBlockEntityRenderer.getShaftAngleOffset(state.axis, state.blockPos);
        state.shaftAngle = ((time * be.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity be, BracketedKineticRenderState state) {
        if (state.large)
            return CachedBuffers.partialFacingVertical(TransmissionModels.shaftlessLargeCog(state.blockState.getBlock()), state.blockState, state.direction);
        return super.getRotatedModel(be, state);
    }
}
