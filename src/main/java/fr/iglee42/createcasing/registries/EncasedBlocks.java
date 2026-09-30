package fr.iglee42.createcasing.registries;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.contraptions.actors.plough.PloughBlock;
import com.zurrtum.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import com.zurrtum.create.content.contraptions.actors.roller.RollerBlock;
import com.zurrtum.create.content.contraptions.actors.roller.RollerBlockItem;
import com.zurrtum.create.content.contraptions.actors.harvester.HarvesterBlock;
import com.zurrtum.create.content.decoration.encasing.CasingBlock;
import com.zurrtum.create.content.decoration.encasing.EncasingRegistry;
import com.zurrtum.create.content.fluids.drain.ItemDrainBlock;
import com.zurrtum.create.content.fluids.hosePulley.HosePulleyBlock;
import com.zurrtum.create.content.fluids.pipes.EncasedPipeBlock;
import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import com.zurrtum.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.zurrtum.create.content.fluids.pipes.valve.FluidValveBlock;
import com.zurrtum.create.content.fluids.pump.PumpBlock;
import com.zurrtum.create.content.fluids.spout.SpoutBlock;
import com.zurrtum.create.content.fluids.tank.FluidTankItem;
import com.zurrtum.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import com.zurrtum.create.content.kinetics.crank.ValveHandleBlock;
import com.zurrtum.create.content.kinetics.deployer.DeployerBlock;
import com.zurrtum.create.content.kinetics.drill.DrillBlock;
import com.zurrtum.create.content.kinetics.fan.EncasedFanBlock;
import com.zurrtum.create.content.kinetics.mixer.MechanicalMixerBlock;
import com.zurrtum.create.content.kinetics.press.MechanicalPressBlock;
import com.zurrtum.create.content.kinetics.saw.SawBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.zurrtum.create.content.kinetics.simpleRelays.ShaftBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.zurrtum.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.zurrtum.create.content.kinetics.transmission.ClutchBlock;
import com.zurrtum.create.content.kinetics.transmission.GearshiftBlock;
import com.zurrtum.create.content.logistics.depot.DepotBlock;
import com.zurrtum.create.content.processing.AssemblyOperatorBlockItem;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.blocks.AutoClutchBlock;
import fr.iglee42.createcasing.blocks.ConfigurableGearboxBlock;
import fr.iglee42.createcasing.blocks.CreativeCogwheelBlock;
import fr.iglee42.createcasing.blocks.cogwheels.CustomCogwheelBlock;
import fr.iglee42.createcasing.blocks.customs.*;
import fr.iglee42.createcasing.blocks.fluids.*;
import fr.iglee42.createcasing.blocks.shafts.CustomShaftBlock;
import fr.iglee42.createcasing.blocks.shafts.EncasedCustomShaftBlock;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSet.Part;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import fr.iglee42.createcasing.transmissions.TransmissionSet;
import fr.iglee42.createcasing.transmissions.TransmissionSets;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers every block of the mod from the casing, transmission and fluid sets.
 * <p>
 * Each block is built from its Create counterpart's properties ({@link Properties#ofFullCopy}),
 * with upstream's map colour on top, and remembers that counterpart in {@link #BASE}. The base
 * block is how the mod's blocks inherit Create's registrations: stress values, movement and
 * interaction behaviours, display sources and mounted storage are looked up through it
 * ({@code BaseBlockProviders}), and client code picks renderers and models from it.
 * <p>
 * Upstream's Registrate builders also generated blockstates, models, loot tables and tags; those
 * JSONs are committed under {@code src/generated/resources}.
 */
public class EncasedBlocks {
    /** Every block of the mod, in registration order. */
    public static final List<Block> ALL = new ArrayList<>();
    /** Each block's Create counterpart, where it has one. */
    public static final Map<Block, Block> BASE = new IdentityHashMap<>();

    public static CreativeCogwheelBlock CREATIVE_COGWHEEL;

