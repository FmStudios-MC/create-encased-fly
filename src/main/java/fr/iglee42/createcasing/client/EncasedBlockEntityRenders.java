package fr.iglee42.createcasing.client;

import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.client.AllBlockEntityBehaviours;
import com.zurrtum.create.client.AllBlockEntityRenders;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.fluids.tank.FluidTankRenderer;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedLargeCogRenderer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedSmallCogRenderer;
import com.zurrtum.create.client.content.kinetics.transmission.SplitShaftRenderer;
import com.zurrtum.create.client.content.kinetics.transmission.SplitShaftVisual;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.audio.KineticAudioBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.KineticScrollValueBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.FluidTankTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.GeneratingKineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import fr.iglee42.createcasing.blockEntities.CreativeCogwheelBlockEntity;
import fr.iglee42.createcasing.blocks.CreativeCogwheelBlock;
import fr.iglee42.createcasing.client.render.*;
import fr.iglee42.createcasing.registries.EncasedBlockEntities;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Renderers, Flywheel visuals and client behaviours of the mod's own block entity types. The
 * blocks that joined Create's types get all of these from Create Fly.
 * <p>
 * {@code visual} skips the renderer while Flywheel draws, like upstream's {@code visual(..., false)}.
 */
public class EncasedBlockEntityRenders {

    public static void register() {
        AllBlockEntityRenders.visual(EncasedBlockEntities.CREATIVE_COGWHEEL, CreativeCogwheelRenderer::new,
            SingleAxisRotatingVisual.of(AllPartialModels.SHAFTLESS_COGWHEEL));
        AllBlockEntityRenders.visual(EncasedBlockEntities.AUTOMATIC_CLUTCH, SplitShaftRenderer::new, SplitShaftVisual::new);
        AllBlockEntityRenders.visual(EncasedBlockEntities.CONFIGURABLE_GEARBOX, ConfigurableGearboxRenderer::new, ConfigurableGearboxVisual::new);

        AllBlockEntityRenders.visual(EncasedBlockEntities.WOODEN_SHAFT, TransmissionRenderer::new, TransmissionVisual::create);
        AllBlockEntityRenders.visual(EncasedBlockEntities.GLASS_SHAFT, TransmissionRenderer::new, TransmissionVisual::create);
        AllBlockEntityRenders.visual(EncasedBlockEntities.CUSTOM_SHAFT, TransmissionRenderer::new, TransmissionVisual::create);
        AllBlockEntityRenders.visual(EncasedBlockEntities.WOODEN_COGWHEELS, TransmissionRenderer::new, TransmissionVisual::create);
        AllBlockEntityRenders.visual(EncasedBlockEntities.CUSTOM_COGWHEELS, TransmissionRenderer::new, TransmissionVisual::create);

        AllBlockEntityRenders.visual(EncasedBlockEntities.ENCASED_CUSTOM_SHAFT, EncasedCustomRenderers.Shaft::new,
            (context, be, partialTick) -> new SingleAxisRotatingVisual<>(context, be, partialTick,
                Models.chunkPartial(TransmissionModels.shaft(be.getBlockState().getBlock()))));
        // Create Fly's encased cogwheel renderers; EncasedCogRendererMixin swaps in the inner cogwheel's material.
        AllBlockEntityRenders.visual(EncasedBlockEntities.ENCASED_CUSTOM_COGWHEEL, EncasedSmallCogRenderer::new,
            (context, be, partialTick) -> new EncasedCogVisual(context, be, false, partialTick,
                Models.chunkPartial(TransmissionModels.shaftlessCog(be.getBlockState().getBlock()))));
        AllBlockEntityRenders.visual(EncasedBlockEntities.ENCASED_CUSTOM_LARGE_COGWHEEL, EncasedLargeCogRenderer::new,
            (context, be, partialTick) -> new EncasedCogVisual(context, be, true, partialTick,
                Models.chunkPartial(TransmissionModels.shaftlessLargeCog(be.getBlockState().getBlock()))));

        AllBlockEntityRenders.render(EncasedBlockEntities.FLUID_TANK, FluidTankRenderer::new);
    }

    /** Client halves of block entity behaviours: goggle tooltips, kinetic sounds, the speed box. */
    public static void registerBehaviours() {
        AllBlockEntityBehaviours.add(EncasedBlockEntities.CREATIVE_COGWHEEL, GeneratingKineticTooltipBehaviour::new,
            KineticAudioBehaviour::new, EncasedBlockEntityRenders::creativeCogwheelSpeed);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.AUTOMATIC_CLUTCH, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.CONFIGURABLE_GEARBOX, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.WOODEN_SHAFT, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.GLASS_SHAFT, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.CUSTOM_SHAFT, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.WOODEN_COGWHEELS, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.CUSTOM_COGWHEELS, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.ENCASED_CUSTOM_SHAFT, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.ENCASED_CUSTOM_COGWHEEL, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.ENCASED_CUSTOM_LARGE_COGWHEEL, KineticAudioBehaviour::new, KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(EncasedBlockEntities.FLUID_TANK, FluidTankTooltipBehaviour::new);
    }

    private static KineticScrollValueBehaviour creativeCogwheelSpeed(CreativeCogwheelBlockEntity be) {
        return new KineticScrollValueBehaviour(CreateLang.translateDirect("kinetics.creative_motor.rotation_speed"), be, new CogwheelValueBox());
    }

    /** The speed box sits on the cogwheel's faces along its axis. */
    static class CogwheelValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5f);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return state.getValue(CreativeCogwheelBlock.AXIS) == direction.getAxis();
        }
    }
}
