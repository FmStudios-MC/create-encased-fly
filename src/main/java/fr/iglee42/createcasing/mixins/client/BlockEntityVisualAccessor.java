package fr.iglee42.createcasing.mixins.client;

import com.zurrtum.create.client.flywheel.lib.visual.AbstractBlockEntityVisual;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlockEntityVisual.class)
public interface BlockEntityVisualAccessor {
    @Accessor("blockState")
    BlockState createcasing$blockState();
}
