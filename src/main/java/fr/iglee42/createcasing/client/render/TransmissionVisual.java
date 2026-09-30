package fr.iglee42.createcasing.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.visual.BlockEntityVisual;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.foundation.render.AllInstanceTypes;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Flywheel visuals of the mod's shafts and cogwheels (upstream's wooden shaft, custom shaft, glass
 * shaft and wooden cogwheel visuals, which only differed in the partial model).
 */
public final class TransmissionVisual {
    private TransmissionVisual() {
    }

    public static <T extends KineticBlockEntity> BlockEntityVisual<T> create(VisualizationContext context, T blockEntity, float partialTick) {
        BlockState state = blockEntity.getBlockState();
        if (ICogWheel.isLargeCog(state))
            return new LargeCogVisual<>(context, blockEntity, partialTick);
        if (ICogWheel.isSmallCog(state))
            return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, Models.chunkPartial(TransmissionModels.cog(state.getBlock())));
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, Models.chunkPartial(TransmissionModels.shaft(state.getBlock())));
    }

    /** Large cogs sometimes have to offset their teeth by 11.25 degrees in order to mesh properly. */
    public static class LargeCogVisual<T extends KineticBlockEntity> extends SingleAxisRotatingVisual<T> {
        protected final RotatingInstance additionalShaft;

        private LargeCogVisual(VisualizationContext context, T blockEntity, float partialTick) {
            super(context, blockEntity, partialTick, Models.chunkPartial(TransmissionModels.shaftlessLargeCog(blockEntity.getBlockState().getBlock())));
            Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);
            additionalShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.chunkPartial(AllPartialModels.COGWHEEL_SHAFT)).createInstance();
            additionalShaft.rotateToFace(axis).setup(blockEntity)
                .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos))
                .setPosition(getVisualPosition()).setChanged();
        }

        @Override
        public void update(float pt) {
            super.update(pt);
            additionalShaft.setup(blockEntity).setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos)).setChanged();
        }

        @Override
        public void updateLight(float partialTick) {
            super.updateLight(partialTick);
            relight(additionalShaft);
        }

        @Override
        protected void _delete() {
            super._delete();
            additionalShaft.delete();
        }

        @Override
        public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
            super.collectCrumblingInstances(consumer);
            consumer.accept(additionalShaft);
        }
    }
}
