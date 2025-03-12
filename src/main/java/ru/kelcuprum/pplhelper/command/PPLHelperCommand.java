package ru.kelcuprum.pplhelper.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.info.Player;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.projects.FollowProject;
import ru.kelcuprum.pplhelper.utils.FollowManager;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static ru.kelcuprum.pplhelper.api.PepeLandAPI.uriEncode;

public class PPLHelperCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("pplhelper").then(literal("report")
                        .then(argument("description", greedyString()).executes((s) -> sendReport(s, getString(s, "description"))))
                        .executes((s) -> sendReport(s, "<описание>")))
                .then(literal("follow")
                        .then(argument("x", integer())
                                .then(argument("z", integer()).executes((s) -> {
                                    if(!PepelandHelper.playerInPPL() || TabHelper.getWorld() == null) sendFeedback(s, Component.literal("Вы не можете использовать команду вне сервера"));
                                    else {
//                                        PepelandHelper.selectedProject = new FollowProject(TabHelper.getWorld().shortName, String.format("%s %s", getInteger(s, "x"), getInteger(s, "z")), AlinLib.MINECRAFT.player.level().dimension().location().toString());
                                        FollowManager.setCoordinates("followed", TabHelper.getWorld(), AlinLib.MINECRAFT.player.level().dimension().location().toString(), getInteger(s, "x"), getInteger(s, "z"));
                                    }
                                    return 0;
                                })))

                        .then(argument("x", integer())
                                .then(argument("z", integer())
                                        .then(argument("world", new WorldArgumentType()).executes((s) -> {
                                            if(!PepelandHelper.playerInPPL() || TabHelper.getWorld() == null) sendFeedback(s, Component.literal("Вы не можете использовать команду вне сервера"));
                                            else FollowManager.setCoordinates("followed", TabHelper.getWorldByShortName(getString(s, "world")), AlinLib.MINECRAFT.player.level().dimension().location().toString(), getInteger(s, "x"), getInteger(s, "z"));
                                            return 0;
                                        }))
                                )
                        )
                ).then(literal("ie_gradient")
                        .then(argument("colors", StringArgumentType.string()).then(argument("name", greedyString())
                                .executes((s) -> {
                                    String name = getString(s, "name");
                                    String[] argsColors = getString(s, "colors").split(",");
                                    JsonArray colorsArray = new JsonArray();
                                    for(int j = 0; j<argsColors.length; j++) {
                                        JsonObject object = new JsonObject();
                                        object.addProperty("pos", ((float) j /(argsColors.length-1)) * 100);
                                        object.addProperty("hex", "#"+argsColors[j]);
                                        colorsArray.add(object);
                                    }
                                    Util.getPlatform().openUri("https://www.birdflop.com/resources/rgb/?colors="+uriEncode(colorsArray.toString())+"&text="+uriEncode(name));

                                    return 0;
                                })))

                        .executes((s) -> {
                            Util.getPlatform().openUri("https://www.birdflop.com/resources/rgb");
                            return 0;
                        })
                )
                .executes(s -> {
                    sendFeedback(s, Component.literal(String.format("Привет, %s!", Player.getName())));
                    return 0;
                }));
    }

    public static String fixFormatCodes(String text) {
        for (String formatCode : formatCodes.keySet())
            if(formatCode.equalsIgnoreCase(text)) return formatCodes.get(formatCode);
        return "";
    }

    private static final int codes = 16;
    private static final Map<String, String> formatCodes = IntStream.range(0, codes)
            .boxed()
            .collect(Collectors.toMap(List.of(new String[]{
                    "a","b","c","d","e","f","0","1","2","3","4","5","6","7","8","9"
            })::get, List.of(new String[]{
                    "55FF55","55FFFF","FF5555","FF55FF","FFFF55","FFFFFF","000000","0000AA","00AA00","00AAAA","AA0000","AA00AA","FFAA00","AAAAAA","555555","5555FF"
            })::get));

    public static void sendFeedback(CommandContext<FabricClientCommandSource> s, Component component) {
        s.getSource().sendFeedback(Component.empty().append(TabHelper.getGradient("[PPL Helper]: ", 0xFF257570, 0xFF257570)).append(component));
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
        } else {
            sendFeedback(s, Component.literal("Вы не можете использовать команду вне сервера"));
            return 1;
        }
    }
}
