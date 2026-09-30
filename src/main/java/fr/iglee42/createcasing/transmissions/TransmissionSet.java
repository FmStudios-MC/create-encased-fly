package fr.iglee42.createcasing.transmissions;

import com.google.common.base.Preconditions;
import com.zurrtum.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A shaft/cogwheel material (wood types, metals, glass). Upstream also let each set pick a block
 * entity type; in the port the block classes return their type themselves.
 */
public class TransmissionSet {
    private final String name;
    private final Supplier<? extends Item> item;
    private @Nullable Supplier<? extends ShaftBlock> shaftBlock;
    private @Nullable Supplier<? extends CogWheelBlock> cogwheelBlock;
    private @Nullable Supplier<? extends CogWheelBlock> largeCogwheelBlock;
    private final @Nullable Function<BlockBehaviour.Properties, ? extends ShaftBlock> shaftConstructor;
    private final @Nullable BiFunction<BlockBehaviour.Properties, Boolean, ? extends CogWheelBlock> cogwheelConstructor;

    private final boolean shaft;
    private final boolean cogwheel;
    private final boolean largeCogwheel;
    private final boolean notEncasable;
    private final boolean wooden;

    protected TransmissionSet(String name, Options options) {
        this.name = name;
        this.item = options.item;
        shaft = options.shaft;
        cogwheel = options.cogwheel;
        largeCogwheel = options.largeCogwheel;
        notEncasable = options.notEncasable;
        wooden = options.wooden;
        shaftConstructor = options.shaftConstructor;
        cogwheelConstructor = options.cogwheelConstructor;
        shaftBlock = options.existingShaft;
        cogwheelBlock = options.existingCogwheel;
        largeCogwheelBlock = options.existingLargeCogwheel;
    }

    public String getName() {
        return name;
    }

    public boolean doesGenerateCogwheel() {
        return cogwheel;
    }

    public boolean doesGenerateShaft() {
        return shaft;
    }

    public boolean doesGenerateLargeCogwheel() {
        return largeCogwheel;
    }

    public boolean isWooden() {
        return wooden;
    }

    public Item getItem() {
        return item.get();
    }

    public @Nullable ShaftBlock getShaft() {
        return shaftBlock == null ? null : shaftBlock.get();
    }

    public @Nullable CogWheelBlock getCogwheel() {
        return cogwheelBlock == null ? null : cogwheelBlock.get();
    }

    public @Nullable CogWheelBlock getLargeCogwheel() {
        return largeCogwheelBlock == null ? null : largeCogwheelBlock.get();
    }

    public @Nullable Function<BlockBehaviour.Properties, ? extends ShaftBlock> getShaftConstructor() {
        return shaftConstructor;
    }

    public @Nullable BiFunction<BlockBehaviour.Properties, Boolean, ? extends CogWheelBlock> getCogwheelConstructor() {
        return cogwheelConstructor;
    }

    public void setShaft(ShaftBlock shaft) {
        if (shaftBlock != null)
            throw new UnsupportedOperationException("You cannot modify a shaft that has already been referenced");
        shaftBlock = () -> shaft;
    }

    public void setCogwheel(CogWheelBlock cogwheel) {
        if (cogwheelBlock != null)
            throw new UnsupportedOperationException("You cannot modify a cogwheel that has already been referenced");
        cogwheelBlock = () -> cogwheel;
    }

    public void setLargeCogwheel(CogWheelBlock cogwheel) {
        if (largeCogwheelBlock != null)
            throw new UnsupportedOperationException("You cannot modify a large cogwheel that has already been referenced");
        largeCogwheelBlock = () -> cogwheel;
    }

    public boolean isInSet(Block block) {
        return block == getShaft() || block == getCogwheel() || block == getLargeCogwheel();
    }

    public boolean isNotEncasable() {
        return notEncasable;
    }

    public static class Options {
        private Supplier<? extends Item> item;
        private boolean shaft;
        private boolean cogwheel;
        private boolean largeCogwheel;
        private boolean notEncasable;
        private boolean wooden;
        private @Nullable Supplier<? extends ShaftBlock> existingShaft;
        private @Nullable Supplier<? extends CogWheelBlock> existingCogwheel;
        private @Nullable Supplier<? extends CogWheelBlock> existingLargeCogwheel;
        private @Nullable Function<BlockBehaviour.Properties, ? extends ShaftBlock> shaftConstructor;
        private @Nullable BiFunction<BlockBehaviour.Properties, Boolean, ? extends CogWheelBlock> cogwheelConstructor;

        public Options item(Supplier<? extends Item> item) {
            Preconditions.checkNotNull(item, "Item Supplier can't be null");
            this.item = item;
            return this;
        }

        public Options shaft() {
            this.shaft = true;
            return this;
        }

        public Options cogwheel() {
            this.cogwheel = true;
            return this;
        }

        public Options largeCogwheel() {
            this.largeCogwheel = true;
            return this;
        }

        public Options notEncasable() {
            this.notEncasable = true;
            return this;
        }

        public Options wooden() {
            this.wooden = true;
            return this;
        }

        public Options shaftConstructor(Function<BlockBehaviour.Properties, ? extends ShaftBlock> constructor) {
            this.shaftConstructor = constructor;
            return this;
        }

        public Options cogwheelConstructor(BiFunction<BlockBehaviour.Properties, Boolean, ? extends CogWheelBlock> constructor) {
            this.cogwheelConstructor = constructor;
            return this;
        }

        public Options everything(Supplier<? extends Item> item) {
            return item(item).shaft().cogwheel().largeCogwheel();
        }

        Options existingShaft(Supplier<? extends ShaftBlock> shaft) {
            this.existingShaft = shaft;
            this.shaft = false;
            return this;
        }

        Options existingCogwheel(Supplier<? extends CogWheelBlock> cogwheel) {
            this.existingCogwheel = cogwheel;
            this.cogwheel = false;
            return this;
        }

        Options existingLargeCogwheel(Supplier<? extends CogWheelBlock> largeCogwheel) {
            this.existingLargeCogwheel = largeCogwheel;
            this.largeCogwheel = false;
            return this;
        }
    }
}
