package fr.iglee42.createcasing.gametest;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.api.behaviour.display.DisplaySource;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.api.stress.BlockStressValues;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import com.zurrtum.create.content.kinetics.belt.item.BeltConnectorItem;
import com.zurrtum.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.zurrtum.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.zurrtum.create.content.kinetics.motor.CreativeMotorBlockEntity;
import com.zurrtum.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
import fr.iglee42.createcasing.blockEntities.AutoClutchBlockEntity;
import fr.iglee42.createcasing.casings.CasingSets;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.world.TestWorldSave;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Builds small setups and checks their state: rotation through every kind of the mod's shaft and
 * transmission, chain drives joining only their own casing, the configurable gearbox and the
 * automatic clutch, tanks and steam engines, the transfer API, casing swaps and encasing by hand,
 * belt casings, stress values, behaviours inherited from Create, recipes, and state across a save
 * and reload. Every check logs one "ENCASED-TEST PASS/FAIL" line; the test fails if any check did.
 */
public class GameplayCheck implements FabricClientGameTest {
    static final int Y = 150;
    static final BlockPos LINE_END = new BlockPos(16, Y, 0);
    static final BlockPos COG_END = new BlockPos(1, Y + 3, 3);
    static final BlockPos GEARBOX = new BlockPos(1, Y, 6);
    static final BlockPos CLUTCH = new BlockPos(1, Y, 9);
    static final BlockPos BRASS_DRIVE_A = new BlockPos(20, Y, 0), BRASS_DRIVE_B = new BlockPos(20, Y, 1), COPPER_DRIVE = new BlockPos(20, Y, 2);
    static final BlockPos TANK = new BlockPos(24, Y, 0), CREATE_TANK = new BlockPos(25, Y, 0), ENGINE = new BlockPos(24, Y, -1);
    static final BlockPos SWAP = new BlockPos(28, Y, 0), ENCASE = new BlockPos(30, Y, 0), BELT = new BlockPos(32, Y, 0);

    private final List<String> failures = new ArrayList<>();

