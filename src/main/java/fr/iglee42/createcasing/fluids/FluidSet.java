package fr.iglee42.createcasing.fluids;

import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * One metal and the fluid blocks made from it. Built like {@code CasingSet}: blocks in an
 * {@link EnumMap}, client-only data (tank sprites, gauge and magnet partials) in
 * {@code client.FluidSetVisuals}. The recipe tags upstream kept here only fed its datagen.
 */
public class FluidSet {

    public enum Part {
        FLUID_PIPE, GLASS_FLUID_PIPE, PUMP, SMART_FLUID_PIPE, FLUID_VALVE, VALVE_HANDLE, FLUID_TANK, HOSE_PULLEY,
        ITEM_DRAIN, PORTABLE_FLUID_INTERFACE, STEAM_ENGINE, WHISTLE, SPOUT
    }

    private final String name;
    private final Supplier<? extends Block> casing;
    private final EnumSet<Part> generated;
    private final EnumMap<Part, Supplier<? extends Block>> blocks = new EnumMap<>(Part.class);
    private final boolean notEncasable;

    protected FluidSet(String name, Options options) {
        this.name = name;
        this.casing = Objects.requireNonNull(options.casing, "Casing Supplier for fluid set " + name + " can't be null");
        generated = options.generated.isEmpty() ? EnumSet.noneOf(Part.class) : EnumSet.copyOf(options.generated);
        blocks.putAll(options.existing);
        notEncasable = options.notEncasable;
    }

    public String getName() {
        return name;
    }

    public Block getCasing() {
        return casing.get();
    }

    public boolean generates(Part part) {
        return generated.contains(part);
    }

    public @Nullable Block get(Part part) {
        Supplier<? extends Block> supplier = blocks.get(part);
        return supplier == null ? null : supplier.get();
    }

    public void set(Part part, Block block) {
        if (blocks.containsKey(part))
            throw new UnsupportedOperationException("You cannot modify the " + part + " of fluid set " + name + ", it has already been set");
        blocks.put(part, () -> block);
    }

    public boolean isNotEncasable() {
        return notEncasable;
    }

    public boolean isInSet(Block block) {
        for (Supplier<? extends Block> supplier : blocks.values())
            if (supplier.get() == block)
                return true;
        return false;
    }

    public List<Block> getAllBlocks() {
        List<Block> list = new ArrayList<>();
        for (Supplier<? extends Block> supplier : blocks.values()) {
            Block block = supplier.get();
            if (block != null)
                list.add(block);
        }
        return list;
    }

    public static class Options {
        private @Nullable Supplier<? extends Block> casing;
        private final EnumSet<Part> generated = EnumSet.noneOf(Part.class);
        private final EnumMap<Part, Supplier<? extends Block>> existing = new EnumMap<>(Part.class);
        private boolean notEncasable;

        public Options casing(Supplier<? extends Block> casing) {
            this.casing = casing;
            return this;
        }

        public Options generate(Part... parts) {
            for (Part part : parts) {
                if (!existing.containsKey(part))
                    generated.add(part);
            }
            return this;
        }

        public Options existing(Part part, Supplier<? extends Block> block) {
            existing.put(part, block);
            generated.remove(part);
            return this;
        }

        public Options notEncasable() {
            this.notEncasable = true;
            return this;
        }

        /** Every fluid block; the glass pipe comes with the pipe. */
        public Options everything(Supplier<? extends Block> casing) {
            return casing(casing).generate(Part.values());
        }
    }
}
