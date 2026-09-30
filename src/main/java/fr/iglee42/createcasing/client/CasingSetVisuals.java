package fr.iglee42.createcasing.client;

import com.zurrtum.create.client.AllSpriteShifts;
import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * The client half of upstream's casing set builder calls: connected-texture sprites, belt casing
 * sprite and covers, and the per-set partial models of chain conveyors, mixers, drills and rollers.
 */
public record CasingSetVisuals(
    @Nullable CTSpriteShiftEntry casing,
    @Nullable CTSpriteShiftEntry cogSide,
    @Nullable CTSpriteShiftEntry cogOtherSide,
    @Nullable SpriteShiftEntry belt,
    @Nullable PartialModel beltCoverX,
    @Nullable PartialModel beltCoverZ,
    @Nullable PartialModel conveyorGuard,
    @Nullable PartialModel conveyorShaft,
    @Nullable PartialModel mixerHead,
    @Nullable PartialModel drillHead,
    @Nullable PartialModel rollerFrame
) {
    private static final Map<String, CasingSetVisuals> BY_SET = new HashMap<>();
    private static final Map<Block, CasingSetVisuals> BY_BLOCK = new IdentityHashMap<>();

    public static void init() {
        put(CasingSets.ANDESITE, AllSpriteShifts.ANDESITE_CASING, null, null, null, false, false);
        put(CasingSets.BRASS, AllSpriteShifts.BRASS_CASING, null, null, null, true, true);
        put(CasingSets.COPPER, AllSpriteShifts.COPPER_CASING, EncasedSprites.COPPER_ENCASED_COGWHEEL_SIDE,
            EncasedSprites.COPPER_ENCASED_COGWHEEL_OTHERSIDE, EncasedSprites.COPPER_BELT_CASING, true, true);
        put(CasingSets.RAILWAY, AllSpriteShifts.RAILWAY_CASING, EncasedSprites.RAILWAY_ENCASED_COGWHEEL_SIDE,
            EncasedSprites.RAILWAY_ENCASED_COGWHEEL_OTHERSIDE, EncasedSprites.RAILWAY_BELT_CASING, true, true);
        put(CasingSets.SHADOW_STEEL, AllSpriteShifts.SHADOW_STEEL_CASING, EncasedSprites.SHADOW_STEEL_ENCASED_COGWHEEL_SIDE,
            EncasedSprites.SHADOW_STEEL_ENCASED_COGWHEEL_OTHERSIDE, EncasedSprites.SHADOW_STEEL_BELT_CASING, true, true);
        put(CasingSets.REFINED_RADIANCE, AllSpriteShifts.REFINED_RADIANCE_CASING, EncasedSprites.REFINED_RADIANCE_ENCASED_COGWHEEL_SIDE,
            EncasedSprites.REFINED_RADIANCE_ENCASED_COGWHEEL_OTHERSIDE, EncasedSprites.REFINED_RADIANCE_BELT_CASING, true, true);
        put(CasingSets.CREATIVE, EncasedSprites.CREATIVE_CASING, EncasedSprites.CREATIVE_ENCASED_COGWHEEL_SIDE,
            EncasedSprites.CREATIVE_ENCASED_COGWHEEL_OTHERSIDE, EncasedSprites.CREATIVE_BELT_CASING, true, true);
        put(CasingSets.INDUSTRIAL_IRON, null, null, null, EncasedSprites.INDUSTRIAL_IRON_BELT_CASING, true, true);
        put(CasingSets.WEATHERED_IRON, null, null, null, EncasedSprites.WEATHERED_IRON_BELT_CASING, true, true);
        put(CasingSets.ZINC, EncasedSprites.ZINC_CASING, EncasedSprites.ZINC_ENCASED_COGWHEEL_SIDE,
            EncasedSprites.ZINC_ENCASED_COGWHEEL_OTHERSIDE, null, false, false);
    }

    private static void put(CasingSet set, @Nullable CTSpriteShiftEntry casing, @Nullable CTSpriteShiftEntry cogSide,
                            @Nullable CTSpriteShiftEntry cogOtherSide, @Nullable SpriteShiftEntry belt,
                            boolean complexTransmissions, boolean processingAndContraptions) {
        String name = set.getName();
        boolean hasBelt = belt != null;
        CasingSetVisuals visuals = new CasingSetVisuals(casing, cogSide, cogOtherSide, belt,
            hasBelt ? EncasedPartialModels.block("belt_cover/" + name + "_belt_cover_x") : null,
            hasBelt ? EncasedPartialModels.block("belt_cover/" + name + "_belt_cover_z") : null,
            complexTransmissions ? EncasedPartialModels.block("chain_conveyor/" + name + "/guard") : null,
            complexTransmissions ? EncasedPartialModels.block("chain_conveyor/" + name + "/shaft") : null,
            processingAndContraptions ? EncasedPartialModels.block("mixer/" + name + "/head") : null,
            processingAndContraptions ? EncasedPartialModels.block("mechanical_drill/" + name + "/head") : null,
            processingAndContraptions ? EncasedPartialModels.block("mechanical_roller/" + name + "/frame") : null);
        BY_SET.put(name, visuals);
    }

    /** Called once all blocks exist. */
    public static void indexBlocks() {
        for (CasingSet set : CasingSets.getSets()) {
            CasingSetVisuals visuals = BY_SET.get(set.getName());
            for (Block block : set.getAllBlocks())
                if (!BY_BLOCK.containsKey(block))
                    BY_BLOCK.put(block, visuals);
        }
    }

    public static @Nullable CasingSetVisuals of(CasingSet set) {
        return BY_SET.get(set.getName());
    }

    public static @Nullable CasingSetVisuals of(BlockState state) {
        return BY_BLOCK.get(state.getBlock());
    }

    public static PartialModel mixerHead(BlockState state, PartialModel fallback) {
        CasingSetVisuals visuals = of(state);
        return visuals != null && visuals.mixerHead != null ? visuals.mixerHead : fallback;
    }

    public static PartialModel drillHead(BlockState state, PartialModel fallback) {
        CasingSetVisuals visuals = of(state);
        return visuals != null && visuals.drillHead != null ? visuals.drillHead : fallback;
    }

    public static PartialModel rollerFrame(BlockState state, PartialModel fallback) {
        CasingSetVisuals visuals = of(state);
        return visuals != null && visuals.rollerFrame != null ? visuals.rollerFrame : fallback;
    }

    public static PartialModel conveyorGuard(BlockState state, PartialModel fallback) {
        CasingSetVisuals visuals = of(state);
        return visuals != null && visuals.conveyorGuard != null ? visuals.conveyorGuard : fallback;
    }

    public static PartialModel conveyorShaft(BlockState state, PartialModel fallback) {
        CasingSetVisuals visuals = of(state);
        return visuals != null && visuals.conveyorShaft != null ? visuals.conveyorShaft : fallback;
    }
}