    @Override
    public void runTest(ClientGameTestContext context) {
        TestWorldSave save;
        try (TestSingleplayerContext singleplayer = context.worldBuilder().setUseConsistentSettings(true).create()) {
            singleplayer.getConnection().waitForChunksRender();
            TestServerContext server = singleplayer.getServer();
            save = singleplayer.getWorldSave();
            server.runCommand("gamemode creative @p");
            server.runCommand("tp @p 12 " + (Y + 4) + " -6");
            server.runCommand("fill -4 " + (Y - 1) + " -4 40 " + (Y - 1) + " 12 minecraft:smooth_stone");
            server.runCommand("fill -4 " + Y + " -4 40 " + (Y + 6) + " 12 minecraft:air");

            // a line of every shaft kind: creative motor -> shafts, encased shafts, gearbox, clutch, gearshift, automatic clutch
            String[] line = {"createcasing:oak_shaft[axis=x]", "createcasing:brass_shaft[axis=x]", "createcasing:railway_encased_shaft[axis=x]",
                "createcasing:zinc_encased_glass_shaft[axis=x]", "createcasing:railway_gearbox[axis=y]", "createcasing:copper_shaft[axis=x]",
                "createcasing:railway_clutch[axis=x]", "createcasing:railway_gearshift[axis=x]", "createcasing:andesite_automatic_clutch[axis=x]",
                "createcasing:mldeg_shaft[axis=x]", "createcasing:creative_encased_oak_shaft[axis=x]", "createcasing:spruce_shaft[axis=x]",
                "createcasing:zinc_encased_shaft[axis=x]", "createcasing:bamboo_shaft[axis=x]", "createcasing:glass_shaft[axis=x]",
                "createcasing:cherry_shaft[axis=x]"};
            set(server, new BlockPos(0, Y, 0), "create:creative_motor[facing=east]");
            for (int i = 0; i < line.length; i++)
                set(server, new BlockPos(i + 1, Y, 0), line[i]);

            // cogwheels meshing upwards
            set(server, new BlockPos(0, Y, 3), "create:creative_motor[facing=east]");
            set(server, new BlockPos(1, Y, 3), "createcasing:oak_cogwheel[axis=x]");
            set(server, new BlockPos(1, Y + 1, 3), "createcasing:brass_cogwheel[axis=x]");
            set(server, new BlockPos(1, Y + 2, 3), "createcasing:zinc_cogwheel[axis=x]");
            set(server, COG_END, "createcasing:andesite_cogwheel[axis=x]");

            // configurable gearbox: open west and north, closed south
            set(server, GEARBOX.west(), "create:creative_motor[facing=east]");
            set(server, GEARBOX, "createcasing:railway_configurable_gearbox[west=true,north=true]");
            set(server, GEARBOX.north(), "createcasing:brass_shaft[axis=z]");
            set(server, GEARBOX.south(), "createcasing:brass_shaft[axis=z]");

            // automatic clutch set to cut above 100 rpm, driven at 256
            set(server, CLUTCH.west(), "create:creative_motor[facing=east]");
            set(server, CLUTCH, "createcasing:brass_automatic_clutch[axis=x]");
            set(server, CLUTCH.east(), "createcasing:zinc_shaft[axis=x]");

            // chain drives: two brass ones join, the copper one next to them stays alone
            set(server, BRASS_DRIVE_A, "createcasing:brass_encased_chain_drive[axis=x]");
            set(server, BRASS_DRIVE_B, "createcasing:brass_encased_chain_drive[axis=x]");
            set(server, COPPER_DRIVE, "createcasing:copper_encased_chain_drive[axis=x]");

            // a brass tank two high with a brass steam engine, next to Create's tank
            set(server, TANK, "createcasing:brass_fluid_tank");
            set(server, TANK.above(), "createcasing:brass_fluid_tank");
            set(server, CREATE_TANK, "create:fluid_tank");
            set(server, ENGINE, "createcasing:brass_steam_engine[face=wall,facing=north]");

            set(server, SWAP, "createcasing:railway_press[facing=north]");
            set(server, ENCASE, "createcasing:oak_shaft[axis=x]");
            set(server, BELT, "create:shaft[axis=z]");
            set(server, BELT.east(2), "create:shaft[axis=z]");
            context.waitTicks(5);

            server.runOnServer(mc -> {
                ServerLevel level = mc.overworld();
                for (BlockPos motor : new BlockPos[]{new BlockPos(0, Y, 0), CLUTCH.west()})
                    if (level.getBlockEntity(motor) instanceof CreativeMotorBlockEntity m)
                    {
                        m.generatedSpeed.setValue(motor.equals(CLUTCH.west()) ? 256 : 32);
                        m.updateGeneratedRotation();
                    }
                if (level.getBlockEntity(CLUTCH) instanceof AutoClutchBlockEntity clutch) {
                    clutch.setMode(AutoClutchBlockEntity.Mode.SPEED);
                    clutch.setOperation(AutoClutchBlockEntity.Operation.GREATER);
                    clutch.setConfiguredValue(100);
                }
                BeltConnectorItem.createBelts(level, BELT, BELT.east(2));
                ServerPlayer player = level.players().getFirst();
                use(level, player, SWAP, new ItemStack(CasingSets.COPPER.getCasing()));
                use(level, player, ENCASE, new ItemStack(AllBlocks.BRASS_CASING));
                use(level, player, BELT, new ItemStack(CasingSets.RAILWAY.getCasing()));
            });
            context.waitTicks(60);

            check(server, "rotation through every shaft kind", level -> {
                StringBuilder speeds = new StringBuilder();
                for (int x = 1; x <= LINE_END.getX(); x++)
                    speeds.append(level.getBlockEntity(new BlockPos(x, Y, 0)) instanceof KineticBlockEntity k ? (int) k.getSpeed() : "-").append(' ');
                return speeds + speed(level, LINE_END);
            });
            check(server, "rotation through the mod's cogwheels", level -> speed(level, COG_END));
            check(server, "configurable gearbox drives its open face", level -> speed(level, GEARBOX.north()));
            check(server, "configurable gearbox leaves its closed face still", level -> {
                float s = level.getBlockEntity(GEARBOX.south()) instanceof KineticBlockEntity k ? k.getSpeed() : -1;
                return "speed " + s + (s == 0 ? "" : " FAIL");
            });
            check(server, "automatic clutch cuts above its speed", level -> {
                float in = level.getBlockEntity(CLUTCH) instanceof KineticBlockEntity k ? k.getSpeed() : 0;
                float s = level.getBlockEntity(CLUTCH.east()) instanceof KineticBlockEntity k ? k.getSpeed() : -1;
                return "input " + in + ", output " + s + (s == 0 && in >= 256 ? "" : " FAIL");
            });
            check(server, "brass chain drives join", level -> {
                var part = level.getBlockState(BRASS_DRIVE_A).getValue(ChainDriveBlock.PART);
                return part + (part != ChainDriveBlock.Part.NONE ? "" : " FAIL");
            });
            check(server, "copper chain drive does not join brass", level -> {
                var part = level.getBlockState(COPPER_DRIVE).getValue(ChainDriveBlock.PART);
                var brass = level.getBlockState(BRASS_DRIVE_B).getValue(ChainDriveBlock.PART);
                return "copper " + part + ", brass " + brass + (part == ChainDriveBlock.Part.NONE && brass == ChainDriveBlock.Part.END ? "" : " FAIL");
            });
            check(server, "brass tanks form one tank", level -> {
                if (!(level.getBlockEntity(TANK.above()) instanceof FluidTankBlockEntity top))
                    return "missing FAIL";
                return "controller " + top.getController() + (TANK.equals(top.getController()) ? "" : " FAIL");
            });
            check(server, "brass tank stays apart from Create's tank", level -> {
                if (!(level.getBlockEntity(CREATE_TANK) instanceof FluidTankBlockEntity create))
                    return "missing FAIL";
                return "controller " + create.getController() + (CREATE_TANK.equals(create.getController()) ? "" : " FAIL");
            });
            check(server, "brass steam engine sits on a valid tank", level ->
                level.getBlockEntity(ENGINE) instanceof SteamEngineBlockEntity e ? (e.isValid() ? "valid" : "invalid FAIL") : "missing FAIL");
            check(server, "transfer API finds the brass tank", level -> {
                boolean tank = FluidStorage.SIDED.find(level, TANK, Direction.UP) != null;
                return "tank " + tank + (tank ? "" : " FAIL");
            });
            check(server, "copper casing turns a railway press into a copper press", level -> id(level, SWAP).equals("createcasing:copper_press") ? id(level, SWAP) : id(level, SWAP) + " FAIL");
            check(server, "brass casing encases an oak shaft", level -> id(level, ENCASE).equals("createcasing:brass_encased_oak_shaft") ? id(level, ENCASE) : id(level, ENCASE) + " FAIL");
            check(server, "railway casing on a belt", level -> beltCasing(level, "railway"));
            check(server, "railway press has Create's press stress", level -> {
                double own = BlockStressValues.getImpact(block("railway_press")), create = BlockStressValues.getImpact(AllBlocks.MECHANICAL_PRESS);
                return own + " vs " + create + (own == create && own > 0 ? "" : " FAIL");
            });
            check(server, "brass steam engine has Create's capacity", level -> {
                double own = BlockStressValues.getCapacity(block("brass_steam_engine")), create = BlockStressValues.getCapacity(AllBlocks.STEAM_ENGINE);
                return own + " vs " + create + (own == create && own > 0 ? "" : " FAIL");
            });
            check(server, "creative cogwheel capacity", level -> {
                double c = BlockStressValues.getCapacity(block("creative_cogwheel"));
                return c + (c == 16384 ? "" : " FAIL");
            });
            check(server, "contraption behaviours of drills, saws, deployers", level -> {
                boolean all = MovementBehaviour.REGISTRY.get(block("railway_mechanical_drill")) != null
                    && MovementBehaviour.REGISTRY.get(block("copper_mechanical_saw")) != null
                    && MovementBehaviour.REGISTRY.get(block("creative_deployer")) != null
                    && MovementBehaviour.REGISTRY.get(block("brass_mechanical_harvester")) != null;
                return all ? "registered" : "missing FAIL";
            });
            check(server, "depot display source", level -> DisplaySource.BY_BLOCK.get(block("brass_depot")).isEmpty() ? "none FAIL" : "present");
            check(server, "mechanical arm uses a brass depot", level -> {
                BlockPos pos = new BlockPos(34, Y, 4);
                level.setBlockAndUpdate(pos, block("brass_depot").defaultBlockState());
                return ArmInteractionPoint.create(level, pos, level.getBlockState(pos)) != null ? "point" : "no point FAIL";
            });
            check(server, "recipes loaded", level -> {
                long count = level.getServer().getRecipeManager().getRecipes().stream()
                    .filter(r -> r.id().identifier().getNamespace().equals("createcasing")).count();
                return count + " recipes" + (count >= 258 ? "" : " FAIL");
            });
        }

        try (TestSingleplayerContext singleplayer = save.open()) {
            singleplayer.getConnection().waitForChunksRender();
            TestServerContext server = singleplayer.getServer();
            context.waitTicks(20);
            check(server, "belt casing survives save and reload", level -> beltCasing(level, "railway"));
            check(server, "automatic clutch settings survive save and reload", level -> {
                if (!(level.getBlockEntity(CLUTCH) instanceof AutoClutchBlockEntity clutch))
                    return "missing FAIL";
                String s = clutch.getMode() + " " + clutch.getOperation() + " " + clutch.getConfiguredValue();
                return s + (clutch.getMode() == AutoClutchBlockEntity.Mode.SPEED && clutch.getConfiguredValue() == 100 ? "" : " FAIL");
            });
            check(server, "rotation after reload", level -> speed(level, LINE_END));
        }

        if (!failures.isEmpty())
            throw new AssertionError("ENCASED-TEST failures: " + failures);
    }

