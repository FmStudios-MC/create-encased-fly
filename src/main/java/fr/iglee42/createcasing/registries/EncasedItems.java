package fr.iglee42.createcasing.registries;

import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyItem;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.items.CustomVerticalGearboxItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Items and the creative tab. The tab lists what upstream's Registrate tab listed: flat items
 * first, then blocks in registration order with each vertical gearbox after its gearbox, without
 * the encased shaft and cogwheel items (only obtained by encasing), the sequenced assembly
 * intermediate and the mldeg shaft.
 */
public class EncasedItems {
    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateCasing.asResource("tab"));

    private static final List<Item> TAB_ITEMS = new ArrayList<>();
    private static final List<Item> TAB_BLOCK_ITEMS = new ArrayList<>();
    /** Items upstream kept out of the creative tab and recipe viewers. */
    public static final List<Item> HIDDEN = new ArrayList<>();

    public static Item CHORIUM_INGOT;
    public static SequencedAssemblyItem PROCESSING_CHORIUM;
    public static Item ANDESITE_SHEET;
    public static Item ZINC_SHEET;

    /** Items not tied to a block. Runs before the blocks, so they come first in the tab. */
    public static void register() {
        CHORIUM_INGOT = register("chorium_ingot", Item::new, new Item.Properties().rarity(Rarity.EPIC), true);
        PROCESSING_CHORIUM = register("processing_chorium", SequencedAssemblyItem::new, new Item.Properties().rarity(Rarity.EPIC), false);
        HIDDEN.add(PROCESSING_CHORIUM);
        ANDESITE_SHEET = register("andesite_sheet", Item::new, new Item.Properties(), true);
        ZINC_SHEET = register("zinc_sheet", Item::new, new Item.Properties(), true);
    }

    /** Vertical gearboxes, once their gearbox blocks exist. */
    public static void registerVerticalGearboxes() {
        for (CasingSet set : CasingSets.getSets()) {
            if (!set.generates(CasingSet.Part.GEARBOX))
                continue;
            Block gearbox = set.get(CasingSet.Part.GEARBOX);
            CustomVerticalGearboxItem item = register("vertical_" + set.getName() + "_gearbox",
                p -> new CustomVerticalGearboxItem(p, gearbox), new Item.Properties(), false);
            set.setVerticalGearboxItem(item);
            int index = TAB_BLOCK_ITEMS.indexOf(gearbox.asItem());
            TAB_BLOCK_ITEMS.add(index + 1, item);
        }
        Item mldeg = fr.iglee42.createcasing.transmissions.TransmissionSets.MLDEG.getShaft().asItem();
        TAB_BLOCK_ITEMS.remove(mldeg);
        HIDDEN.add(mldeg);
    }

    public static void registerTab() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY,
            CreativeModeTab.builder(null, -1)
                .title(Component.translatable("itemGroup." + CreateCasing.MODID + ".base"))
                .icon(() -> new ItemStack(CasingSets.BRASS.get(CasingSet.Part.GEARBOX)))
                .displayItems((parameters, output) -> {
                    TAB_ITEMS.forEach(output::accept);
                    TAB_BLOCK_ITEMS.forEach(output::accept);
                })
                .build());
    }

    static void registerBlockItem(Block block, BiFunction<Block, Item.Properties, ? extends Item> factory, boolean hidden) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, BuiltInRegistries.BLOCK.getKey(block));
        // useBlockDescriptionPrefix keeps the "block.createcasing.*" translation keys of the lang files.
        Item item = factory.apply(block, new Item.Properties().setId(key).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, key, item);
        if (hidden)
            HIDDEN.add(item);
        else
            TAB_BLOCK_ITEMS.add(item);
    }

    private static <I extends Item> I register(String name, Function<Item.Properties, I> factory, Item.Properties properties, boolean inTab) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, CreateCasing.asResource(name));
        I item = Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
        if (inTab)
            TAB_ITEMS.add(item);
        return item;
    }
}