    public static void register() {
        CREATIVE_COGWHEEL = register("creative_cogwheel", CreativeCogwheelBlock::new,
            Properties.ofFullCopy(AllBlocks.ANDESITE_CASING).mapColor(MapColor.COLOR_PURPLE).noOcclusion(),
            (block, props) -> new BlockItem(block, props.rarity(Rarity.EPIC)));

        for (CasingSet set : CasingSets.getSets()) {
            String name = set.getName();
            if (set.generates(Part.CASING))
                set.set(Part.CASING, register(name + "_casing", CasingBlock::new, Properties.ofFullCopy(AllBlocks.ANDESITE_CASING), AllBlocks.ANDESITE_CASING));
            Block casing = set.getCasing();

            if (set.generates(Part.SHAFT))
                set.set(Part.SHAFT, encasedShaft(AllBlocks.SHAFT, name + "_encased_shaft", casing, p -> new EncasedShaftBlock(p, casing)));
            if (set.generates(Part.COGWHEEL))
                set.set(Part.COGWHEEL, encasedCogwheel(AllBlocks.COGWHEEL, AllBlocks.ANDESITE_ENCASED_COGWHEEL, name + "_encased_cogwheel", p -> new EncasedCogwheelBlock(p, false, casing)));
            if (set.generates(Part.LARGE_COGWHEEL))
                set.set(Part.LARGE_COGWHEEL, encasedCogwheel(AllBlocks.LARGE_COGWHEEL, AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL, name + "_encased_large_cogwheel", p -> new EncasedCogwheelBlock(p, true, casing)));
            if (set.generates(Part.FLUID_PIPE))
                set.set(Part.FLUID_PIPE, encasedPipe(AllBlocks.FLUID_PIPE, name + "_encased_fluid_pipe", p -> new EncasedPipeBlock(p, casing)));
            if (set.generates(Part.GEARBOX))
                set.set(Part.GEARBOX, register(name + "_gearbox", p -> new CustomGearboxBlock(p, set::getVerticalGearboxItem),
                    Properties.ofFullCopy(AllBlocks.GEARBOX).noOcclusion(), AllBlocks.GEARBOX));
            if (set.generates(Part.PRESS))
                set.set(Part.PRESS, register(name + "_press", MechanicalPressBlock::new, copy(AllBlocks.MECHANICAL_PRESS), AssemblyOperatorBlockItem::new, AllBlocks.MECHANICAL_PRESS));
            if (set.generates(Part.MIXER))
                set.set(Part.MIXER, register(name + "_mixer", MechanicalMixerBlock::new, copy(AllBlocks.MECHANICAL_MIXER), AssemblyOperatorBlockItem::new, AllBlocks.MECHANICAL_MIXER));
            if (set.generates(Part.DEPOT))
                set.set(Part.DEPOT, register(name + "_depot", DepotBlock::new, copy(AllBlocks.DEPOT), AllBlocks.DEPOT));
            if (set.generates(Part.CHAIN_DRIVE))
                set.set(Part.CHAIN_DRIVE, register(name + "_encased_chain_drive", p -> new CustomChainDriveBlock(p, name), copy(AllBlocks.ENCASED_CHAIN_DRIVE), AllBlocks.ENCASED_CHAIN_DRIVE));
            if (set.generates(Part.CHAIN_GEARSHIFT))
                set.set(Part.CHAIN_GEARSHIFT, register(name + "_adjustable_chain_gearshift", p -> new CustomChainGearshiftBlock(p, name), copy(AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT), AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT));
            if (set.generates(Part.CONFIGURABLE_GEARBOX))
                set.set(Part.CONFIGURABLE_GEARBOX, register(name + "_configurable_gearbox", ConfigurableGearboxBlock::new, Properties.ofFullCopy(AllBlocks.GEARBOX).noOcclusion(), AllBlocks.GEARBOX));
            if (set.generates(Part.CHAIN_CONVEYOR))
                set.set(Part.CHAIN_CONVEYOR, register(name + "_chain_conveyor", ChainConveyorBlock::new, copy(AllBlocks.CHAIN_CONVEYOR), AllBlocks.CHAIN_CONVEYOR));
            if (set.generates(Part.GEARSHIFT))
                set.set(Part.GEARSHIFT, register(name + "_gearshift", GearshiftBlock::new, copy(AllBlocks.GEARSHIFT).noOcclusion(), AllBlocks.GEARSHIFT));
            if (set.generates(Part.CLUTCH))
                set.set(Part.CLUTCH, register(name + "_clutch", ClutchBlock::new, copy(AllBlocks.CLUTCH).noOcclusion(), AllBlocks.CLUTCH));
            if (set.generates(Part.AUTO_CLUTCH))
                set.set(Part.AUTO_CLUTCH, register(name + "_automatic_clutch", AutoClutchBlock::new, copy(AllBlocks.CLUTCH).noOcclusion(), AllBlocks.CLUTCH));
            if (set.generates(Part.DEPLOYER))
                set.set(Part.DEPLOYER, register(name + "_deployer", DeployerBlock::new, copy(AllBlocks.DEPLOYER), AssemblyOperatorBlockItem::new, AllBlocks.DEPLOYER));
            if (set.generates(Part.STORAGE_INTERFACE))
                set.set(Part.STORAGE_INTERFACE, register(name + "_portable_storage_interface", PortableStorageInterfaceBlock::forItems, copy(AllBlocks.PORTABLE_STORAGE_INTERFACE), AllBlocks.PORTABLE_STORAGE_INTERFACE));
            if (set.generates(Part.ENCASED_FAN))
                set.set(Part.ENCASED_FAN, register(name + "_encased_fan", EncasedFanBlock::new, copy(AllBlocks.ENCASED_FAN), AllBlocks.ENCASED_FAN));
            if (set.generates(Part.HARVESTER))
                set.set(Part.HARVESTER, register(name + "_mechanical_harvester", HarvesterBlock::new, copy(AllBlocks.MECHANICAL_HARVESTER), AllBlocks.MECHANICAL_HARVESTER));
            if (set.generates(Part.SAW))
                set.set(Part.SAW, register(name + "_mechanical_saw", SawBlock::new, copy(AllBlocks.MECHANICAL_SAW), AllBlocks.MECHANICAL_SAW));
            if (set.generates(Part.DRILL))
                set.set(Part.DRILL, register(name + "_mechanical_drill", DrillBlock::new, copy(AllBlocks.MECHANICAL_DRILL), AllBlocks.MECHANICAL_DRILL));
            if (set.generates(Part.PLOUGH))
                set.set(Part.PLOUGH, register(name + "_mechanical_plough", PloughBlock::new, copy(AllBlocks.MECHANICAL_PLOUGH), AllBlocks.MECHANICAL_PLOUGH));
            if (set.generates(Part.ROLLER))
                set.set(Part.ROLLER, register(name + "_mechanical_roller", RollerBlock::new, copy(AllBlocks.MECHANICAL_ROLLER), RollerBlockItem::new, AllBlocks.MECHANICAL_ROLLER));
        }

        for (TransmissionSet set : TransmissionSets.getSets()) {
            String name = set.getName();
            if (set.doesGenerateShaft()) {
                Function<Properties, ? extends ShaftBlock> factory = set.getShaftConstructor() != null ? set.getShaftConstructor() : CustomShaftBlock::new;
                set.setShaft(register(name + "_shaft", factory, Properties.ofFullCopy(AllBlocks.SHAFT), AllBlocks.SHAFT));
            }
            BiFunction<Properties, Boolean, ? extends CogWheelBlock> cogFactory = set.getCogwheelConstructor() != null
                ? set.getCogwheelConstructor() : (p, large) -> large ? CustomCogwheelBlock.large(p) : CustomCogwheelBlock.small(p);
            if (set.doesGenerateCogwheel())
                set.setCogwheel(register(name + "_cogwheel", p -> cogFactory.apply(p, false), Properties.ofFullCopy(AllBlocks.COGWHEEL), CogwheelBlockItem::new, AllBlocks.COGWHEEL));
            if (set.doesGenerateLargeCogwheel())
                set.setLargeCogwheel(register(name + "_large_cogwheel", p -> cogFactory.apply(p, true), Properties.ofFullCopy(AllBlocks.LARGE_COGWHEEL), CogwheelBlockItem::new, AllBlocks.LARGE_COGWHEEL));
        }

        registerFluidSets();

        for (CasingSet set : CasingSets.getSets()) {
            Block casing = set.getCasing();
            for (TransmissionSet tset : TransmissionSets.getSets()) {
                if (tset.isNotEncasable())
                    continue;
                if (set.doesGenerateEncasedCustomShaft() && tset.doesGenerateShaft()) {
                    ShaftBlock shaft = tset.getShaft();
                    encasedShaft(shaft, set.getName() + "_encased_" + tset.getName() + "_shaft", casing,
                        p -> new EncasedCustomShaftBlock(p, casing, () -> shaft));
                }
            }
            for (TransmissionSet tset : TransmissionSets.getSets()) {
                if (tset.isNotEncasable() || !set.doesGenerateEncasedCustomCogwheel() || !tset.doesGenerateCogwheel())
                    continue;
                CogWheelBlock cog = tset.getCogwheel();
                encasedCogwheel(cog, AllBlocks.ANDESITE_ENCASED_COGWHEEL, set.getName() + "_encased_" + tset.getName() + "_cogwheel",
                    p -> new EncasedCustomCogwheelBlock(p, false, casing, () -> cog));
            }
            for (TransmissionSet tset : TransmissionSets.getSets()) {
                if (tset.isNotEncasable() || !set.doesGenerateEncasedCustomLargeCogwheel() || !tset.doesGenerateLargeCogwheel())
                    continue;
                CogWheelBlock cog = tset.getLargeCogwheel();
                encasedCogwheel(cog, AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL, set.getName() + "_encased_" + tset.getName() + "_large_cogwheel",
                    p -> new EncasedCustomCogwheelBlock(p, true, casing, () -> cog));
            }
        }
    }

