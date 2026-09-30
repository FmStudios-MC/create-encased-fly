package fr.iglee42.createcasing.client;

import com.zurrtum.create.client.AllCasings;
import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.client.content.decoration.encasing.EncasedCTBehaviour;
import com.zurrtum.create.client.content.fluids.tank.FluidTankCTBehaviour;
import com.zurrtum.create.catnip.data.Couple;
import com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.infrastructure.model.BracketedKineticBlockModel;
import com.zurrtum.create.client.infrastructure.model.CTModel;
import com.zurrtum.create.client.infrastructure.model.FluidTankModel;
import com.zurrtum.create.client.infrastructure.model.PipeAttachmentModel;
import com.zurrtum.create.content.fluids.pipes.EncasedPipeBlock;
import com.zurrtum.create.content.kinetics.gearbox.GearboxBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import fr.iglee42.createcasing.blocks.ConfigurableGearboxBlock;
import fr.iglee42.createcasing.blocks.customs.EncasedCustomCogwheelBlock;
import fr.iglee42.createcasing.blocks.fluids.EncasedCustomPipeBlock;
import fr.iglee42.createcasing.blocks.shafts.EncasedCustomShaftBlock;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSet.Part;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.client.model.EncasedPipeAttachmentModel;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import fr.iglee42.createcasing.registries.EncasedBlocks;
import fr.iglee42.createcasing.transmissions.TransmissionSet;
import fr.iglee42.createcasing.transmissions.TransmissionSets;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

/**
 * Block model wrappers (connected textures, pipe attachments, tanks, bracketed shafts) and casing
 * connectivity. Upstream attached these in its Registrate builders ({@code connectedTextures},
 * {@code casingConnectivity}, {@code blockModel}); Create Fly keeps them in client registries
 * keyed by block ({@link AllModels}, {@link AllCasings}). Only the mod's own blocks are touched.
 */
public class EncasedModels {

