package fr.iglee42.createcasing.transmissions;

import com.google.common.collect.ImmutableList;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import fr.iglee42.createcasing.blocks.cogwheels.WoodenCogwheelBlock;
import fr.iglee42.createcasing.blocks.shafts.GlassShaftBlock;
import fr.iglee42.createcasing.blocks.shafts.WoodenShaftBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.*;

public class TransmissionSets {

    private static final List<TransmissionSet> sets = new ArrayList<>();
    private static final Map<WoodType, TransmissionSet> woodSets = new HashMap<>();

    public static final TransmissionSet ANDESITE = register("andesite", new TransmissionSet.Options()
        .item(() -> AllItems.ANDESITE_ALLOY)
        .existingShaft(() -> AllBlocks.SHAFT)
        .cogwheel()
        .largeCogwheel()
    );

    public static final TransmissionSet BRASS = register("brass", new TransmissionSet.Options()
        .everything(() -> AllItems.BRASS_INGOT)
    );

    public static final TransmissionSet COPPER = register("copper", new TransmissionSet.Options()
        .everything(() -> Items.COPPER_INGOT)
    );

    public static final TransmissionSet ZINC = register("zinc", new TransmissionSet.Options()
        .everything(() -> AllItems.ZINC_INGOT)
    );

    public static final TransmissionSet MLDEG = register("mldeg", new TransmissionSet.Options()
        .item(() -> Items.BLACKSTONE)
        .shaft()
    );

    public static final TransmissionSet GLASS = register("glass", new TransmissionSet.Options()
        .item(() -> Items.GLASS)
        .shaftConstructor(GlassShaftBlock::new)
        .shaft()
    );

    static {
        for (WoodType woodType : new WoodType[]{WoodType.ACACIA, WoodType.BIRCH, WoodType.BAMBOO, WoodType.CHERRY, WoodType.CRIMSON, WoodType.DARK_OAK, WoodType.OAK, WoodType.JUNGLE, WoodType.MANGROVE, WoodType.WARPED}) {
            String name = woodType.name().toLowerCase(Locale.ROOT);
            TransmissionSet set = register(name, new TransmissionSet.Options()
                .wooden()
                .shaftConstructor(WoodenShaftBlock::new)
                .cogwheelConstructor((props, large) -> large ? WoodenCogwheelBlock.large(props) : WoodenCogwheelBlock.small(props))
                .everything(() -> BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(name + "_planks")))
            );
            woodSets.put(woodType, set);
        }

        TransmissionSet spruceSet = register("spruce", new TransmissionSet.Options()
            .wooden()
            .item(() -> Items.SPRUCE_PLANKS)
            .shaft()
            .shaftConstructor(WoodenShaftBlock::new)
            .existingCogwheel(() -> AllBlocks.COGWHEEL)
            .existingLargeCogwheel(() -> AllBlocks.LARGE_COGWHEEL)
        );

        woodSets.put(WoodType.SPRUCE, spruceSet);
    }

    public static TransmissionSet register(String id, TransmissionSet.Options options) {
        String name = id.toLowerCase(Locale.ROOT);
        if (sets.stream().anyMatch(set -> set.getName().equalsIgnoreCase(name)))
            throw new IllegalArgumentException("A transmission set with name `" + name + "` already exists !");
        TransmissionSet set = new TransmissionSet(name, options);
        sets.add(set);
        return set;
    }

    public static List<TransmissionSet> getSets() {
        return ImmutableList.copyOf(sets);
    }

    public static List<TransmissionSet> getWoodSets() {
        return ImmutableList.copyOf(woodSets.values());
    }

    public static TransmissionSet getWoodSet(WoodType woodType) {
        return woodSets.get(woodType);
    }

    public static WoodType getWoodTypeForSet(TransmissionSet set) {
        return woodSets.entrySet().stream().filter(e -> e.getValue().equals(set)).map(Map.Entry::getKey).findFirst().orElse(null);
    }
}
