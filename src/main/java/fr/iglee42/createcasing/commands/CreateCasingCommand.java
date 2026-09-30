package fr.iglee42.createcasing.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.iglee42.createcasing.CreateCasing;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Predicate;

/** Development only: places every block of the mod (or those matching a filter) next to the player. */
public class CreateCasingCommand {

    public CreateCasingCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("createcasing")
            .then(Commands.literal("placeAllBlocks").requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                .executes(c -> placeBlocks(c, id -> true))
                .then(Commands.argument("filter", StringArgumentType.string()).executes(this::placeBlocksWithFilter))));
        dispatcher.register(Commands.literal("cc").requires(Commands.hasPermission(Commands.LEVEL_OWNERS)).redirect(command));
    }

    private int placeBlocksWithFilter(CommandContext<CommandSourceStack> source) {
        String filter = StringArgumentType.getString(source, "filter");
        if (filter.startsWith("/") && filter.endsWith("/")) {
            String regex = filter.substring(1, filter.length() - 1);
            return placeBlocks(source, id -> id.getPath().matches(regex));
        }
        return placeBlocks(source, id -> id.getPath().contains(filter));
    }

    private int placeBlocks(CommandContext<CommandSourceStack> source, Predicate<Identifier> filter) {
        ServerLevel level = source.getSource().getLevel();
        ServerPlayer player = source.getSource().getPlayer();
        if (player == null)
            return 0;
        List<Identifier> blocks = BuiltInRegistries.BLOCK.keySet().stream()
            .filter(k -> k.getNamespace().equals(CreateCasing.MODID) && filter.test(k)).toList();
        int x = 0;
        int y = 0;
        for (Identifier id : blocks) {
            BlockPos pos = player.blockPosition().offset(x, y, 0);
            level.setBlockAndUpdate(pos, BuiltInRegistries.BLOCK.getValue(id).defaultBlockState());
            x++;
            if (x > 16) {
                x = 0;
                y++;
            }
        }
        return 1;
    }
}
