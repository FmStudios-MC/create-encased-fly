package fr.iglee42.createcasing.blockEntities;

import com.zurrtum.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Create Fly's {@code GearboxBlockEntity} with a free type; Create Fly's fixes it to {@code GEARBOX}. */
public class ConfigurableGearboxBlockEntity extends DirectionalShaftHalvesBlockEntity {
    public ConfigurableGearboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean isNoisy() {
        return false;
    }
}
