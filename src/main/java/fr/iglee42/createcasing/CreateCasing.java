package fr.iglee42.createcasing;

import com.zurrtum.create.api.stress.BlockStressValues;
import fr.iglee42.createcasing.commands.CreateCasingCommand;
import fr.iglee42.createcasing.config.EncasedConfigs;
import fr.iglee42.createcasing.packets.ConfigureAutoClutchPacket;
import fr.iglee42.createcasing.registries.EncasedBlockEntities;
import fr.iglee42.createcasing.registries.EncasedBlocks;
import fr.iglee42.createcasing.registries.EncasedItems;
import fr.iglee42.createcasing.registries.EncasedSounds;
import fr.iglee42.createcasing.utils.BaseBlockProviders;
import fr.iglee42.createcasing.utils.ItemChangeBlockManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint. Upstream registered through Registrate on NeoForge's event bus; here
 * everything is registered in order from {@link #onInitialize()}.
 */
public class CreateCasing implements ModInitializer {

    public static final String MODID = "createcasing";

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Override
    public void onInitialize() {
        EncasedSounds.register();
        EncasedItems.register();
        EncasedBlocks.register();
        EncasedItems.registerVerticalGearboxes();
        EncasedBlocks.registerEncasedVariants();
        EncasedBlockEntities.register();
        EncasedItems.registerTab();
        EncasedConfigs.register();

        BaseBlockProviders.register();
        BlockStressValues.setGeneratorSpeed(EncasedBlocks.CREATIVE_COGWHEEL, 256, true);

        ConfigureAutoClutchPacket.register();
        ItemChangeBlockManager.register();

        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> new CreateCasingCommand(dispatcher));

        LOGGER.info("Registered {} blocks", EncasedBlocks.ALL.size());
    }
}