    private static void registerFluidSets() {
        for (FluidSet set : FluidSets.getSets()) {
            String name = set.getName();
            if (set.generates(FluidSet.Part.FLUID_PIPE)) {
                FluidPipeBlock pipe = register(name + "_fluid_pipe", CustomFluidPipeBlock::new, copy(AllBlocks.FLUID_PIPE), AllBlocks.FLUID_PIPE);
                set.set(FluidSet.Part.FLUID_PIPE, pipe);
                set.set(FluidSet.Part.GLASS_FLUID_PIPE, registerWithoutItem(name + "_glass_fluid_pipe", CustomGlassFluidPipeBlock::new, copy(AllBlocks.GLASS_FLUID_PIPE), AllBlocks.GLASS_FLUID_PIPE));
            }
            if (set.generates(FluidSet.Part.PUMP))
                set.set(FluidSet.Part.PUMP, register(name + "_mechanical_pump", PumpBlock::new, copy(AllBlocks.MECHANICAL_PUMP), AllBlocks.MECHANICAL_PUMP));
            if (set.generates(FluidSet.Part.SMART_FLUID_PIPE))
                set.set(FluidSet.Part.SMART_FLUID_PIPE, register(name + "_smart_fluid_pipe", SmartFluidPipeBlock::new, copy(AllBlocks.SMART_FLUID_PIPE), AllBlocks.SMART_FLUID_PIPE));
            if (set.generates(FluidSet.Part.FLUID_TANK))
                set.set(FluidSet.Part.FLUID_TANK, register(name + "_fluid_tank", CustomFluidTankBlock::new, copy(AllBlocks.FLUID_TANK), FluidTankItem::new, AllBlocks.FLUID_TANK));
            if (set.generates(FluidSet.Part.STEAM_ENGINE))
                set.set(FluidSet.Part.STEAM_ENGINE, register(name + "_steam_engine", SteamEngineBlock::new, copy(AllBlocks.STEAM_ENGINE), AllBlocks.STEAM_ENGINE));
            if (set.generates(FluidSet.Part.ITEM_DRAIN))
                set.set(FluidSet.Part.ITEM_DRAIN, register(name + "_item_drain", ItemDrainBlock::new, copy(AllBlocks.ITEM_DRAIN), AllBlocks.ITEM_DRAIN));
            if (set.generates(FluidSet.Part.FLUID_VALVE))
                set.set(FluidSet.Part.FLUID_VALVE, register(name + "_fluid_valve", FluidValveBlock::new, copy(AllBlocks.FLUID_VALVE), AllBlocks.FLUID_VALVE));
            if (set.generates(FluidSet.Part.VALVE_HANDLE))
                set.set(FluidSet.Part.VALVE_HANDLE, register(name + "_valve_handle", ValveHandleBlock::copper, copy(AllBlocks.COPPER_VALVE_HANDLE), AllBlocks.COPPER_VALVE_HANDLE));
            if (set.generates(FluidSet.Part.HOSE_PULLEY))
                set.set(FluidSet.Part.HOSE_PULLEY, register(name + "_hose_pulley", HosePulleyBlock::new, copy(AllBlocks.HOSE_PULLEY), AllBlocks.HOSE_PULLEY));
            if (set.generates(FluidSet.Part.PORTABLE_FLUID_INTERFACE))
                set.set(FluidSet.Part.PORTABLE_FLUID_INTERFACE, register(name + "_portable_fluid_interface", PortableStorageInterfaceBlock::forFluids, copy(AllBlocks.PORTABLE_FLUID_INTERFACE), AllBlocks.PORTABLE_FLUID_INTERFACE));
            if (set.generates(FluidSet.Part.WHISTLE))
                set.set(FluidSet.Part.WHISTLE, register(name + "_steam_whistle", CustomWhistleBlock::new, copy(AllBlocks.STEAM_WHISTLE), AllBlocks.STEAM_WHISTLE));
            if (set.generates(FluidSet.Part.SPOUT))
                set.set(FluidSet.Part.SPOUT, register(name + "_spout", SpoutBlock::new, copy(AllBlocks.SPOUT), AssemblyOperatorBlockItem::new, AllBlocks.SPOUT));
        }

        for (CasingSet set : CasingSets.getSets()) {
            if (!set.doesGenerateEncasedCustomPipe())
                continue;
            Block casing = set.getCasing();
            for (FluidSet fset : FluidSets.getSets()) {
                if (fset.isNotEncasable() || !fset.generates(FluidSet.Part.FLUID_PIPE))
                    continue;
                FluidPipeBlock pipe = (FluidPipeBlock) fset.get(FluidSet.Part.FLUID_PIPE);
                encasedPipe(pipe, set.getName() + "_encased_" + fset.getName() + "_fluid_pipe", p -> new EncasedCustomPipeBlock(p, casing, () -> pipe));
            }
        }
    }

