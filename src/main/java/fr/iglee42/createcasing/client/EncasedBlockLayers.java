package fr.iglee42.createcasing.client;

import fr.iglee42.createcasing.registries.EncasedBlocks;
import fr.iglee42.createcasing.transmissions.TransmissionSets;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;

/**
 * Chunk layers. 1.21.11 does not derive them from the textures (26.2 does), and Create Fly sets
 * them per block in {@code AllBlockLayers}: every block takes its Create counterpart's layer, the
 * glass shaft cutout (upstream's model asked for {@code cutout_mipped}, which 1.21.11 no longer
 * has). Done once the client has started, so Create Fly's own layers are in place whatever order
 * the entrypoints ran in.
 */
public class EncasedBlockLayers {
    public static void register() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            for (Block block : EncasedBlocks.ALL) {
                Block base = EncasedBlocks.getBase(block);
                if (base == null)
                    continue;
                ChunkSectionLayer layer = ItemBlockRenderTypes.getChunkRenderType(base.defaultBlockState());
                if (layer != ChunkSectionLayer.SOLID)
                    BlockRenderLayerMap.putBlock(block, layer);
            }
            BlockRenderLayerMap.putBlock(TransmissionSets.GLASS.getShaft(), ChunkSectionLayer.CUTOUT);
        });
    }
}
