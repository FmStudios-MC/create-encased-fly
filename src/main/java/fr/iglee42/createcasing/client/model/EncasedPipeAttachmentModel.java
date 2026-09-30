package fr.iglee42.createcasing.client.model;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import com.zurrtum.create.client.infrastructure.model.CTModel;
import com.zurrtum.create.client.infrastructure.model.WrapperBlockStateModel;
import com.zurrtum.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour.AttachmentTypes.ComponentPartials;
import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import fr.iglee42.createcasing.client.EncasedPartialModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Create Fly's {@code PipeAttachmentModel} with the rims, connectors, drains and pipe casing of one
 * fluid set instead of Create's copper ones (upstream's {@code EncasedPipeAttachmentModel}).
 */
public class EncasedPipeAttachmentModel extends WrapperBlockStateModel {
    private final String setName;

    public EncasedPipeAttachmentModel(BlockState state, UnbakedRoot unbaked, String setName) {
        super(state, unbaked);
        this.setName = setName;
    }

    public static BiFunction<BlockState, UnbakedRoot, UnbakedRoot> of(String setName) {
        return (state, unbaked) -> new EncasedPipeAttachmentModel(state, unbaked, setName);
    }

    /** An encased pipe: the casing's connected textures, plus this set's attachments. */
    public static BiFunction<BlockState, UnbakedRoot, UnbakedRoot> encased(String setName, ConnectedTextureBehaviour casing) {
        return (state, unbaked) -> new Encased(state, new CTModel(state, unbaked, casing), setName);
    }

    @Override
    public void addPartsWithInfo(BlockAndTintGetter world, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        collectModelParts(world, pos, state, random, parts);
        Optional.ofNullable(BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE))
            .map(BracketedBlockEntityBehaviour::getBracket)
            .map(bracket -> Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(bracket))
            .ifPresent(model -> model.collectParts(random, parts));
        Map<ComponentPartials, Map<Direction, PartialModel>> attachments = EncasedPartialModels.PIPE_ATTACHMENTS.get(setName);
        FluidTransportBehaviour transport = BlockEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);
        if (transport != null && attachments != null) {
            for (Direction direction : Iterate.directions) {
                AttachmentTypes type = transport.getRenderedRimAttachment(world, pos, state, direction);
                for (ComponentPartials partial : type.partials)
                    attachments.get(partial).get(direction).get().collectParts(random, parts);
            }
        }
        PartialModel casing = EncasedPartialModels.PIPE_CASINGS.get(setName);
        if (casing != null && FluidPipeBlock.shouldDrawCasing(world, pos, state))
            casing.get().collectParts(random, parts);
    }

    protected void collectModelParts(BlockAndTintGetter world, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        model.collectParts(random, parts);
    }

    public static class Encased extends EncasedPipeAttachmentModel {
        public Encased(BlockState state, UnbakedRoot unbaked, String setName) {
            super(state, unbaked, setName);
        }

        @Override
        protected void collectModelParts(BlockAndTintGetter world, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
            ((WrapperBlockStateModel) model).addPartsWithInfo(world, pos, state, random, parts);
        }
    }
}
