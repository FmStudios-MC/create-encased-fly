package fr.iglee42.createcasing.client.ponder;

import com.zurrtum.create.client.infrastructure.ponder.AllCreatePonderTags;
import com.zurrtum.create.client.infrastructure.ponder.scenes.*;
import com.zurrtum.create.client.infrastructure.ponder.scenes.fluid.*;
import com.zurrtum.create.client.infrastructure.ponder.scenes.highLogistics.FrogAndConveyorScenes;
import com.zurrtum.create.client.ponder.api.registration.PonderPlugin;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import com.zurrtum.create.client.ponder.api.registration.PonderTagRegistrationHelper;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSet.Part;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import fr.iglee42.createcasing.registries.EncasedBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

/**
 * Ponder: the mod's blocks reuse Create's scenes (registered under Create's plugin, so the scene
 * schematics resolve in Create's namespace, as upstream did), plus three scenes and a tag of its
 * own.
 */
public final class EncasedPonders {
    public static final Identifier ENCASED_BLOCKS = CreateCasing.asResource("encased_blocks");

    private EncasedPonders() {
    }

    private static PonderSceneRegistrationHelper<ItemLike> items(PonderSceneRegistrationHelper<Identifier> helper) {
        return helper.withKeyFunction(item -> BuiltInRegistries.ITEM.getKey(item.asItem()));
    }

    private static ItemLike[] casing(Part part) {
        List<ItemLike> list = new ArrayList<>();
        for (CasingSet set : CasingSets.getSets())
            if (set.generates(part))
                list.add(set.get(part));
        return list.toArray(ItemLike[]::new);
    }

    private static ItemLike[] fluid(FluidSet.Part part) {
        List<ItemLike> list = new ArrayList<>();
        for (FluidSet set : FluidSets.getSets())
            if (set.generates(part))
                list.add(set.get(part));
        return list.toArray(ItemLike[]::new);
    }

    private static ItemLike[] verticalGearboxes() {
        List<ItemLike> list = new ArrayList<>();
        for (CasingSet set : CasingSets.getSets())
            if (set.getVerticalGearboxItem() != null && set.generates(Part.GEARBOX))
                list.add(set.getVerticalGearboxItem());
        return list.toArray(ItemLike[]::new);
    }

    /** Create's scenes for the mod's variants of Create's blocks. */
    public static class CreateScenes implements PonderPlugin {
        @Override
        public String getModId() {
            return "create";
        }

