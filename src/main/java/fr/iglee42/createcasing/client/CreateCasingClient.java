package fr.iglee42.createcasing.client;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.AllExtensions;
import com.zurrtum.create.client.AllItemTooltips;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import fr.iglee42.createcasing.client.ponder.EncasedPonders;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.EncasedClientHooks;
import fr.iglee42.createcasing.registries.EncasedBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Client entrypoint: partial models, sprite shifts, model wrappers and casing connectivity,
 * renderers and visuals, client behaviours, item tooltips and the automatic clutch screen.
 */
public class CreateCasingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EncasedPartialModels.init();
        EncasedSprites.init();
        CasingSetVisuals.init();
        FluidSetVisuals.init();
        CasingSetVisuals.indexBlocks();
        FluidSetVisuals.indexBlocks();

        EncasedModels.register();
        EncasedBlockEntityRenders.register();
        EncasedBlockEntityRenders.registerBehaviours();

        // Upstream gave every item Create's tooltip: its description, if the lang file has one, and kinetic stats.
        for (Item item : BuiltInRegistries.ITEM) {
            if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(CreateCasing.MODID))
                AllItemTooltips.register(item);
        }

        // chain conveyors outline their whole shape, like Create's
        for (Block block : EncasedBlocks.ALL)
            if (EncasedBlocks.getBase(block) == AllBlocks.CHAIN_CONVEYOR)
                AllExtensions.BIG_OUTLINE.add(block);

        PonderIndex.addPlugin(new EncasedPonders.CreateScenes());
        PonderIndex.addPlugin(new EncasedPonders.OwnScenes());

        EncasedClientHooks.autoClutchScreen = AutoClutchClient::openScreen;
    }
}
