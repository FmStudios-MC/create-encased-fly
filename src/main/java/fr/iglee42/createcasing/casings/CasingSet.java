package fr.iglee42.createcasing.casings;

import com.google.common.base.Preconditions;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity.CasingType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * One casing material and the Create blocks made in it.
 * <p>
 * Upstream kept one field, flag, getter and setter per block kind, plus the connected-texture
 * sprites and partial models. In the port the blocks live in an {@link EnumMap} keyed by
 * {@link Part}, and everything that only exists on the client (sprites, partial models) lives in
 * {@code client.CasingSetVisuals}, keyed by set name.
 */
public class CasingSet {

    /** Every block kind a set can hold. A set either generates a part or points at an existing block. */
    public enum Part {
        CASING, SHAFT, COGWHEEL, LARGE_COGWHEEL, FLUID_PIPE, GEARBOX, PRESS, MIXER, DEPOT, CHAIN_DRIVE,
        CHAIN_GEARSHIFT, CONFIGURABLE_GEARBOX, CHAIN_CONVEYOR, GEARSHIFT, CLUTCH, AUTO_CLUTCH, DEPLOYER,
        STORAGE_INTERFACE, ENCASED_FAN, HARVESTER, SAW, DRILL, PLOUGH, ROLLER
    }

    private final String name;
    private final EnumSet<Part> generated;
    private final EnumMap<Part, Supplier<? extends Block>> blocks = new EnumMap<>(Part.class);
    private @Nullable Supplier<? extends BlockItem> verticalGearboxItem;
    private @Nullable CasingType beltCasingType;

    private final boolean belt;
    private final boolean encasedCustomShaft;
    private final boolean encasedCustomCogwheel;
    private final boolean encasedCustomLargeCogwheel;
    private final boolean encasedCustomPipe;

    protected CasingSet(String name, Options options) {
        this.name = name;
        generated = options.generated.isEmpty() ? EnumSet.noneOf(Part.class) : EnumSet.copyOf(options.generated);
        blocks.putAll(options.existing);
        verticalGearboxItem = options.existingVerticalGearboxItem;
        belt = options.belt;
        encasedCustomShaft = options.encasedCustomShaft;
        encasedCustomCogwheel = options.encasedCustomCogwheel;
        encasedCustomLargeCogwheel = options.encasedCustomLargeCogwheel;
        encasedCustomPipe = options.encasedCustomPipe;
    }

    public String getName() {
        return name;
    }

    public boolean generates(Part part) {
        return generated.contains(part);
    }

    public @Nullable Block get(Part part) {
        Supplier<? extends Block> supplier = blocks.get(part);
        return supplier == null ? null : supplier.get();
    }

    public @Nullable Supplier<? extends Block> getSupplier(Part part) {
        return blocks.get(part);
    }

    public void set(Part part, Block block) {
        if (blocks.containsKey(part))
            throw new UnsupportedOperationException("You cannot modify the " + part + " of casing set " + name + ", it has already been set");
        blocks.put(part, () -> block);
    }

    public boolean doesGenerateBelt() {
        return belt;
    }

    public boolean doesGenerateEncasedCustomShaft() {
        return encasedCustomShaft;
    }

    public boolean doesGenerateEncasedCustomCogwheel() {
        return encasedCustomCogwheel;
    }

    public boolean doesGenerateEncasedCustomLargeCogwheel() {
        return encasedCustomLargeCogwheel;
    }

    public boolean doesGenerateEncasedCustomPipe() {
        return encasedCustomPipe;
    }

    public @Nullable Block getCasing() {
        return get(Part.CASING);
    }

    public @Nullable BlockItem getVerticalGearboxItem() {
        return verticalGearboxItem == null ? null : verticalGearboxItem.get();
    }

    public void setVerticalGearboxItem(BlockItem item) {
        if (verticalGearboxItem != null)
            throw new UnsupportedOperationException("You cannot modify a vertical gearbox item that has already been referenced");
        verticalGearboxItem = () -> item;
    }

