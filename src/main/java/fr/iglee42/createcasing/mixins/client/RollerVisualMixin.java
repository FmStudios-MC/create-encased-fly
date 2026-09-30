package fr.iglee42.createcasing.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.client.content.contraptions.actors.roller.RollerVisual;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.contraptions.actors.roller.RollerBlockEntity;
import fr.iglee42.createcasing.client.EncasedPartialSwaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Rollers of every casing draw their own frame. See {@code RendererPartialsMixin}. */
@Mixin(RollerVisual.class)
public class RollerVisualMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/flywheel/lib/model/Models;chunkPartial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;)Lcom/zurrtum/create/client/flywheel/api/model/Model;"))
    private static Model createcasing$swap(PartialModel partial, Operation<Model> original, @Local(argsOnly = true) RollerBlockEntity be) {
        return original.call(EncasedPartialSwaps.swap(partial, be.getBlockState()));
    }
}
