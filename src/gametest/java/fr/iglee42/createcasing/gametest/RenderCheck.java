package fr.iglee42.createcasing.gametest;

import fr.iglee42.createcasing.registries.EncasedBlocks;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Places every block of the mod in a grid in the sky and takes screenshots, plus a row of
 * shafts and cogwheels driven by a creative motor, and the creative tab. A visual check: it
 * asserts nothing, but a crash while rendering fails the run, and missing models show up as the
 * purple-black texture.
 */
public class RenderCheck implements FabricClientGameTest {
    static final int Y = 150;
    static final int PER_ROW = 24;

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().setUseConsistentSettings(true).create()) {
            singleplayer.getConnection().waitForChunksRender();
            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamemode creative @p");
            context.runOnClient(mc -> mc.player.getAbilities().flying = true);
            server.runCommand("time set noon");
            server.runCommand("weather clear");

            List<Block> blocks = EncasedBlocks.ALL;
            int rows = (blocks.size() + PER_ROW - 1) / PER_ROW;
            server.runCommand("fill -2 " + (Y - 1) + " -2 " + (PER_ROW * 2 + 1) + " " + (Y - 1) + " " + (rows * 2 + 1) + " minecraft:smooth_stone");
            server.runOnServer(minecraftServer -> {
                var level = minecraftServer.overworld();
                for (int i = 0; i < blocks.size(); i++) {
                    BlockState state = blocks.get(i).defaultBlockState();
                    level.setBlock(new BlockPos((i % PER_ROW) * 2, Y, (i / PER_ROW) * 2), state, Block.UPDATE_CLIENTS);
                }
            });
            context.waitTicks(20);

            for (int band = 0; band < rows; band += 5) {
                for (int half = 0; half < 2; half++) {
                    int cx = half * PER_ROW + PER_ROW / 2 - 1;
                    int cz = band * 2 + 4;
                    look(context, server, cx, Y + 6, cz - 9, cx, Y, cz);
                    context.waitTicks(30);
                    singleplayer.getConnection().waitForChunksRender();
                    context.takeScreenshot("grid_rows_" + band + "_" + half);
                }
            }

            driveRow(context, singleplayer, server, rows * 2 + 6);
            closeUps(context, singleplayer, server, rows * 2 + 14);

            server.runOnServer(minecraftServer -> {
                for (Block block : blocks)
                    if (block.asItem() == net.minecraft.world.item.Items.AIR)
                        System.out.println("ENCASED-CHECK no item: " + BuiltInRegistries.BLOCK.getKey(block));
            });
        }
    }

    /** A creative motor drives one line of every shaft, then one line of cogwheels. */
    private static void driveRow(ClientGameTestContext context, TestSingleplayerContext singleplayer, TestServerContext server, int z) {
        server.runCommand("fill -2 " + (Y - 1) + " " + (z - 2) + " 40 " + (Y - 1) + " " + (z + 4) + " minecraft:smooth_stone");
        String[] shafts = {"oak", "birch", "acacia", "jungle", "warped", "dark_oak", "crimson", "mangrove", "cherry", "bamboo", "spruce", "brass", "copper", "zinc", "mldeg", "glass"};
        set(server, -1, z, "create:creative_motor[facing=east]");
        for (int i = 0; i < shafts.length; i++)
            set(server, i, z, "createcasing:" + shafts[i] + "_shaft[axis=x]");
        set(server, shafts.length, z, "createcasing:brass_encased_oak_shaft[axis=x]");
        set(server, shafts.length + 1, z, "createcasing:zinc_encased_glass_shaft[axis=x]");
        set(server, shafts.length + 2, z, "createcasing:creative_encased_shaft[axis=x]");

        String[] cogs = {"oak", "birch", "acacia", "jungle", "warped", "dark_oak", "crimson", "mangrove", "cherry", "bamboo", "brass", "copper", "zinc", "andesite"};
        set(server, -1, z + 2, "create:creative_motor[facing=east]");
        for (int i = 0; i < cogs.length; i++)
            set(server, i, z + 2, "createcasing:" + cogs[i] + "_cogwheel[axis=x]");
        set(server, cogs.length, z + 2, "createcasing:railway_encased_oak_cogwheel[axis=x,top_shaft=true,bottom_shaft=true]");
        set(server, cogs.length + 1, z + 2, "createcasing:copper_encased_cogwheel[axis=x,top_shaft=true,bottom_shaft=true]");
        set(server, cogs.length + 2, z + 2, "createcasing:creative_cogwheel[axis=y]");

        context.waitTicks(40);
        look(context, server, 9, Y + 4, z + 8, 9, Y, z + 1);
        context.waitTicks(20);
        singleplayer.getConnection().waitForChunksRender();
        context.takeScreenshot("driven_a");
        context.waitTicks(5);
        context.takeScreenshot("driven_b");
    }

    /** One casing set and one fluid set side by side, close enough to see their heads, frames and gauges. */
    private static void closeUps(ClientGameTestContext context, TestSingleplayerContext singleplayer, TestServerContext server, int z) {
        server.runCommand("fill -2 " + (Y - 1) + " " + (z - 2) + " 40 " + (Y - 1) + " " + (z + 8) + " minecraft:smooth_stone");
        String[] railway = {"mixer", "press", "mechanical_drill[facing=north]", "mechanical_saw[facing=up]", "mechanical_roller[facing=north]",
            "mechanical_harvester[facing=north]", "deployer[facing=north]", "encased_fan[facing=north]", "chain_conveyor", "depot",
            "gearbox", "configurable_gearbox[north=true,south=true,up=true]", "automatic_clutch", "encased_chain_drive", "portable_storage_interface[facing=up]"};
        for (int i = 0; i < railway.length; i++)
            set(server, i * 2, z, "createcasing:railway_" + railway[i]);
        String[] brass = {"fluid_tank", "steam_engine[face=wall,facing=north]", "spout", "hose_pulley", "portable_fluid_interface[facing=up]",
            "valve_handle[facing=up]", "mechanical_pump[facing=east]", "smart_fluid_pipe", "fluid_valve", "item_drain", "steam_whistle"};
        for (int i = 0; i < brass.length; i++)
            set(server, i * 2, z + 4, "createcasing:brass_" + brass[i]);
        set(server, 0, z + 5, "createcasing:brass_fluid_tank");
        set(server, 0, z + 6, "createcasing:brass_steam_engine[face=wall,facing=south]");

        for (int part = 0; part < 3; part++) {
            int cx = part * 10 + 4;
            look(context, server, cx, Y + 3, z - 5, cx, Y, z + 2);
            context.waitTicks(20);
            singleplayer.getConnection().waitForChunksRender();
            context.takeScreenshot("close_" + part);
        }
    }

    static void set(TestServerContext server, int x, int z, String block) {
        server.runCommand("setblock " + x + " " + Y + " " + z + " " + block);
    }

    /** Teleports the player to (px, py, pz) looking at the centre of block (tx, ty, tz). */
    static void look(ClientGameTestContext context, TestServerContext server, double px, double py, double pz, int tx, int ty, int tz) {
        double dx = tx + 0.5 - px, dy = ty + 0.5 - (py + 1.62), dz = tz + 0.5 - pz;
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        server.runCommand("tp @p " + px + " " + py + " " + pz + " " + yaw + " " + pitch);
        context.waitTicks(10);
        context.runOnClient(mc -> {
            mc.player.setYRot(yaw);
            mc.player.setXRot(pitch);
        });
    }
}
