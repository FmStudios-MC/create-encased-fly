package fr.iglee42.createcasing.blockEntities;

import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import fr.iglee42.createcasing.blocks.shafts.EncasedCustomShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** An encased glass or wooden shaft keeps the breaking rules of the shaft inside. */
public class CustomEncasedShaftBlockEntity extends SimpleKineticBlockEntity {
    public CustomEncasedShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(getBlockState().getBlock() instanceof EncasedCustomShaftBlock encased))
            return;
        if (ShaftBreaking.isGlass(encased))
            ShaftBreaking.tickGlass(this);
        if (ShaftBreaking.isWooden(encased))
            ShaftBreaking.tickWooden(this);
    }
}