    public @Nullable CasingType getBeltCasingType() {
        return beltCasingType;
    }

    public void setBeltCasingType(CasingType type) {
        if (beltCasingType != null)
            throw new UnsupportedOperationException("You cannot modify a belt casing type that has already been referenced");
        beltCasingType = type;
    }

    public boolean isInSet(Block block) {
        for (Supplier<? extends Block> supplier : blocks.values())
            if (supplier.get() == block)
                return true;
        return false;
    }

    /** The part a block plays in this set, or null. */
    public @Nullable Part partOf(Block block) {
        for (Map.Entry<Part, Supplier<? extends Block>> entry : blocks.entrySet())
            if (entry.getValue().get() == block)
                return entry.getKey();
        return null;
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

    /**
     * Builder for a set. The method names are upstream's, minus the sprite and partial model
     * arguments, which moved to the client side.
     */
    public static class Options {
        private final EnumSet<Part> generated = EnumSet.noneOf(Part.class);
        private final EnumMap<Part, Supplier<? extends Block>> existing = new EnumMap<>(Part.class);
        private @Nullable Supplier<? extends BlockItem> existingVerticalGearboxItem;
        private boolean belt;
        private boolean encasedCustomShaft;
        private boolean encasedCustomCogwheel;
        private boolean encasedCustomLargeCogwheel;
        private boolean encasedCustomPipe;

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

        public Options existingCasing(Supplier<? extends Block> casing) {
            return existing(Part.CASING, casing);
        }

        public Options casing() {
            Preconditions.checkState(!existing.containsKey(Part.CASING), "Cannot create a casing if an existing casing was already set");
            return generate(Part.CASING);
        }

        public Options existingGearbox(Supplier<? extends Block> gearbox, Supplier<? extends BlockItem> verticalItem) {
            existingVerticalGearboxItem = verticalItem;
            return existing(Part.GEARBOX, gearbox);
        }

        public Options belt() {
            belt = true;
            return this;
        }

        public Options encasedCustomTransmissionBlocks() {
            encasedCustomShaft = true;
            encasedCustomCogwheel = true;
            encasedCustomLargeCogwheel = true;
            return this;
        }

        public Options encasedCustomPipe() {
            encasedCustomPipe = true;
            return this;
        }

        public Options fluids() {
            return generate(Part.FLUID_PIPE).encasedCustomPipe();
        }

        public Options simpleTransmissions() {
            return generate(Part.SHAFT, Part.COGWHEEL, Part.LARGE_COGWHEEL);
        }

        public Options configurableGearbox() {
            return generate(Part.CONFIGURABLE_GEARBOX);
        }

        public Options autoClutch() {
            return generate(Part.AUTO_CLUTCH);
        }

        public Options complexTransmissionBlocks() {
            return generate(Part.GEARBOX, Part.CHAIN_DRIVE, Part.CHAIN_GEARSHIFT, Part.CONFIGURABLE_GEARBOX,
                Part.CHAIN_CONVEYOR, Part.GEARSHIFT, Part.CLUTCH, Part.AUTO_CLUTCH);
        }

        /** Upstream also listed the Slice and Dice slicer here; that mod has no Fabric 26.2 release. */
        public Options processingBlocks() {
            return generate(Part.PRESS, Part.MIXER, Part.DEPOT, Part.DEPLOYER, Part.ENCASED_FAN);
        }

        public Options contraptionBlocks() {
            return generate(Part.STORAGE_INTERFACE, Part.HARVESTER, Part.SAW, Part.DRILL, Part.PLOUGH, Part.ROLLER);
        }

        public Options everythingExceptCasing() {
            return contraptionBlocks().encasedCustomTransmissionBlocks().simpleTransmissions().belt()
                .processingBlocks().complexTransmissionBlocks().fluids();
        }

        public Options everything() {
            return casing().everythingExceptCasing();
        }
    }
}
