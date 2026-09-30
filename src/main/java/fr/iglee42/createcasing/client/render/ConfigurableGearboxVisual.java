package fr.iglee42.createcasing.client.render;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.instance.AbstractInstance;
import com.zurrtum.create.client.flywheel.lib.instance.FlatLit;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.foundation.render.AllInstanceTypes;
import fr.iglee42.createcasing.blockEntities.ConfigurableGearboxBlockEntity;
import fr.iglee42.createcasing.blocks.ConfigurableGearboxBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/** Create Fly's {@code GearboxVisual}, with a half shaft on each open face instead of the four around the axis. */
public class ConfigurableGearboxVisual extends KineticBlockEntityVisual<ConfigurableGearboxBlockEntity> {
    protected final EnumMap<Direction, RotatingInstance> keys = new EnumMap<>(Direction.class);
    protected @Nullable Direction sourceFacing;

    public ConfigurableGearboxVisual(VisualizationContext context, ConfigurableGearboxBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        updateSourceFacing();
        var instancer = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF));
        for (Direction direction : Iterate.directions) {
            if (!blockState.getValue(ConfigurableGearboxBlock.getPropertyByDirection(direction)))
                continue;
            RotatingInstance instance = instancer.createInstance();
            instance.setup(blockEntity, direction.getAxis(), getSpeed(direction)).setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, direction).setChanged();
            keys.put(direction, instance);
        }
    }

    private float getSpeed(Direction direction) {
        float speed = blockEntity.getSpeed();
        if (speed != 0 && sourceFacing != null) {
            if (sourceFacing.getAxis() == direction.getAxis())
                speed *= sourceFacing == direction ? 1 : -1;
            else if (sourceFacing.getAxisDirection() == direction.getAxisDirection())
                speed *= -1;
        }
        return speed;
    }

    protected void updateSourceFacing() {
        if (blockEntity.hasSource()) {
            BlockPos source = blockEntity.source.subtract(pos);
            sourceFacing = Direction.getApproximateNearest(source.getX(), source.getY(), source.getZ());
        } else {
            sourceFacing = null;
        }
    }

    @Override
    public void update(float pt) {
        updateSourceFacing();
        for (Map.Entry<Direction, RotatingInstance> key : keys.entrySet())
            key.getValue().setup(blockEntity, key.getKey().getAxis(), getSpeed(key.getKey())).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(keys.values().toArray(FlatLit[]::new));
    }

    @Override
    protected void _delete() {
        keys.values().forEach(AbstractInstance::delete);
        keys.clear();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        keys.values().forEach(consumer);
    }
}
