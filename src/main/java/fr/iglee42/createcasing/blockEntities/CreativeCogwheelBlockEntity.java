package fr.iglee42.createcasing.blockEntities;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerKineticScrollValueBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import fr.iglee42.createcasing.blocks.CreativeCogwheelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * A creative motor in cogwheel form. The speed box is split like Create Fly's creative motor:
 * the value lives here, the value box is added on the client ({@code EncasedClientBehaviours}).
 */
public class CreativeCogwheelBlockEntity extends GeneratingKineticBlockEntity {
    public static final int DEFAULT_SPEED = 16;
    public static final int MAX_SPEED = 256;

    public ServerScrollValueBehaviour generatedSpeed;

    public CreativeCogwheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        generatedSpeed = new ServerKineticScrollValueBehaviour(this);
        generatedSpeed.between(-MAX_SPEED, MAX_SPEED);
        generatedSpeed.setValue(DEFAULT_SPEED);
        generatedSpeed.withCallback(i -> updateGeneratedRotation());
        behaviours.add(generatedSpeed);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        if (!(getBlockState().getBlock() instanceof CreativeCogwheelBlock))
            return 0;
        return generatedSpeed.getValue();
    }
}
