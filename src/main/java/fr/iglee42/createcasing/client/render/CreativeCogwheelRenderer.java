package fr.iglee42.createcasing.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.KineticRenderState;
import fr.iglee42.createcasing.blockEntities.CreativeCogwheelBlockEntity;
import fr.iglee42.createcasing.blocks.CreativeCogwheelBlock;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;

/** Turns Create's shaftless cogwheel; the block model is the creative casing around it. */
public class CreativeCogwheelRenderer extends KineticBlockEntityRenderer<CreativeCogwheelBlockEntity, KineticRenderState> {
    public CreativeCogwheelRenderer(Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CreativeCogwheelBlockEntity be, KineticRenderState state) {
        return CachedBuffers.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, state.blockState,
            Direction.fromAxisAndDirection(state.blockState.getValue(CreativeCogwheelBlock.AXIS), AxisDirection.POSITIVE));
    }
}