    // Kinds registered for many sets

    private static <B extends Block> B encasedShaft(ShaftBlock shaft, String id, Block casing, Function<Properties, B> factory) {
        B block = registerHidden(id, factory, copy(AllBlocks.ANDESITE_ENCASED_SHAFT).noOcclusion(), AllBlocks.ANDESITE_ENCASED_SHAFT);
        ENCASED_VARIANTS.add(new EncasedVariant(shaft, block));
        return block;
    }

    private static <B extends Block> B encasedCogwheel(CogWheelBlock cogwheel, Block base, String id, Function<Properties, B> factory) {
        B block = registerHidden(id, factory, copy(base).noOcclusion(), base);
        ENCASED_VARIANTS.add(new EncasedVariant(cogwheel, block));
        return block;
    }

    private static <B extends Block> B encasedPipe(FluidPipeBlock pipe, String id, Function<Properties, B> factory) {
        B block = registerWithoutItem(id, factory, copy(AllBlocks.ENCASED_FLUID_PIPE), AllBlocks.ENCASED_FLUID_PIPE);
        ENCASED_VARIANTS.add(new EncasedVariant(pipe, block));
        return block;
    }

    /** Encased variants, handed to Create's {@link EncasingRegistry} once all blocks exist. */
    private record EncasedVariant(Block encasable, Block encased) {
    }

