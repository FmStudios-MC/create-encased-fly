package fr.iglee42.createcasing.casings;

import com.google.common.collect.ImmutableList;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import fr.iglee42.createcasing.casings.CasingSet.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The casing sets. Sprites, belt covers and the per-set partial models of upstream's builder calls
 * are in {@code client.CasingSetVisuals}.
 */
public class CasingSets {

    private static final List<CasingSet> sets = new ArrayList<>();

    public static final CasingSet ANDESITE = register("andesite", new CasingSet.Options()
        .existingCasing(() -> AllBlocks.ANDESITE_CASING)
        .existing(Part.SHAFT, () -> AllBlocks.ANDESITE_ENCASED_SHAFT)
        .existing(Part.COGWHEEL, () -> AllBlocks.ANDESITE_ENCASED_COGWHEEL)
        .existing(Part.LARGE_COGWHEEL, () -> AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL)
        .existing(Part.DEPOT, () -> AllBlocks.DEPOT)
        .existing(Part.PRESS, () -> AllBlocks.MECHANICAL_PRESS)
        .existing(Part.MIXER, () -> AllBlocks.MECHANICAL_MIXER)
        .existing(Part.CHAIN_DRIVE, () -> AllBlocks.ENCASED_CHAIN_DRIVE)
        .existing(Part.CHAIN_GEARSHIFT, () -> AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT)
        .existing(Part.CHAIN_CONVEYOR, () -> AllBlocks.CHAIN_CONVEYOR)
        .existing(Part.GEARSHIFT, () -> AllBlocks.GEARSHIFT)
        .existing(Part.CLUTCH, () -> AllBlocks.CLUTCH)
        .existingGearbox(() -> AllBlocks.GEARBOX, () -> AllItems.VERTICAL_GEARBOX)
        .existing(Part.DEPLOYER, () -> AllBlocks.DEPLOYER)
        .existing(Part.ENCASED_FAN, () -> AllBlocks.ENCASED_FAN)
        .existing(Part.HARVESTER, () -> AllBlocks.MECHANICAL_HARVESTER)
        .existing(Part.SAW, () -> AllBlocks.MECHANICAL_SAW)
        .existing(Part.DRILL, () -> AllBlocks.MECHANICAL_DRILL)
        .existing(Part.PLOUGH, () -> AllBlocks.MECHANICAL_PLOUGH)
        .existing(Part.ROLLER, () -> AllBlocks.MECHANICAL_ROLLER)
        .fluids()
        .encasedCustomTransmissionBlocks()
        .configurableGearbox()
        .autoClutch()
        .existing(Part.STORAGE_INTERFACE, () -> AllBlocks.PORTABLE_STORAGE_INTERFACE)
    );
    public static final CasingSet BRASS = register("brass", new CasingSet.Options()
        .existingCasing(() -> AllBlocks.BRASS_CASING)
        .existing(Part.SHAFT, () -> AllBlocks.BRASS_ENCASED_SHAFT)
        .existing(Part.COGWHEEL, () -> AllBlocks.BRASS_ENCASED_COGWHEEL)
        .existing(Part.LARGE_COGWHEEL, () -> AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL)
        .fluids()
        .encasedCustomTransmissionBlocks()
        .complexTransmissionBlocks()
        .processingBlocks()
        .contraptionBlocks()
    );
    public static final CasingSet COPPER = register("copper", new CasingSet.Options()
        .existingCasing(() -> AllBlocks.COPPER_CASING)
        .simpleTransmissions()
        .encasedCustomTransmissionBlocks()
        .existing(Part.FLUID_PIPE, () -> AllBlocks.ENCASED_FLUID_PIPE)
        .encasedCustomPipe()
        .belt()
        .complexTransmissionBlocks()
        .processingBlocks()
        .contraptionBlocks()
    );
    public static final CasingSet RAILWAY = register("railway", new CasingSet.Options()
        .existingCasing(() -> AllBlocks.RAILWAY_CASING)
        .everythingExceptCasing()
    );
    public static final CasingSet SHADOW_STEEL = register("shadow_steel", new CasingSet.Options()
        .existingCasing(() -> AllBlocks.SHADOW_STEEL_CASING)
        .everythingExceptCasing()
    );
    public static final CasingSet REFINED_RADIANCE = register("refined_radiance", new CasingSet.Options()
        .existingCasing(() -> AllBlocks.REFINED_RADIANCE_CASING)
        .everythingExceptCasing()
    );
    public static final CasingSet CREATIVE = register("creative", new CasingSet.Options()
        .everything()
    );
    public static final CasingSet INDUSTRIAL_IRON = register("industrial_iron", new CasingSet.Options()
        .existingCasing(() -> AllBlocks.INDUSTRIAL_IRON_BLOCK)
        .everythingExceptCasing()
    );
    public static final CasingSet WEATHERED_IRON = register("weathered_iron", new CasingSet.Options()
        .existingCasing(() -> AllBlocks.WEATHERED_IRON_BLOCK)
        .everythingExceptCasing()
    );

    public static final CasingSet ZINC = register("zinc", new CasingSet.Options()
        .simpleTransmissions()
        .encasedCustomTransmissionBlocks()
        .fluids()
        .casing());

    public static CasingSet register(String id, CasingSet.Options options) {
        String name = id.toLowerCase(Locale.ROOT);
        if (sets.stream().anyMatch(set -> set.getName().equalsIgnoreCase(name)))
            throw new IllegalArgumentException("A casing set with name `" + name + "` already exists !");
        CasingSet set = new CasingSet(name, options);
        sets.add(set);
        return set;
    }

    public static List<CasingSet> getSets() {
        return ImmutableList.copyOf(sets);
    }
}
