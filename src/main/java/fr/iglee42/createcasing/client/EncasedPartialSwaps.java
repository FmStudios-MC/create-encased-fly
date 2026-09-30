package fr.iglee42.createcasing.client;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createcasing.blocks.customs.EncasedCustomCogwheelBlock;
import fr.iglee42.createcasing.client.render.TransmissionModels;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Swaps Create's partial models for a block's own set: mixer heads, drill heads, roller frames,
 * chain conveyor guards and shafts, boiler gauges, valve handles, hose pulley magnets, fluid
 * interface tops, spout bottoms and the cogwheel inside a custom encased cogwheel.
 * <p>
 * Upstream patched each renderer and visual with its own redirect; the client mixins here all
 * call {@link #swap}, which leaves every partial it does not know, and every block without a set
 * of its own (Create's), unchanged.
 */
public final class EncasedPartialSwaps {
    private EncasedPartialSwaps() {
    }

    public static PartialModel swap(PartialModel partial, BlockState state) {
        if (state == null)
            return partial;
        if (state.getBlock() instanceof EncasedCustomCogwheelBlock) {
            if (partial == AllPartialModels.COGWHEEL)
                return TransmissionModels.cog(state.getBlock());
            if (partial == AllPartialModels.SHAFTLESS_COGWHEEL)
                return TransmissionModels.shaftlessCog(state.getBlock());
            if (partial == AllPartialModels.SHAFTLESS_LARGE_COGWHEEL)
                return TransmissionModels.shaftlessLargeCog(state.getBlock());
            return partial;
        }
        CasingSetVisuals casing = CasingSetVisuals.of(state);
        if (casing != null) {
            PartialModel swapped = null;
            if (partial == AllPartialModels.MECHANICAL_MIXER_HEAD)
                swapped = casing.mixerHead();
            else if (partial == AllPartialModels.DRILL_HEAD)
                swapped = casing.drillHead();
            else if (partial == AllPartialModels.ROLLER_FRAME)
                swapped = casing.rollerFrame();
            else if (partial == AllPartialModels.CHAIN_CONVEYOR_GUARD)
                swapped = casing.conveyorGuard();
            else if (partial == AllPartialModels.CHAIN_CONVEYOR_SHAFT)
                swapped = casing.conveyorShaft();
            if (swapped != null)
                return swapped;
        }
        FluidSetVisuals fluid = FluidSetVisuals.of(state);
        if (fluid != null) {
            if (partial == AllPartialModels.BOILER_GAUGE)
                return fluid.gauge();
            if (partial == AllPartialModels.BOILER_GAUGE_DIAL)
                return fluid.gaugeDial();
            if (partial == AllPartialModels.VALVE_HANDLE)
                return fluid.valveHandle();
            if (partial == AllPartialModels.HOSE_MAGNET)
                return fluid.hosePulleyMagnet();
            if (partial == AllPartialModels.HOSE_HALF_MAGNET)
                return fluid.hosePulleyHalfMagnet();
            if (partial == AllPartialModels.PORTABLE_FLUID_INTERFACE_TOP)
                return fluid.fluidInterfaceTop();
            if (partial == AllPartialModels.SPOUT_BOTTOM)
                return fluid.spoutBottom();
        }
        return partial;
    }
}
