package fr.iglee42.createcasing.client;

import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * The client half of upstream's fluid set builder calls: tank sprites and the partial models of
 * steam engine gauges, valve handles, hose pulley magnets, fluid interface tops and spout bottoms.
 * Only the sets that generate their own blocks have entries; copper is Create's.
 */
public record FluidSetVisuals(
    String name,
    CTSpriteShiftEntry tankSide,
    CTSpriteShiftEntry tankTop,
    CTSpriteShiftEntry tankInner,
    PartialModel gauge,
    PartialModel gaugeDial,
    PartialModel valveHandle,
    PartialModel hosePulleyHalfMagnet,
    PartialModel hosePulleyMagnet,
    PartialModel fluidInterfaceTop,
    PartialModel spoutBottom
) {
    private static final Map<String, FluidSetVisuals> BY_SET = new HashMap<>();
    private static final Map<Block, FluidSetVisuals> BY_BLOCK = new IdentityHashMap<>();

    public static void init() {
        put("andesite", EncasedSprites.ANDESITE_FLUID_TANK, EncasedSprites.ANDESITE_FLUID_TANK_TOP, EncasedSprites.ANDESITE_FLUID_TANK_INNER);
        put("brass", EncasedSprites.BRASS_FLUID_TANK, EncasedSprites.BRASS_FLUID_TANK_TOP, EncasedSprites.BRASS_FLUID_TANK_INNER);
        put("zinc", EncasedSprites.ZINC_FLUID_TANK, EncasedSprites.ZINC_FLUID_TANK_TOP, EncasedSprites.ZINC_FLUID_TANK_INNER);
    }

    private static void put(String name, CTSpriteShiftEntry side, CTSpriteShiftEntry top, CTSpriteShiftEntry inner) {
        BY_SET.put(name, new FluidSetVisuals(name, side, top, inner,
            EncasedPartialModels.block("steam_engine/" + name + "/gauge"),
            EncasedPartialModels.block("steam_engine/" + name + "/gauge_dial"),
            EncasedPartialModels.block(name + "_valve_handle"),
            EncasedPartialModels.block("hose_pulley/" + name + "/rope_half_magnet"),
            EncasedPartialModels.block("hose_pulley/" + name + "/pulley_magnet"),
            EncasedPartialModels.block("portable_fluid_interface/" + name + "/block_top"),
            EncasedPartialModels.block("spout/" + name + "/bottom")));
    }

    /** Called once all blocks exist; only generated blocks are indexed, so Create's keep their partials. */
    public static void indexBlocks() {
        for (FluidSet set : FluidSets.getSets()) {
            FluidSetVisuals visuals = BY_SET.get(set.getName());
            if (visuals == null)
                continue;
            for (FluidSet.Part part : FluidSet.Part.values()) {
                Block block = set.get(part);
                if (block != null && set.generates(part))
                    BY_BLOCK.put(block, visuals);
            }
        }
    }

    public static @Nullable FluidSetVisuals of(String setName) {
        return BY_SET.get(setName);
    }

    public static @Nullable FluidSetVisuals of(BlockState state) {
        return BY_BLOCK.get(state.getBlock());
    }
}
