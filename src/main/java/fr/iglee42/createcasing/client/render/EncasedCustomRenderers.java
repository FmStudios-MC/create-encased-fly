package fr.iglee42.createcasing.client.render;

import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renderers for casings around the mod's shafts and cogwheels: Create Fly's encased shaft and
 * encased cogwheel renderers, drawing the inner shaft or cogwheel in its own material (upstream's
 * {@code CustomEncasedShaftRenderer} and {@code EncasedCustomCogRenderer}).
 */
public final class EncasedCustomRenderers {
    private EncasedCustomRenderers() {
    }

    public static class Shaft extends KineticBlockEntityRenderer<KineticBlockEntity> {
        public Shaft(Context context) {
            super(context);
        }

        // cutout, not the casing's layer: the inner shaft may be the glass one
        @Override
        protected RenderType getRenderType(KineticBlockEntity be, BlockState state) {
            return RenderType.cutout();
        }

        @Override
        protected SuperByteBuffer getRotatedModel(KineticBlockEntity be, BlockState state) {
            return CachedBuffers.partialFacingVertical(TransmissionModels.shaft(state.getBlock()), state,
                Direction.fromAxisAndDirection(getRotationAxisOf(be), AxisDirection.POSITIVE));
        }
    }
}
