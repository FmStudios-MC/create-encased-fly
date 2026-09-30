package fr.iglee42.createcasing.blockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GlassShaftBlockEntity extends CustomShaftBlockEntity {
    public GlassShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        ShaftBreaking.tickGlass(this);
    }
}
