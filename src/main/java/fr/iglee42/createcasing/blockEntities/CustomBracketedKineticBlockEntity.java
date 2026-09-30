package fr.iglee42.createcasing.blockEntities;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.api.contraption.transformable.TransformableBlockEntity;
import com.zurrtum.create.content.contraptions.StructureTransform;
import com.zurrtum.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Create Fly's {@code BracketedKineticBlockEntity} with a free block entity type; Create Fly's
 * constructor fixes it to {@code BRACKETED_KINETIC}, while the mod's shafts and cogwheels need
 * their own types for their renderers.
 */
public class CustomBracketedKineticBlockEntity extends SimpleKineticBlockEntity implements TransformableBlockEntity {
    public CustomBracketedKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        behaviours.add(new BracketedBlockEntityBehaviour(this, state -> state.getBlock() instanceof AbstractSimpleShaftBlock));
        super.addBehaviours(behaviours);
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        BracketedBlockEntityBehaviour bracketBehaviour = getBehaviour(BracketedBlockEntityBehaviour.TYPE);
        if (bracketBehaviour != null)
            bracketBehaviour.transformBracket(transform);
    }
}
