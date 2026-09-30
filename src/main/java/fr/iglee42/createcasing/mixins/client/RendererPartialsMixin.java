package fr.iglee42.createcasing.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createcasing.client.EncasedPartialSwaps;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Create's renderers (and the contraption renderers of drills and rollers) ask
 * {@code CachedBuffers} for a partial model together with the block state; each such request is
 * passed through {@link EncasedPartialSwaps}, which gives the mod's blocks their own set's head,
 * frame, gauge or cogwheel. Upstream redirected each call separately.
 */
@Mixin(targets = {
    "com.zurrtum.create.client.content.kinetics.mixer.MechanicalMixerRenderer",
    "com.zurrtum.create.client.content.kinetics.drill.DrillRenderer",
    "com.zurrtum.create.client.content.kinetics.drill.DrillMovementRenderBehaviour",
    "com.zurrtum.create.client.content.contraptions.actors.roller.RollerRenderer",
    "com.zurrtum.create.client.content.contraptions.actors.roller.RollerMovementRenderBehaviour",
    "com.zurrtum.create.client.content.kinetics.chainConveyor.ChainConveyorRenderer",
    "com.zurrtum.create.client.content.fluids.tank.FluidTankRenderer",
    "com.zurrtum.create.client.content.kinetics.crank.ValveHandleRenderer",
    "com.zurrtum.create.client.content.fluids.hosePulley.HosePulleyRenderer",
    "com.zurrtum.create.client.content.contraptions.pulley.AbstractPulleyRenderer",
    "com.zurrtum.create.client.content.fluids.spout.SpoutRenderer",
    "com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedSmallCogRenderer",
    "com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedLargeCogRenderer"
})
public class RendererPartialsMixin {
    @WrapOperation(method = "*", require = 0, at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/catnip/render/CachedBuffers;partial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;"))
    private static SuperByteBuffer createcasing$partial(PartialModel partial, BlockState state, Operation<SuperByteBuffer> original) {
        return original.call(EncasedPartialSwaps.swap(partial, state), state);
    }

    @WrapOperation(method = "*", require = 0, at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/catnip/render/CachedBuffers;partialFacing(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;"))
    private static SuperByteBuffer createcasing$partialFacing(PartialModel partial, BlockState state, Operation<SuperByteBuffer> original) {
        return original.call(EncasedPartialSwaps.swap(partial, state), state);
    }

    @WrapOperation(method = "*", require = 0, at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/catnip/render/CachedBuffers;partialFacing(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;"))
    private static SuperByteBuffer createcasing$partialFacingTo(PartialModel partial, BlockState state, Direction direction, Operation<SuperByteBuffer> original) {
        return original.call(EncasedPartialSwaps.swap(partial, state), state, direction);
    }

    @WrapOperation(method = "*", require = 0, at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/catnip/render/CachedBuffers;partialFacingVertical(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;"))
    private static SuperByteBuffer createcasing$partialFacingVertical(PartialModel partial, BlockState state, Direction direction, Operation<SuperByteBuffer> original) {
        return original.call(EncasedPartialSwaps.swap(partial, state), state, direction);
    }
}
