package fr.iglee42.createcasing;

import fr.iglee42.createcasing.blockEntities.AutoClutchBlockEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

/**
 * Calls from common code into the client, filled in by {@code CreateCasingClient}. Upstream used
 * Catnip's {@code executeOnClientOnly}; Create Fly does the same with {@code AllClientHandle}.
 */
public final class EncasedClientHooks {
    public static BiConsumer<AutoClutchBlockEntity, Player> autoClutchScreen = (be, player) -> {
    };

    private EncasedClientHooks() {
    }

    public static void openAutoClutchScreen(AutoClutchBlockEntity be, Player player) {
        autoClutchScreen.accept(be, player);
    }
}