        @Override
        public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
            PonderSceneRegistrationHelper<ItemLike> h = items(helper);
            h.forComponents(casing(Part.GEARBOX)).addStoryBoard("gearbox", KineticsScenes::gearbox, AllCreatePonderTags.KINETIC_RELAYS);
            h.forComponents(verticalGearboxes()).addStoryBoard("gearbox", KineticsScenes::gearbox, AllCreatePonderTags.KINETIC_RELAYS);
            h.forComponents(casing(Part.MIXER)).addStoryBoard("mechanical_mixer/mixing", ProcessingScenes::mixing);
            h.forComponents(casing(Part.PRESS))
                .addStoryBoard("mechanical_press/pressing", ProcessingScenes::pressing)
                .addStoryBoard("mechanical_press/compacting", ProcessingScenes::compacting);
            h.forComponents(casing(Part.DEPOT)).addStoryBoard("depot", BeltScenes::depot);
            h.forComponents(casing(Part.CHAIN_DRIVE))
                .addStoryBoard("chain_drive/relay", ChainDriveScenes::chainDriveAsRelay)
                .addStoryBoard("chain_drive/gearshift", ChainDriveScenes::adjustableChainGearshift);
            h.forComponents(casing(Part.CHAIN_GEARSHIFT)).addStoryBoard("chain_drive/gearshift", ChainDriveScenes::adjustableChainGearshift);
            h.forComponents(casing(Part.CHAIN_CONVEYOR)).addStoryBoard("high_logistics/chain_conveyor", FrogAndConveyorScenes::conveyor);
            h.forComponents(casing(Part.CLUTCH)).addStoryBoard("clutch", KineticsScenes::clutch, AllCreatePonderTags.KINETIC_RELAYS);
            h.forComponents(casing(Part.GEARSHIFT)).addStoryBoard("gearshift", KineticsScenes::gearshift, AllCreatePonderTags.KINETIC_RELAYS);
            h.forComponents(casing(Part.DEPLOYER))
                .addStoryBoard("deployer/filter", DeployerScenes::filter, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("deployer/modes", DeployerScenes::modes)
                .addStoryBoard("deployer/processing", DeployerScenes::processing)
                .addStoryBoard("deployer/redstone", DeployerScenes::redstone)
                .addStoryBoard("deployer/contraption", DeployerScenes::contraption, AllCreatePonderTags.CONTRAPTION_ACTOR);
            h.forComponents(casing(Part.STORAGE_INTERFACE))
                .addStoryBoard("portable_interface/transfer", MovementActorScenes::psiTransfer, AllCreatePonderTags.CONTRAPTION_ACTOR)
                .addStoryBoard("portable_interface/redstone", MovementActorScenes::psiRedstone);
            h.forComponents(casing(Part.ENCASED_FAN))
                .addStoryBoard("fan/direction", FanScenes::direction, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("fan/processing", FanScenes::processing);
            h.forComponents(casing(Part.HARVESTER)).addStoryBoard("harvester", MovementActorScenes::harvester);
            h.forComponents(casing(Part.PLOUGH)).addStoryBoard("plough", MovementActorScenes::plough);
            h.forComponents(casing(Part.ROLLER))
                .addStoryBoard("mechanical_roller/clear_and_pave", RollerScenes::clearAndPave)
                .addStoryBoard("mechanical_roller/fill", RollerScenes::fill);
            h.forComponents(casing(Part.SAW))
                .addStoryBoard("mechanical_saw/processing", MechanicalSawScenes::processing, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("mechanical_saw/breaker", MechanicalSawScenes::treeCutting)
                .addStoryBoard("mechanical_saw/contraption", MechanicalSawScenes::contraption, AllCreatePonderTags.CONTRAPTION_ACTOR);
            h.forComponents(casing(Part.DRILL))
                .addStoryBoard("mechanical_drill/breaker", MechanicalDrillScenes::breaker, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("mechanical_drill/contraption", MechanicalDrillScenes::contraption, AllCreatePonderTags.CONTRAPTION_ACTOR);

            h.forComponents(fluid(FluidSet.Part.FLUID_PIPE))
                .addStoryBoard("fluid_pipe/flow", PipeScenes::flow, AllCreatePonderTags.FLUIDS)
                .addStoryBoard("fluid_pipe/interaction", PipeScenes::interaction)
                .addStoryBoard("fluid_pipe/encasing", PipeScenes::encasing);
            h.forComponents(fluid(FluidSet.Part.PUMP))
                .addStoryBoard("mechanical_pump/flow", PumpScenes::flow, AllCreatePonderTags.FLUIDS, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("mechanical_pump/speed", PumpScenes::speed);
            h.forComponents(fluid(FluidSet.Part.FLUID_VALVE))
                .addStoryBoard("fluid_valve", PipeScenes::valve, AllCreatePonderTags.FLUIDS, AllCreatePonderTags.KINETIC_APPLIANCES);
            h.forComponents(fluid(FluidSet.Part.SMART_FLUID_PIPE)).addStoryBoard("smart_pipe", PipeScenes::smart, AllCreatePonderTags.FLUIDS);
            h.forComponents(fluid(FluidSet.Part.FLUID_TANK))
                .addStoryBoard("fluid_tank/storage", FluidTankScenes::storage, AllCreatePonderTags.FLUIDS)
                .addStoryBoard("fluid_tank/sizes", FluidTankScenes::sizes);
            h.forComponents(fluid(FluidSet.Part.HOSE_PULLEY))
                .addStoryBoard("hose_pulley/intro", HosePulleyScenes::intro, AllCreatePonderTags.FLUIDS, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("hose_pulley/level", HosePulleyScenes::level)
                .addStoryBoard("hose_pulley/infinite", HosePulleyScenes::infinite);
            h.forComponents(fluid(FluidSet.Part.SPOUT)).addStoryBoard("spout", SpoutScenes::filling, AllCreatePonderTags.FLUIDS);
            h.forComponents(fluid(FluidSet.Part.ITEM_DRAIN)).addStoryBoard("item_drain", DrainScenes::emptying, AllCreatePonderTags.FLUIDS);
            h.forComponents(fluid(FluidSet.Part.PORTABLE_FLUID_INTERFACE))
                .addStoryBoard("portable_interface/transfer_fluid", FluidMovementActorScenes::transfer, AllCreatePonderTags.FLUIDS, AllCreatePonderTags.CONTRAPTION_ACTOR)
                .addStoryBoard("portable_interface/redstone_fluid", MovementActorScenes::psiRedstone);
            h.forComponents(fluid(FluidSet.Part.WHISTLE)).addStoryBoard("steam_whistle", SteamScenes::whistle);
            h.forComponents(fluid(FluidSet.Part.STEAM_ENGINE)).addStoryBoard("steam_engine", SteamScenes::engine);
            h.forComponents(fluid(FluidSet.Part.VALVE_HANDLE)).addStoryBoard("valve_handle", KineticsScenes::valveHandle, AllCreatePonderTags.KINETIC_SOURCES);
        }
    }

    /** The mod's own scenes and tag. */
    public static class OwnScenes implements PonderPlugin {
        @Override
        public String getModId() {
            return CreateCasing.MODID;
        }

        @Override
        public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
            PonderSceneRegistrationHelper<ItemLike> h = items(helper);
            h.forComponents(EncasedBlocks.CREATIVE_COGWHEEL).addStoryBoard("creative_cogwheel", CustomPonderScenes::creativeCogwheel, AllCreatePonderTags.KINETIC_SOURCES);
            h.forComponents(casing(Part.CONFIGURABLE_GEARBOX)).addStoryBoard("configurable_gearbox", CustomPonderScenes::configurableGearbox);
            h.forComponents(casing(Part.AUTO_CLUTCH)).addStoryBoard("auto_clutch", CustomPonderScenes::autoClutch);
        }

        @Override
        public void registerTags(PonderTagRegistrationHelper<Identifier> helper) {
            PonderTagRegistrationHelper<ItemLike> h = helper.withKeyFunction(item -> BuiltInRegistries.ITEM.getKey(item.asItem()));
            helper.registerTag(ENCASED_BLOCKS).item(CasingSets.BRASS.get(Part.GEARBOX), true, false)
                .title("Create Encased")
                .description("Components added by Create Encased")
                .addToIndex()
                .register();
            h.addToTag(AllCreatePonderTags.CREATIVE).add(EncasedBlocks.CREATIVE_COGWHEEL);
            h.addToTag(ENCASED_BLOCKS).add(EncasedBlocks.CREATIVE_COGWHEEL)
                .add(CasingSets.ANDESITE.get(Part.CONFIGURABLE_GEARBOX)).add(CasingSets.ANDESITE.get(Part.AUTO_CLUTCH));
            h.addToTag(AllCreatePonderTags.KINETIC_RELAYS)
                .add(CasingSets.ANDESITE.get(Part.CONFIGURABLE_GEARBOX)).add(CasingSets.ANDESITE.get(Part.AUTO_CLUTCH));
        }
    }
}
