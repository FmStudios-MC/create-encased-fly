package fr.iglee42.createcasing.client;

import com.zurrtum.create.client.catnip.gui.ScreenOpener;
import fr.iglee42.createcasing.blockEntities.AutoClutchBlockEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public final class AutoClutchClient {
    private AutoClutchClient() {
    }

    public static void openScreen(AutoClutchBlockEntity be, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new AutoClutchScreen(be));
    }
}
