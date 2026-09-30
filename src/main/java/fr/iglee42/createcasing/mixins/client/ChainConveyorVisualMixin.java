package fr.iglee42.createcasing.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.client.content.kinetics.chainConveyor.ChainConveyorVisual;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import fr.iglee42.createcasing.client.EncasedPartialSwaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Chain conveyors of every casing draw their own shaft and guards. See {@code RendererPartialsMixin}. */
@Mixin(ChainConveyorVisual.class)
public class ChainConveyorVisualMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/flywheel/lib/model/Models;chunkPartial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;)Lcom/zurrtum/create/client/flywheel/api/model/Model;"))
    private static Model createcasing$shaft(PartialModel partial, Operation<Model> original, @Local(argsOnly = true) ChainConveyorBlockEntity be) {
        return original.call(EncasedPartialSwaps.swap(partial, be.getBlockState()));
    }

    @WrapOperation(method = "setupGuards", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/flywheel/lib/model/Models;chunkPartial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;)Lcom/zurrtum/create/client/flywheel/api/model/Model;"))
    private Model createcasing$guard(PartialModel partial, Operation<Model> original) {
        return original.call(EncasedPartialSwaps.swap(partial, ((BlockEntityVisualAccessor) this).createcasing$blockState()));
    }
}
