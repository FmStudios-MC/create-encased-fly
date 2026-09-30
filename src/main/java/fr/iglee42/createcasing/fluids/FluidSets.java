package fr.iglee42.createcasing.fluids;

import com.google.common.collect.ImmutableList;
import com.zurrtum.create.AllBlocks;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.fluids.FluidSet.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FluidSets {

    private static final List<FluidSet> sets = new ArrayList<>();

    public static final FluidSet ANDESITE = register("andesite", new FluidSet.Options()
        .everything(() -> AllBlocks.ANDESITE_CASING));

    public static final FluidSet BRASS = register("brass", new FluidSet.Options()
        .everything(() -> AllBlocks.BRASS_CASING));

    public static final FluidSet COPPER = register("copper", new FluidSet.Options()
        .casing(() -> AllBlocks.COPPER_CASING)
        .existing(Part.FLUID_PIPE, () -> AllBlocks.FLUID_PIPE)
        .existing(Part.GLASS_FLUID_PIPE, () -> AllBlocks.GLASS_FLUID_PIPE)
        .existing(Part.PUMP, () -> AllBlocks.MECHANICAL_PUMP)
        .existing(Part.SMART_FLUID_PIPE, () -> AllBlocks.SMART_FLUID_PIPE)
        .existing(Part.ITEM_DRAIN, () -> AllBlocks.ITEM_DRAIN)
        .existing(Part.HOSE_PULLEY, () -> AllBlocks.HOSE_PULLEY)
        .existing(Part.PORTABLE_FLUID_INTERFACE, () -> AllBlocks.PORTABLE_FLUID_INTERFACE)
        .existing(Part.STEAM_ENGINE, () -> AllBlocks.STEAM_ENGINE)
        .existing(Part.FLUID_TANK, () -> AllBlocks.FLUID_TANK)
        .existing(Part.WHISTLE, () -> AllBlocks.STEAM_WHISTLE)
        .existing(Part.FLUID_VALVE, () -> AllBlocks.FLUID_VALVE)
        .existing(Part.VALVE_HANDLE, () -> AllBlocks.COPPER_VALVE_HANDLE)
    );

    public static final FluidSet ZINC = register("zinc", new FluidSet.Options()
        .everything(CasingSets.ZINC::getCasing));

    public static FluidSet register(String id, FluidSet.Options options) {
        String name = id.toLowerCase(Locale.ROOT);
        if (sets.stream().anyMatch(set -> set.getName().equalsIgnoreCase(name)))
            throw new IllegalArgumentException("A fluid set with name `" + name + "` already exists !");
        FluidSet set = new FluidSet(name, options);
        sets.add(set);
        return set;
    }

    public static List<FluidSet> getSets() {
        return ImmutableList.copyOf(sets);
    }
}