    public static void register() {
        for (CasingSet set : CasingSets.getSets()) {
            CasingSetVisuals visuals = CasingSetVisuals.of(set);
            CTSpriteShiftEntry ct = visuals == null ? null : visuals.casing();
            if (ct == null) {
                // the iron sets have no connected textures, but their encased pipes still need rims
                if (set.generates(Part.FLUID_PIPE))
                    AllModels.register(set.get(Part.FLUID_PIPE), PipeAttachmentModel::new);
                continue;
            }
            if (set.generates(Part.CASING))
                casing(set.get(Part.CASING), ct);
            if (set.generates(Part.SHAFT))
                encasedShaft(set.get(Part.SHAFT), ct);
            if (set.generates(Part.COGWHEEL))
                encasedCogwheel(set.get(Part.COGWHEEL), ct, visuals);
            if (set.generates(Part.LARGE_COGWHEEL))
                encasedLargeCogwheel(set.get(Part.LARGE_COGWHEEL), ct);
            if (set.generates(Part.FLUID_PIPE)) {
                Block pipe = set.get(Part.FLUID_PIPE);
                AllModels.register(pipe, (state, unbaked) -> new PipeAttachmentModel.Encased(state, new CTModel(state, unbaked, new EncasedCTBehaviour(ct))));
                AllCasings.make(pipe, ct, (s, f) -> !s.getValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)));
            }
            if (set.generates(Part.GEARBOX)) {
                Block gearbox = set.get(Part.GEARBOX);
                AllModels.register(gearbox, CTModel.of(new EncasedCTBehaviour(ct)));
                AllCasings.make(gearbox, ct, (s, f) -> f.getAxis() == s.getValue(GearboxBlock.AXIS));
            }
            if (set.generates(Part.CONFIGURABLE_GEARBOX)) {
                Block gearbox = set.get(Part.CONFIGURABLE_GEARBOX);
                AllModels.register(gearbox, CTModel.of(new EncasedCTBehaviour(ct)));
                AllCasings.make(gearbox, ct, (s, f) -> !s.getValue(ConfigurableGearboxBlock.getPropertyByDirection(f)));
            }
        }

        for (Block block : EncasedBlocks.ALL) {
            if (block instanceof EncasedCustomShaftBlock shaft) {
                CTSpriteShiftEntry ct = casingSprite(shaft.getCasing());
                if (ct != null)
                    encasedShaft(block, ct);
            } else if (block instanceof EncasedCustomCogwheelBlock cog) {
                CasingSetVisuals visuals = visualsOf(cog.getCasing());
                if (visuals == null || visuals.casing() == null)
                    continue;
                if (cog.isLargeCog())
                    encasedLargeCogwheel(block, visuals.casing());
                else
                    encasedCogwheel(block, visuals.casing(), visuals);
            } else if (block instanceof EncasedCustomPipeBlock pipe) {
                String fluidSet = fluidSetOf(pipe.getPipe());
                CTSpriteShiftEntry ct = casingSprite(pipe.getCasing());
                if (ct != null) {
                    AllModels.register(block, EncasedPipeAttachmentModel.encased(fluidSet, new EncasedCTBehaviour(ct)));
                    AllCasings.make(block, ct, (s, f) -> !s.getValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)));
                } else {
                    AllModels.register(block, EncasedPipeAttachmentModel.of(fluidSet));
                }
            }
        }

        for (TransmissionSet set : TransmissionSets.getSets()) {
            if (set.doesGenerateShaft())
                AllModels.register(set.getShaft(), BracketedKineticBlockModel::new);
            if (set.doesGenerateCogwheel())
                AllModels.register(set.getCogwheel(), BracketedKineticBlockModel::new);
            if (set.doesGenerateLargeCogwheel())
                AllModels.register(set.getLargeCogwheel(), BracketedKineticBlockModel::new);
        }

        for (FluidSet set : FluidSets.getSets()) {
            String name = set.getName();
            for (FluidSet.Part part : new FluidSet.Part[]{FluidSet.Part.FLUID_PIPE, FluidSet.Part.GLASS_FLUID_PIPE, FluidSet.Part.PUMP,
                FluidSet.Part.SMART_FLUID_PIPE, FluidSet.Part.FLUID_VALVE}) {
                if (set.generates(part))
                    AllModels.register(set.get(part), EncasedPipeAttachmentModel.of(name));
            }
            FluidSetVisuals visuals = FluidSetVisuals.of(name);
            if (set.generates(FluidSet.Part.FLUID_TANK) && visuals != null) {
                FluidTankCTBehaviour behaviour = new FluidTankCTBehaviour(visuals.tankSide(), visuals.tankTop(), visuals.tankInner());
                AllModels.register(set.get(FluidSet.Part.FLUID_TANK), (state, unbaked) -> new FluidTankModel(state, unbaked, behaviour));
            }
        }
    }

    private static void casing(Block block, CTSpriteShiftEntry ct) {
        AllModels.register(block, CTModel.of(new EncasedCTBehaviour(ct)));
        AllCasings.make(block, ct);
    }

    private static void encasedShaft(Block block, CTSpriteShiftEntry ct) {
        AllModels.register(block, CTModel.of(new EncasedCTBehaviour(ct)));
        AllCasings.make(block, ct, (s, f) -> f.getAxis() != s.getValue(EncasedShaftBlock.AXIS));
    }

    private static void encasedCogwheel(Block block, CTSpriteShiftEntry ct, CasingSetVisuals visuals) {
        if (visuals.cogSide() != null && visuals.cogOtherSide() != null)
            AllModels.register(block, CTModel.of(new EncasedCogCTBehaviour(ct, Couple.create(visuals.cogSide(), visuals.cogOtherSide()))));
        else
            AllModels.register(block, CTModel.of(new EncasedCTBehaviour(ct)));
        cogConnectivity(block, ct);
    }

    private static void encasedLargeCogwheel(Block block, CTSpriteShiftEntry ct) {
        AllModels.register(block, CTModel.of(new EncasedCogCTBehaviour(ct, null)));
        cogConnectivity(block, ct);
    }

    private static void cogConnectivity(Block block, CTSpriteShiftEntry ct) {
        AllCasings.make(block, ct, (s, f) -> f.getAxis() == s.getValue(EncasedCogwheelBlock.AXIS)
            && !s.getValue(f.getAxisDirection() == AxisDirection.POSITIVE ? EncasedCogwheelBlock.TOP_SHAFT : EncasedCogwheelBlock.BOTTOM_SHAFT));
    }

    private static @Nullable CasingSetVisuals visualsOf(Block casing) {
        for (CasingSet set : CasingSets.getSets())
            if (set.getCasing() == casing)
                return CasingSetVisuals.of(set);
        return null;
    }

    private static @Nullable CTSpriteShiftEntry casingSprite(Block casing) {
        CasingSetVisuals visuals = visualsOf(casing);
        return visuals == null ? null : visuals.casing();
    }

    private static String fluidSetOf(Block pipe) {
        for (FluidSet set : FluidSets.getSets())
            if (set.isInSet(pipe))
                return set.getName();
        return "copper";
    }
}
