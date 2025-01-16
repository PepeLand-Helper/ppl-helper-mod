package ru.kelcuprum.pplhelper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.info.Player;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.projects.FollowProject;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import java.util.Locale;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PPLHelperCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("pplhelper").then(literal("report").then(argument("description", greedyString()).executes((s) -> sendReport(s, getString(s, "description")))).executes((s) -> sendReport(s, "<описание>")))
                .then(literal("follow")
                        .then(argument("x", integer())
                                .then(argument("z", integer()).executes((s) -> {
                                    PepelandHelper.selectedProject = new FollowProject(TabHelper.getWorld().shortName, String.format("%s %s", getInteger(s, "x"), getInteger(s, "z")), AlinLib.MINECRAFT.player.level().dimension().location().toString());
                                    return 0;
                                })))

                        .then(argument("x", integer())
                                .then(argument("z", integer())
                                        .then(argument("world", new WorldArgumentType()).executes((s) -> {
                                            PepelandHelper.selectedProject = new FollowProject(getString(s, "world"), String.format("%s %s", getInteger(s, "x"), getInteger(s, "z")), AlinLib.MINECRAFT.player.level().dimension().location().toString());
                                            return 0;
                                        }))
                                )
                        )
                ).executes(s -> {
                    sendFeedback(s, Component.literal(String.format("Привет, %s!", Player.getName())));
                    return 0;
                }));
    }

    public static void sendFeedback(CommandContext<FabricClientCommandSource> s, Component component) {
        s.getSource().sendFeedback(Component.empty().append(Localization.fixFormatCodes("&a&l[PPL Helper]:&r ")).append(component));
    }

    public static int sendReport(CommandContext<FabricClientCommandSource> s, String reasons) {
        if (PepelandHelper.playerInPPL() && TabHelper.getWorld() != null) {
            String reportFormat = "1. %s\n2. `[%s]` %s\n3. %s";
            String playerName = Player.getName();
            if (playerName.replace("_", "").length() <= playerName.length() - 2)
                playerName = String.format("`%s`", playerName);
            String command = String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", AlinLib.MINECRAFT.player.level().dimension().location(), AlinLib.MINECRAFT.player.getX(), AlinLib.MINECRAFT.player.getY(), AlinLib.MINECRAFT.player.getZ(), AlinLib.MINECRAFT.player.getYRot(), AlinLib.MINECRAFT.player.getXRot());
            String report = String.format(reportFormat, playerName, TabHelper.getWorld().shortName, command, reasons);
            s.getSource().getClient().keyboardHandler.setClipboard(report);
            sendFeedback(s, Component.literal(String.format("Сообщение репорта было скопировано в буфер обмена:\n%s", report)));
            return 0;
        } else return 1;
    }
}