    private static final List<EncasedVariant> ENCASED_VARIANTS = new ArrayList<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerEncasedVariants() {
        for (EncasedVariant variant : ENCASED_VARIANTS)
            EncasingRegistry.addVariant((Block & com.zurrtum.create.content.decoration.encasing.EncasableBlock) variant.encasable(),
                (Block & com.zurrtum.create.content.decoration.encasing.EncasedBlock) variant.encased());
    }

    // Registration helpers

    private static Properties copy(Block base) {
        return Properties.ofFullCopy(base);
    }

    private static <B extends Block> B register(String id, Function<Properties, B> factory, Properties properties, @Nullable Block base) {
        return register(id, factory, properties, BlockItem::new, base);
    }

    private static <B extends Block> B register(String id, Function<Properties, B> factory, Properties properties,
                                                BiFunction<Block, Item.Properties, ? extends Item> itemFactory) {
        return register(id, factory, properties, itemFactory, null);
    }

    private static <B extends Block> B register(String id, Function<Properties, B> factory, Properties properties,
                                                BiFunction<Block, Item.Properties, ? extends Item> itemFactory, @Nullable Block base) {
        B block = registerWithoutItem(id, factory, properties, base);
        EncasedItems.registerBlockItem(block, itemFactory, false);
        return block;
    }

    /** Upstream hid the items of encased shafts and cogwheels from the creative tab; they are only obtained by encasing. */
    private static <B extends Block> B registerHidden(String id, Function<Properties, B> factory, Properties properties, @Nullable Block base) {
        B block = registerWithoutItem(id, factory, properties, base);
        EncasedItems.registerBlockItem(block, BlockItem::new, true);
        return block;
    }

    private static <B extends Block> B registerWithoutItem(String id, Function<Properties, B> factory, Properties properties, @Nullable Block base) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, CreateCasing.asResource(id));
        B block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
        ALL.add(block);
        if (base != null)
            BASE.put(block, base);
        return block;
    }

    public static @Nullable Block getBase(Block block) {
        return BASE.get(block);
    }

    public static void forEachShaft(java.util.function.Consumer<? super ShaftBlock> action) {
        TransmissionSets.getSets().stream().filter(TransmissionSet::doesGenerateShaft).forEach(set -> action.accept(set.getShaft()));
    }

    public static Supplier<List<Block>> all() {
        return () -> Collections.unmodifiableList(ALL);
    }
}
