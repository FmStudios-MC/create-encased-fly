package fr.iglee42.createcasing.blocks.customs;

import com.zurrtum.create.content.kinetics.gearbox.GearboxBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;
import java.util.function.Supplier;

/** A gearbox whose horizontal form drops and picks its own casing's vertical gearbox item. */
public class CustomGearboxBlock extends GearboxBlock {
    private final Supplier<BlockItem> verticalItem;

    public CustomGearboxBlock(Properties properties, Supplier<BlockItem> verticalItem) {
        super(properties);
        this.verticalItem = verticalItem;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(AXIS).isVertical())
            return super.getDrops(state, builder);
        return List.of(new ItemStack(verticalItem.get()));
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        if (state.getValue(AXIS).isVertical())
            return super.getCloneItemStack(world, pos, state, includeData);
        return new ItemStack(verticalItem.get());
    }
}
