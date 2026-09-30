package fr.iglee42.createcasing.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.client.content.kinetics.crank.ValveHandleVisual;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.kinetics.crank.ValveHandleBlockEntity;
import fr.iglee42.createcasing.client.EncasedPartialSwaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Valve handles of every material draw their own handle. See {@code RendererPartialsMixin}. */
@Mixin(ValveHandleVisual.class)
public class ValveHandleVisualMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/flywheel/lib/model/Models;chunkPartial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;)Lcom/zurrtum/create/client/flywheel/api/model/Model;"))
    private static Model createcasing$swap(PartialModel partial, Operation<Model> original, @Local(argsOnly = true) ValveHandleBlockEntity be) {
        return original.call(EncasedPartialSwaps.swap(partial, be.getBlockState()));
    }
}
