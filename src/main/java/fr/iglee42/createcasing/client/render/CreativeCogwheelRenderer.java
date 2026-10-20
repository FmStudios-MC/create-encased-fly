package fr.iglee42.createcasing.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import fr.iglee42.createcasing.blockEntities.CreativeCogwheelBlockEntity;
import fr.iglee42.createcasing.blocks.CreativeCogwheelBlock;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.state.BlockState;

/** Turns Create's shaftless cogwheel; the block model is the creative casing around it. */
public class CreativeCogwheelRenderer extends KineticBlockEntityRenderer<CreativeCogwheelBlockEntity> {
    public CreativeCogwheelRenderer(Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CreativeCogwheelBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, state,
            Direction.fromAxisAndDirection(state.getValue(CreativeCogwheelBlock.AXIS), AxisDirection.POSITIVE));
    }
}
