package fr.iglee42.createcasing.config;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.catnip.config.Builder;
import com.zurrtum.create.catnip.config.ConfigBase;
import com.zurrtum.create.catnip.config.DoubleRawValue;
import fr.iglee42.createcasing.registries.EncasedBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

/**
 * The mod's own stress values, used when {@code encasedBlocksUsesOwnKeys} is on (otherwise each
 * block uses its Create counterpart's value). Upstream gave the defaults in each Registrate
 * builder; here they come from the block's Create counterpart, with upstream's numbers.
 */
public class CCStress extends ConfigBase {
    // bump this version to reset configured values.
    private static final int VERSION = 2;

    private static final Map<Block, Double> IMPACT_BY_BASE = new IdentityHashMap<>();
    private static final Map<Block, Double> CAPACITY_BY_BASE = new IdentityHashMap<>();

    static {
        for (Block block : new Block[]{AllBlocks.SHAFT, AllBlocks.COGWHEEL, AllBlocks.LARGE_COGWHEEL, AllBlocks.ANDESITE_ENCASED_SHAFT,
            AllBlocks.ANDESITE_ENCASED_COGWHEEL, AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL, AllBlocks.GEARBOX, AllBlocks.ENCASED_CHAIN_DRIVE,
            AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT, AllBlocks.GEARSHIFT, AllBlocks.CLUTCH})
            IMPACT_BY_BASE.put(block, 0.0);
        IMPACT_BY_BASE.put(AllBlocks.CHAIN_CONVEYOR, 1.0);
        IMPACT_BY_BASE.put(AllBlocks.ENCASED_FAN, 2.0);
        IMPACT_BY_BASE.put(AllBlocks.MECHANICAL_MIXER, 4.0);
        IMPACT_BY_BASE.put(AllBlocks.DEPLOYER, 4.0);
        IMPACT_BY_BASE.put(AllBlocks.MECHANICAL_SAW, 4.0);
        IMPACT_BY_BASE.put(AllBlocks.MECHANICAL_DRILL, 4.0);
        IMPACT_BY_BASE.put(AllBlocks.MECHANICAL_PUMP, 4.0);
        IMPACT_BY_BASE.put(AllBlocks.HOSE_PULLEY, 4.0);
        IMPACT_BY_BASE.put(AllBlocks.MECHANICAL_PRESS, 8.0);
        CAPACITY_BY_BASE.put(AllBlocks.STEAM_ENGINE, 1024.0);
        CAPACITY_BY_BASE.put(AllBlocks.COPPER_VALVE_HANDLE, 8.0);
    }

    protected final Map<Identifier, DoubleRawValue> capacities = new HashMap<>();
    protected final Map<Identifier, DoubleRawValue> impacts = new HashMap<>();

    @Override
    public void registerAll(Builder builder) {
        Map<Identifier, Double> defaultImpacts = new LinkedHashMap<>();
        Map<Identifier, Double> defaultCapacities = new LinkedHashMap<>();
        for (Block block : EncasedBlocks.ALL) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            Block base = EncasedBlocks.getBase(block);
            if (base == null)
                continue;
            Double impact = IMPACT_BY_BASE.get(base);
            if (impact != null)
                defaultImpacts.put(id, impact);
            Double capacity = CAPACITY_BY_BASE.get(base);
            if (capacity != null)
                defaultCapacities.put(id, capacity);
        }
        defaultCapacities.put(BuiltInRegistries.BLOCK.getKey(EncasedBlocks.CREATIVE_COGWHEEL), 16384.0);

        builder.comment(".", Comments.su, Comments.impact).push("impact");
        defaultImpacts.forEach((id, value) -> impacts.put(id, builder.define(id.getPath(), value)));
        builder.pop();

        builder.comment(".", Comments.su, Comments.capacity).push("capacity");
        defaultCapacities.forEach((id, value) -> capacities.put(id, builder.define(id.getPath(), value)));
        builder.pop();
    }

    @Override
    public String getName() {
        return "stressValues.v" + VERSION;
    }

    public @Nullable DoubleSupplier getImpact(Block block) {
        DoubleRawValue value = impacts.get(BuiltInRegistries.BLOCK.getKey(block));
        return value == null ? null : value::get;
    }

    public @Nullable DoubleSupplier getCapacity(Block block) {
        DoubleRawValue value = capacities.get(BuiltInRegistries.BLOCK.getKey(block));
        return value == null ? null : value::get;
    }

    private static class Comments {
        static String su = "[in Stress Units]";
        static String impact = "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
        static String capacity = "Configure how much stress a source can accommodate for.";
    }
}
