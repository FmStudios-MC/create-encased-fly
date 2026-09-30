package fr.iglee42.createcasing.utils;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.zurrtum.create.content.kinetics.saw.SawBlock;
import fr.iglee42.createcasing.blockEntities.AutoClutchBlockEntity;
import fr.iglee42.createcasing.blocks.ConfigurableGearboxBlock;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSet.Part;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.config.EncasedConfigs;
import fr.iglee42.createcasing.transmissions.TransmissionSet;
import fr.iglee42.createcasing.transmissions.TransmissionSets;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

/**
 * Right-clicking a machine with another set's casing swaps it into that casing; right-clicking a
 * shaft or cogwheel with a material swaps its material. Upstream listened to NeoForge's
 * {@code RightClickBlock}; this is Fabric's {@link UseBlockCallback}, which runs on both sides
 * like the NeoForge event did.
 */
public class ItemChangeBlockManager {

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty() || player.isSpectator())
                return InteractionResult.PASS;
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (state.isAir())
                return InteractionResult.PASS;
            return onRightClick(level, pos, state, stack.getItem(), hitResult.getDirection());
        });
    }

    private static InteractionResult onRightClick(Level level, BlockPos pos, BlockState state, Item item, Direction face) {
        CasingSet casingSet = getSetForCasing(item);
        if (casingSet != null && EncasedConfigs.common().kinetics.casingBlockSwappable.get()) {
            if (casingSet.isInSet(state.getBlock()))
                return InteractionResult.PASS;
            BlockState newState = swapCasing(level, pos, state, casingSet, face);
            if (newState != null)
                return changeBlock(level, pos, newState);
        }

        TransmissionSet transmissionSet = getSetForItem(item);
        if (transmissionSet != null && EncasedConfigs.common().kinetics.shaftCogwheelsSwappable.get()) {
            if (transmissionSet.isInSet(state.getBlock()))
                return InteractionResult.PASS;
            Block target = null;
            if (isElementInSet(state, TransmissionSet::getShaft))
                target = transmissionSet.getShaft();
            else if (isElementInSet(state, TransmissionSet::getCogwheel))
                target = transmissionSet.getCogwheel();
            else if (isElementInSet(state, TransmissionSet::getLargeCogwheel))
                target = transmissionSet.getLargeCogwheel();
            if (target != null && state.getBlock() instanceof RotatedPillarKineticBlock)
                return changeBlock(level, pos, target.defaultBlockState().setValue(AXIS, state.getValue(AXIS)));
        }
        return InteractionResult.PASS;
    }

    private static @Nullable BlockState swapCasing(Level level, BlockPos pos, BlockState state, CasingSet set, Direction face) {
        Part part = partOf(state.getBlock());
        if (part == null)
            return null;
        Block target = set.get(part);
        if (target == null)
            return null;
        BlockState newState = target.defaultBlockState();
        return switch (part) {
            case GEARBOX, CHAIN_DRIVE, CHAIN_GEARSHIFT, GEARSHIFT, CLUTCH, AUTO_CLUTCH -> withAxis(state, newState);
            case PRESS, HARVESTER, PLOUGH, ROLLER -> copy(state, newState, HORIZONTAL_FACING);
            case DEPLOYER, STORAGE_INTERFACE, ENCASED_FAN, DRILL -> copy(state, newState, FACING);
            case SAW -> copy(state, newState.setValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE, state.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE))
                .setValue(SawBlock.FLIPPED, state.getValue(SawBlock.FLIPPED)), FACING);
            case MIXER, CHAIN_CONVEYOR -> newState;
            case DEPOT -> face == Direction.UP ? null : newState;
            case CONFIGURABLE_GEARBOX -> {
                for (Direction dir : Iterate.directions) {
                    Property<Boolean> property = ConfigurableGearboxBlock.getPropertyByDirection(dir);
                    newState = newState.setValue(property, state.getValue(property));
                }
                yield newState;
            }
            default -> null;
        };
    }

    private static @Nullable Part partOf(Block block) {
        for (CasingSet set : CasingSets.getSets()) {
            Part part = set.partOf(block);
            if (part != null)
                return part;
        }
        return null;
    }

    private static @Nullable BlockState withAxis(BlockState state, BlockState newState) {
        if (!(state.getBlock() instanceof RotatedPillarKineticBlock))
            return null;
        return newState.setValue(AXIS, state.getValue(AXIS));
    }

    private static <T extends Comparable<T>> @Nullable BlockState copy(BlockState state, BlockState newState, Property<T> property) {
        if (!state.hasProperty(property))
            return null;
        return newState.setValue(property, state.getValue(property));
    }

    private static InteractionResult changeBlock(Level level, BlockPos pos, BlockState newState) {
        AutoClutchBlockEntity oldClutch = level.getBlockEntity(pos) instanceof AutoClutchBlockEntity be ? be : null;
        int configuredValue = oldClutch == null ? 0 : oldClutch.getConfiguredValue();
        AutoClutchBlockEntity.Mode mode = oldClutch == null ? null : oldClutch.getMode();
        AutoClutchBlockEntity.Operation operation = oldClutch == null ? null : oldClutch.getOperation();

        level.setBlockAndUpdate(pos, newState);
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(newState));

        if (oldClutch != null && level.getBlockEntity(pos) instanceof AutoClutchBlockEntity be) {
            be.setConfiguredValue(configuredValue);
            be.setMode(mode);
            be.setOperation(operation);
        }
        return InteractionResult.SUCCESS;
    }

    private static @Nullable CasingSet getSetForCasing(Item casing) {
        for (CasingSet set : CasingSets.getSets()) {
            Block block = set.getCasing();
            if (block != null && block.asItem() == casing)
                return set;
        }
        return null;
    }

    private static @Nullable TransmissionSet getSetForItem(Item item) {
        for (TransmissionSet set : TransmissionSets.getSets()) {
            if (set.getItem() == item)
                return set;
        }
        return null;
    }

    private static boolean isElementInSet(BlockState state, Function<TransmissionSet, Block> function) {
        for (TransmissionSet set : TransmissionSets.getSets()) {
            Block block = function.apply(set);
            if (block != null && state.is(block))
                return true;
        }
        return false;
    }
}