    private static void use(ServerLevel level, ServerPlayer player, BlockPos pos, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        player.gameMode.useItemOn(player, level, stack, InteractionHand.MAIN_HAND,
            new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false));
    }

    private static Block block(String path) {
        return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("createcasing", path));
    }

    private static String id(ServerLevel level, BlockPos pos) {
        return BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();
    }

    private static String speed(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof KineticBlockEntity k))
            return id(level, pos) + " has no kinetic block entity FAIL";
        return "speed " + k.getSpeed() + (k.getSpeed() != 0 ? "" : " FAIL");
    }

    private static String beltCasing(ServerLevel level, String expected) {
        if (!(level.getBlockEntity(BELT) instanceof BeltBlockEntity belt))
            return "no belt FAIL";
        String casing = belt.casing.getSerializedName();
        return casing + (casing.equals(expected) ? "" : " FAIL");
    }

    private static void set(TestServerContext server, BlockPos pos, String block) {
        server.runCommand("setblock " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + block);
    }

    private void check(TestServerContext server, String name, Function<ServerLevel, String> probe) {
        String result = server.computeOnServer(mc -> probe.apply(mc.overworld()));
        boolean failed = result.endsWith("FAIL");
        if (failed)
            failures.add(name);
        System.out.println("ENCASED-TEST " + (failed ? "FAIL" : "PASS") + " | " + name + " | " + result);
    }
}
