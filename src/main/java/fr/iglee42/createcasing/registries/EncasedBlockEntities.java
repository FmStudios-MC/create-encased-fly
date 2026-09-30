package fr.iglee42.createcasing.registries;

import com.zurrtum.create.AllTransfer;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.CachedFluidInventoryBehaviour;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.zurrtum.create.foundation.block.IBE;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.blockEntities.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

/**
 * Block entity types.
 * <p>
 * Upstream registered one type per block kind (press, mixer, depot, ...) only because its blocks
 * could not join Create's types, and then patched Create's renderers, transfer lookups and block
 * checks to accept them. Here every block joins the type its block class asks for through
 * {@link FabricBlockEntityType#addValidBlock}, so most blocks simply use Create Fly's types
 * (and with them its renderers, visuals, client behaviours and fluid/item lookups). The types
 * below are the ones with no Create counterpart, or whose Create class fixes its type.
 * <p>
 * The types are created with no blocks; {@link #register()} adds every block of the mod.
 */
public class EncasedBlockEntities {

    public static final BlockEntityType<CreativeCogwheelBlockEntity> CREATIVE_COGWHEEL =
        register("creative_cogwheel", CreativeCogwheelBlockEntity::new);
    public static final BlockEntityType<AutoClutchBlockEntity> AUTOMATIC_CLUTCH =
        register("automatic_clutch", AutoClutchBlockEntity::new);
    public static final BlockEntityType<ConfigurableGearboxBlockEntity> CONFIGURABLE_GEARBOX =
        register("configurable_gearbox", ConfigurableGearboxBlockEntity::new);
    public static final BlockEntityType<WoodenShaftBlockEntity> WOODEN_SHAFT =
        register("wooden_shaft", WoodenShaftBlockEntity::new);
    public static final BlockEntityType<GlassShaftBlockEntity> GLASS_SHAFT =
        register("glass_shaft", GlassShaftBlockEntity::new);
    public static final BlockEntityType<CustomShaftBlockEntity> CUSTOM_SHAFT =
        register("custom_shaft", CustomShaftBlockEntity::new);
    public static final BlockEntityType<CustomBracketedKineticBlockEntity> WOODEN_COGWHEELS =
        register("wooden_cogwheels", CustomBracketedKineticBlockEntity::new);
    public static final BlockEntityType<CustomBracketedKineticBlockEntity> CUSTOM_COGWHEELS =
        register("custom_cogwheels", CustomBracketedKineticBlockEntity::new);
    public static final BlockEntityType<CustomEncasedShaftBlockEntity> ENCASED_CUSTOM_SHAFT =
        register("encased_custom_shaft", CustomEncasedShaftBlockEntity::new);
    public static final BlockEntityType<SimpleKineticBlockEntity> ENCASED_CUSTOM_COGWHEEL =
        register("encased_custom_cogwheel", SimpleKineticBlockEntity::new);
    public static final BlockEntityType<SimpleKineticBlockEntity> ENCASED_CUSTOM_LARGE_COGWHEEL =
        register("encased_custom_large_cogwheel", SimpleKineticBlockEntity::new);
    /**
     * Shared by the mod's tanks, as upstream did: tanks join into multiblocks by block entity
     * type, so andesite, brass and zinc tanks merge with each other but not with Create's.
     */
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK =
        register("fluid_tank", FluidTankBlockEntity::new);

    @FunctionalInterface
    public interface Factory<T extends BlockEntity> {
        T create(BlockEntityType<?> type, BlockPos pos, BlockState state);
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> register(String name, Factory<? extends T> factory) {
        BlockEntityType<?>[] self = new BlockEntityType<?>[1];
        BlockEntityType<T> type = new BlockEntityType<>((pos, state) -> factory.create(self[0], pos, state), Set.of());
        self[0] = type;
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateCasing.asResource(name), type);
    }

    /** Adds every block of the mod to the type its block class returns, Create's or ours. */
    public static void register() {
        for (Block block : EncasedBlocks.ALL) {
            if (block instanceof IBE<?> ibe)
                addValidBlock(ibe.getBlockEntityType(), block);
        }
        registerTransfer();
    }

    /**
     * Fabric makes every type's valid block set a {@link HashSet}, but addons that add blocks to
     * Create's types by assigning an immutable copy (Copycats+ uses {@code Set.copyOf}) undo that.
     */
    private static void addValidBlock(BlockEntityType<?> type, Block block) {
        if (!(type.validBlocks instanceof HashSet))
            type.validBlocks = new HashSet<>(type.validBlocks);
        ((FabricBlockEntityType) type).addValidBlock(block);
    }

    /**
     * Create Fly exposes tanks to Fabric's transfer API per block entity type; the mod's tank type
     * gets the same registration as Create's {@code FLUID_TANK} in {@code AllTransfer}.
     */
    private static void registerTransfer() {
        if (AllTransfer.DISABLE)
            return;
        BlockEntityBehaviour.add(FLUID_TANK, be -> new CachedFluidInventoryBehaviour<>(be, tank -> {
            if (tank.fluidCapability == null)
                tank.refreshCapability();
            return tank.fluidCapability;
        }));
        FluidStorage.SIDED.registerForBlockEntity(CachedFluidInventoryBehaviour::get, FLUID_TANK);
    }
}
