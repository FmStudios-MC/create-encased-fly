package fr.iglee42.createcasing.client.compat.rei;

import com.zurrtum.create.compat.rei.ReiCommonPlugin;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSet.Part;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import fr.iglee42.createcasing.registries.EncasedItems;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The mod's machines as workstations of Create's recipe categories, as upstream added them through a
 * mixin into Create's JEI plugin. Create Fly 1.21.8 integrates REI only, so this is an REI plugin,
 * found through the {@code rei_client} entrypoint.
 */
public class EncasedReiPlugin implements REIClientPlugin {
    // after Create Fly's plugin, whose categories these join
    @Override
    public double getPriority() {
        return 100;
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        EntryStack<?>[] mixers = casing(Part.MIXER), presses = casing(Part.PRESS), deployers = casing(Part.DEPLOYER),
            saws = casing(Part.SAW), fans = casing(Part.ENCASED_FAN);
        for (CategoryIdentifier<?> type : List.of(ReiCommonPlugin.MIXING, ReiCommonPlugin.AUTOMATIC_SHAPELESS, ReiCommonPlugin.AUTOMATIC_BREWING))
            registry.addWorkstations(type, mixers);
        for (CategoryIdentifier<?> type : List.of(ReiCommonPlugin.PRESSING, ReiCommonPlugin.PACKING, ReiCommonPlugin.AUTOMATIC_PACKING))
            registry.addWorkstations(type, presses);
        registry.addWorkstations(ReiCommonPlugin.DEPLOYING, deployers);
        registry.addWorkstations(ReiCommonPlugin.SAWING, saws);
        registry.addWorkstations(ReiCommonPlugin.BLOCK_CUTTING, saws);
        for (CategoryIdentifier<?> type : List.of(ReiCommonPlugin.FAN_BLASTING, ReiCommonPlugin.FAN_HAUNTING, ReiCommonPlugin.FAN_SMOKING, ReiCommonPlugin.FAN_WASHING))
            registry.addWorkstations(type, fans);
        registry.addWorkstations(ReiCommonPlugin.DRAINING, fluid(FluidSet.Part.ITEM_DRAIN));
        registry.addWorkstations(ReiCommonPlugin.SPOUT_FILLING, fluid(FluidSet.Part.SPOUT));
    }

    /** Encased shaft and cogwheel items only come from encasing, as upstream's JEI plugin hid them. */
    @Override
    public void registerEntries(EntryRegistry registry) {
        Set<Item> hidden = new HashSet<>(EncasedItems.HIDDEN);
        registry.removeEntryIf(stack -> stack.getType() == VanillaEntryTypes.ITEM
            && hidden.contains(((ItemStack) stack.getValue()).getItem()));
    }

    private static EntryStack<?>[] casing(Part part) {
        List<EntryStack<?>> list = new ArrayList<>();
        for (CasingSet set : CasingSets.getSets())
            if (set.generates(part))
                list.add(EntryStacks.of(set.get(part)));
        return list.toArray(EntryStack<?>[]::new);
    }

    private static EntryStack<?>[] fluid(FluidSet.Part part) {
        List<EntryStack<?>> list = new ArrayList<>();
        for (FluidSet set : FluidSets.getSets())
            if (set.generates(part))
                list.add(EntryStacks.of(set.get(part)));
        return list.toArray(EntryStack<?>[]::new);
    }
}
