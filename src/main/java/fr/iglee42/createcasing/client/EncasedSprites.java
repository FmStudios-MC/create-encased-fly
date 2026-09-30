package fr.iglee42.createcasing.client;

import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import com.zurrtum.create.client.catnip.render.SpriteShifter;
import com.zurrtum.create.client.foundation.block.connected.AllCTTypes;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShifter;
import com.zurrtum.create.client.foundation.block.connected.CTType;
import fr.iglee42.createcasing.CreateCasing;
import net.minecraft.resources.Identifier;

/**
 * Connected-texture and sprite shifts. Create Fly reads connected textures as one sprite per tile
 * ({@code <name>_connected/<i>.png}), cut from upstream's sheets by {@code tools/split_ct_sheets.py}.
 * <p>
 * Upstream turned Create's creative casing into an omnidirectional connected texture with its own
 * sheet by patching Create's {@code AllSpriteShifts}; here the mod's creative casing blocks simply
 * use {@link #CREATIVE_CASING}, and Create's own blocks are left alone.
 */
public class EncasedSprites {
    public static final CTSpriteShiftEntry ZINC_CASING = omni("casing/zinc");
    public static final CTSpriteShiftEntry CREATIVE_CASING = CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL,
        Identifier.fromNamespaceAndPath("create", "block/creative_casing"), CreateCasing.asResource("block/creative_casing_connected"));

    public static final CTSpriteShiftEntry RAILWAY_ENCASED_COGWHEEL_SIDE = vertical("encased_cogwheel/railway");
    public static final CTSpriteShiftEntry RAILWAY_ENCASED_COGWHEEL_OTHERSIDE = horizontal("encased_cogwheel/railway");
    public static final CTSpriteShiftEntry COPPER_ENCASED_COGWHEEL_SIDE = vertical("encased_cogwheel/copper");
    public static final CTSpriteShiftEntry COPPER_ENCASED_COGWHEEL_OTHERSIDE = horizontal("encased_cogwheel/copper");
    public static final CTSpriteShiftEntry SHADOW_STEEL_ENCASED_COGWHEEL_SIDE = vertical("encased_cogwheel/shadow_steel");
    public static final CTSpriteShiftEntry SHADOW_STEEL_ENCASED_COGWHEEL_OTHERSIDE = horizontal("encased_cogwheel/shadow_steel");
    public static final CTSpriteShiftEntry REFINED_RADIANCE_ENCASED_COGWHEEL_SIDE = vertical("encased_cogwheel/refined_radiance");
    public static final CTSpriteShiftEntry REFINED_RADIANCE_ENCASED_COGWHEEL_OTHERSIDE = horizontal("encased_cogwheel/refined_radiance");
    public static final CTSpriteShiftEntry CREATIVE_ENCASED_COGWHEEL_SIDE = vertical("encased_cogwheel/creative");
    public static final CTSpriteShiftEntry CREATIVE_ENCASED_COGWHEEL_OTHERSIDE = horizontal("encased_cogwheel/creative");
    public static final CTSpriteShiftEntry ZINC_ENCASED_COGWHEEL_SIDE = vertical("encased_cogwheel/zinc");
    public static final CTSpriteShiftEntry ZINC_ENCASED_COGWHEEL_OTHERSIDE = horizontal("encased_cogwheel/zinc");

    public static final SpriteShiftEntry COPPER_BELT_CASING = beltCasing("copper");
    public static final SpriteShiftEntry RAILWAY_BELT_CASING = beltCasing("railway");
    public static final SpriteShiftEntry INDUSTRIAL_IRON_BELT_CASING = beltCasing("industrial_iron");
    public static final SpriteShiftEntry WEATHERED_IRON_BELT_CASING = beltCasing("weathered_iron");
    public static final SpriteShiftEntry CREATIVE_BELT_CASING = beltCasing("creative");
    public static final SpriteShiftEntry REFINED_RADIANCE_BELT_CASING = beltCasing("refined_radiance");
    public static final SpriteShiftEntry SHADOW_STEEL_BELT_CASING = beltCasing("shadow_steel");

    public static final CTSpriteShiftEntry ANDESITE_FLUID_TANK = rectangle("fluid_tank/andesite");
    public static final CTSpriteShiftEntry ANDESITE_FLUID_TANK_TOP = rectangle("fluid_tank_top/andesite");
    public static final CTSpriteShiftEntry ANDESITE_FLUID_TANK_INNER = rectangle("fluid_tank_inner/andesite");
    public static final CTSpriteShiftEntry BRASS_FLUID_TANK = rectangle("fluid_tank/brass");
    public static final CTSpriteShiftEntry BRASS_FLUID_TANK_TOP = rectangle("fluid_tank_top/brass");
    public static final CTSpriteShiftEntry BRASS_FLUID_TANK_INNER = rectangle("fluid_tank_inner/brass");
    public static final CTSpriteShiftEntry ZINC_FLUID_TANK = rectangle("fluid_tank/zinc");
    public static final CTSpriteShiftEntry ZINC_FLUID_TANK_TOP = rectangle("fluid_tank_top/zinc");
    public static final CTSpriteShiftEntry ZINC_FLUID_TANK_INNER = rectangle("fluid_tank_inner/zinc");

    /** Belts are modelled with Create's brass casing; other casings shift its sprite onto theirs. */
    private static SpriteShiftEntry beltCasing(String name) {
        return SpriteShifter.get(Identifier.fromNamespaceAndPath("create", "block/belt/brass_belt_casing"), CreateCasing.asResource("block/belt_casing/" + name));
    }

    private static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL, name);
    }

    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name);
    }

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry rectangle(String name) {
        return getCT(AllCTTypes.RECTANGLE, name);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String name) {
        return CTSpriteShifter.getCT(type, CreateCasing.asResource("block/" + name), CreateCasing.asResource("block/" + name + "_connected"));
    }

    public static void init() {
    }
}
