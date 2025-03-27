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
import ru.kelcuprum.alinlib.info.World;
import ru.kelcuprum.pplhelper.PepeLandHelper;
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
                .then(literal("emotes").then(argument("emote", new EmotesArgumentType()).executes((s) -> {
                            sendFeedback(s, Component.literal("Эмоут был скопирован\n"+getString(s, "emote")));
                            s.getSource().getClient().keyboardHandler.setClipboard(getString(s, "emote"));
                            return 0;
                        }))
                        .then(literal("update").executes((s) -> {
                            PepeLandHelper.emotes = null;
                            sendFeedback(s, Component.literal("Эмоуты были обновлены"));
                            return 0;
                        })))
                .then(literal("e").then(argument("emote", new EmotesArgumentType()).executes((s) -> {
                            sendFeedback(s, Component.literal("Эмоут был скопирован\n"+getString(s, "emote")));
                            s.getSource().getClient().keyboardHandler.setClipboard(getString(s, "emote"));
                            return 0;
                        }))
                        .then(literal("update").executes((s) -> {
                            PepeLandHelper.emotes = null;
                            sendFeedback(s, Component.literal("Эмоуты были обновлены"));
                            return 0;
                        })))
                .then(literal("follow")
                        .then(argument("x", integer())
                                .then(argument("z", integer()).executes((s) -> {
                                    if (!PepeLandHelper.playerInPPL() || TabHelper.getWorld() == null)
                                        sendFeedback(s, Component.literal("Вы не можете использовать команду вне сервера"));
                                    else {
                                        FollowManager.setCoordinates("followed", TabHelper.getWorld(), AlinLib.MINECRAFT.player.level().dimension().location().toString(), getInteger(s, "x"), getInteger(s, "z"));
                                    }
                                    return 0;
                                }).then(argument("world", new WorldArgumentType()).executes((s) -> {
                                    if (!PepeLandHelper.playerInPPL() || TabHelper.getWorld() == null)
                                        sendFeedback(s, Component.literal("Вы не можете использовать команду вне сервера"));
                                    else
                                        FollowManager.setCoordinates("followed", TabHelper.getWorldByShortName(getString(s, "world")), AlinLib.MINECRAFT.player.level().dimension().location().toString(), getInteger(s, "x"), getInteger(s, "z"));
                                    return 0;
                                })))
                        )
                        .then(literal("create")
                                .then(argument("name", new FollowsArgumentType())
                                        .then(argument("x", integer())
                                                .then(argument("z", integer())
                                                        .executes((s) -> {
                                                            if(TabHelper.getWorld() == null) sendFeedback(s, Component.literal("Вы не находитесь на сервере"));
                                                            createFollowedCoordinate(s, getString(s, "name"), TabHelper.getWorld().shortName, World.getCodeName(), getInteger(s, "x"), getInteger(s, "z"));
                                                            return 0;
                                                        }).then(argument("world", new WorldArgumentType())
                                                                .executes((s) -> {
                                                                    createFollowedCoordinate(s, getString(s, "name"), getString(s, "world"), World.getCodeName(), getInteger(s, "x"), getInteger(s, "z"));
                                                                    return 0;
                                                                }).then(argument("level", new LevelArgumentType())
                                                                        .executes((s) -> {
                                                                            createFollowedCoordinate(s, getString(s, "name"), getString(s, "world"), getString(s, "level"), getInteger(s, "x"), getInteger(s, "z"));
                                                                            return 0;
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("remove")
                                .then(argument("name", new FollowsArgumentType()).executes((s) -> {
                                    String id = getString(s, "name");
                                    if((PepeLandHelper.config.toJSON().has("coordinates") ? PepeLandHelper.config.toJSON().getAsJsonObject("coordinates") : new JsonObject()).has(id)){
                                        JsonObject js = (PepeLandHelper.config.toJSON().has("coordinates") ? PepeLandHelper.config.toJSON().getAsJsonObject("coordinates") : new JsonObject());
                                        js.remove(id);
                                        PepeLandHelper.config.setJsonObject("coordinates", js);
                                        sendFeedback(s, Component.literal(String.format("%s был удален", id)));
                                        return 0;
                                    } else {
                                        sendFeedback(s, Component.literal("Таких координат, к счастью, нет :)"));
                                        return 1;
                                    }
                                }))
                        )
                        .then(argument("followed", new FollowsArgumentType()).executes((s) -> {
                            String id = getString(s, "followed");
                            if((PepeLandHelper.config.toJSON().has("coordinates") ? PepeLandHelper.config.toJSON().getAsJsonObject("coordinates") : new JsonObject()).has(id)){
                                JsonObject jsonObject = (PepeLandHelper.config.toJSON().has("coordinates") ? PepeLandHelper.config.toJSON().getAsJsonObject("coordinates") : new JsonObject()).getAsJsonObject(id);
                                FollowManager.setCoordinates(
                                        id,
                                        TabHelper.getWorldByShortName(jsonObject.has("world") ? jsonObject.get("world").getAsString() : TabHelper.getWorld().shortName),
                                        jsonObject.has("level") ? jsonObject.get("level").getAsString() : World.getCodeName(),
                                        jsonObject.get("x").getAsInt(),
                                        jsonObject.get("z").getAsInt()
                                );
                                return 0;
                            } else {
                                sendFeedback(s, Component.literal("Таких координат, к сожалению, нет :("));
                                return 1;
                            }
                        }))
                        .then(literal("reset").executes((s) -> {
                            FollowManager.resetCoordinates();
                            return 0;
                        }))
                )
                .then(literal("unfollow").executes((s) -> {
                    FollowManager.resetCoordinates();
                    return 0;
                }))
                .then(literal("ie_gradient")
                        .then(argument("colors", StringArgumentType.string()).then(argument("name", greedyString())
                                .executes((s) -> {
                                    String name = getString(s, "name");
                                    String[] argsColors = getString(s, "colors").split(",");
                                    JsonArray colorsArray = new JsonArray();
                                    for (int j = 0; j < argsColors.length; j++) {
                                        JsonObject object = new JsonObject();
                                        object.addProperty("pos", ((float) j / (argsColors.length - 1)) * 100);
                                        object.addProperty("hex", "#" + argsColors[j]);
                                        colorsArray.add(object);
                                    }
                                    Util.getPlatform().openUri("https://www.birdflop.com/resources/rgb/?colors=" + uriEncode(colorsArray.toString()) + "&text=" + uriEncode(name));

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

    public static void createFollowedCoordinate(CommandContext<FabricClientCommandSource> s, String id, String world, String level, int x, int z){
        JsonObject coordinates = PepeLandHelper.config.toJSON().has("coordinates") ? PepeLandHelper.config.toJSON().getAsJsonObject("coordinates") : new JsonObject();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", world);
        jsonObject.addProperty("level", level);
        jsonObject.addProperty("x", x);
        jsonObject.addProperty("z", z);
        sendFeedback(s, Component.literal(String.format(coordinates.has(id) ? "%s был отредактирован" : "%s был добавлен", id)));
        coordinates.add(id, jsonObject);
        PepeLandHelper.config.setJsonObject("coordinates", coordinates);
    }

    public static String fixFormatCodes(String text) {
        for (String formatCode : formatCodes.keySet())
            if (formatCode.equalsIgnoreCase(text)) return formatCodes.get(formatCode);
        return "";
    }

    private static final int codes = 16;
    private static final Map<String, String> formatCodes = IntStream.range(0, codes)
            .boxed()
            .collect(Collectors.toMap(List.of(new String[]{
                    "a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
            })::get, List.of(new String[]{
                    "55FF55", "55FFFF", "FF5555", "FF55FF", "FFFF55", "FFFFFF", "000000", "0000AA", "00AA00", "00AAAA", "AA0000", "AA00AA", "FFAA00", "AAAAAA", "555555", "5555FF"
            })::get));

    public static void sendFeedback(CommandContext<FabricClientCommandSource> s, Component component) {
        s.getSource().sendFeedback(Component.empty().append(TabHelper.getGradient("[PPL Helper]: ", 0xFF257570, 0xFF257570)).append(component));
    }

    public static int sendReport(CommandContext<FabricClientCommandSource> s, String reasons) {
        if (PepeLandHelper.playerInPPL() && TabHelper.getWorld() != null) {
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
