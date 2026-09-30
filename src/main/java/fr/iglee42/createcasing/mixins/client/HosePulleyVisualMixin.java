package fr.iglee42.createcasing.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.content.fluids.hosePulley.HosePulleyVisual;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createcasing.client.EncasedPartialSwaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Hose pulleys of every material lower their own magnet. See {@code RendererPartialsMixin}. */
@Mixin(HosePulleyVisual.class)
public class HosePulleyVisualMixin {
    @WrapOperation(method = {"getMagnetModel", "getHalfMagnetModel"}, at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/flywheel/lib/model/Models;chunkPartial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;)Lcom/zurrtum/create/client/flywheel/api/model/Model;"))
    private Model createcasing$magnet(PartialModel partial, Operation<Model> original) {
        return original.call(EncasedPartialSwaps.swap(partial, ((BlockEntityVisualAccessor) this).createcasing$blockState()));
    }
}
