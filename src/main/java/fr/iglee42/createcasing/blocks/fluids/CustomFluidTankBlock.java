package fr.iglee42.createcasing.blocks.fluids;

import com.zurrtum.create.content.fluids.tank.FluidTankBlock;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import fr.iglee42.createcasing.registries.EncasedBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** A tank of the mod's own block entity type, so that it does not merge with Create's copper tanks. */
public class CustomFluidTankBlock extends FluidTankBlock {
    public CustomFluidTankBlock(Properties properties) {
        super(properties, false);
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return EncasedBlockEntities.FLUID_TANK;
    }
}
