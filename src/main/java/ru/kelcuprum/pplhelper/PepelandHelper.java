package ru.kelcuprum.pplhelper;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;
import org.meteordev.starscript.value.Value;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.AlinLogger;
import ru.kelcuprum.alinlib.api.KeyMappingHelper;
import ru.kelcuprum.alinlib.api.events.alinlib.LocalizationEvents;
import ru.kelcuprum.alinlib.api.events.client.ClientLifecycleEvents;
import ru.kelcuprum.alinlib.api.events.client.ClientTickEvents;
import ru.kelcuprum.alinlib.api.events.client.TextureManagerEvent;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfirmScreen;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.TextureHelper;
import ru.kelcuprum.pplhelper.gui.configs.ConfigScreen;
import ru.kelcuprum.pplhelper.gui.configs.UpdaterScreen;
import ru.kelcuprum.pplhelper.gui.message.NewUpdateScreen;
import ru.kelcuprum.pplhelper.gui.screens.CommandsScreen;
import ru.kelcuprum.pplhelper.gui.screens.ModsScreen;
import ru.kelcuprum.pplhelper.gui.screens.NewsListScreen;
import ru.kelcuprum.pplhelper.gui.screens.ProjectsScreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.PROJECTS;

public class PepelandHelper implements ClientModInitializer {
    public static final AlinLogger LOG = new AlinLogger("PPL Helper");
    public static Config config = new Config("config/pplhelper/config.json");
    public static boolean isInstalledABI = FabricLoader.getInstance().isModLoaded("actionbarinfo");
    public static Project selectedProject;

    public static AbstractBuilder[] getPanelWidgets(Screen parent, Screen current){
        return new AbstractBuilder[]{
                new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setSprite(OPTIONS).setSize(20, 20),
                new ButtonBuilder(Component.translatable("pplhelper.news")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new NewsListScreen(current))).setSprite(WIKI).setSize(20, 20),
                new ButtonBuilder(Component.translatable("pplhelper.projects")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ProjectsScreen(current))).setSprite(PROJECTS).setSize(20, 20),
                new ButtonBuilder(Component.translatable("pplhelper.commands")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new CommandsScreen().build(parent))).setSprite(LIST).setSize(20, 20),
                new ButtonBuilder(Component.translatable("pplhelper.mods")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ModsScreen().build(parent))).setSprite(Icons.MODS).setSize(20, 20),

                new ButtonBuilder(Component.translatable("pplhelper.pack")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new UpdaterScreen().build(parent))).setSprite(Icons.PACK_INFO).setSize(20, 20)
        };
    }

    public static Worlds getWorld(){
        if((AlinLib.MINECRAFT.getCurrentServer() == null || !AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net")) || AlinLib.MINECRAFT.gui.getTabList().header == null) return null;
        String[] args = AlinLib.MINECRAFT.gui.getTabList().header.getString().split("\n");
        StringBuilder world = new StringBuilder();
        for(String arg : args){
            if(arg.contains("Мир:")){
                String[] parsed = arg.replace("Мир:", "").replaceAll("[^A-Za-zА-Яа-я #0-9]", "").split(" ");
                boolean first = true;
                for(String parsed_arg : parsed) {
                    if(!parsed_arg.isBlank()) {
                        world.append(first ? "" : " ").append(parsed_arg);
                        if(first) first = false;
                    }
                }
            }
        }
        return switch (world.toString()){
            case "Лобби" -> Worlds.LOBBY;
            case "Постройки #1" -> Worlds.CONSTRUCTIONS_1;
            case "Постройки #2" -> Worlds.CONSTRUCTIONS_2;
            case "Ресурсы" -> Worlds.RESOURCE;
            case "Фермы" -> Worlds.FARM;
            case "Торговля" -> Worlds.TRADE;
            default -> null;
        };
    }

    public enum Worlds {
        LOBBY(Component.translatable("pplhelper.world.lobby"), "Лобби"),
        RESOURCE(Component.translatable("pplhelper.world.resource"), "МР"),
        CONSTRUCTIONS_1(Component.translatable("pplhelper.world.constructions.1"), "МП1"),
        CONSTRUCTIONS_2(Component.translatable("pplhelper.world.constructions.2"), "МП2"),
        FARM(Component.translatable("pplhelper.world.farm"), "МФ"),
        TRADE(Component.translatable("pplhelper.world.trade"), "ТЦ"),
        END(Component.translatable("pplhelper.world.end"), "Энд");
        Component title;
        String shortName;
        Worlds(Component title, String shortName){
            this.title = title;
            this.shortName = shortName;
        }
    }

    public static void executeCommand(LocalPlayer player, String command) {
        if (command.startsWith("/")) {
            command = command.substring(1);
            player.connection.sendCommand(command);
        } else {
            player.connection.sendChat(command);
        }
    }

    @Override
    public void onInitializeClient() {
        LOG.log("Данный проект не является официальной частью сети серверов PepeLand", Level.WARN);
        ClientLifecycleEvents.CLIENT_FULL_STARTED.register((s) -> {
            String packVersion = getInstalledPack();
            if((config.getBoolean("PACK_UPDATES.NOTICE", true) || config.getBoolean("PACK_UPDATES.AUTO_UPDATE", true)) && !packVersion.isEmpty()){
                JsonObject packInfo = PepeLandAPI.getPackInfo(onlyEmotesCheck());
                if(config.getBoolean("PACK_UPDATES.NOTICE", true) && !config.getBoolean("PACK_UPDATES.AUTO_UPDATE", false)){
                    if(!packInfo.get("version").getAsString().contains(packVersion))
                        AlinLib.MINECRAFT.setScreen(new NewUpdateScreen(s.screen, packVersion, packInfo));
                } else if(config.getBoolean("PACK_UPDATES.AUTO_UPDATE", false)) {
                    if(!packInfo.get("version").getAsString().contains(packVersion)) {
//                        AlinLib.MINECRAFT.setScreen(new DownloadScreen(s.screen, packInfo, onlyEmotesCheck()));
                        PepelandHelper.downloadPack(packInfo, onlyEmotesCheck(), (ss) -> {
                            if(ss) {
                                String fileName = String.format("pepeland-%1$s-v%2$s.zip", onlyEmotesCheck() ? "emotes" : "main", packInfo.get("version").getAsString());
                                AlinLib.MINECRAFT.getResourcePackRepository().reload();
                                for(Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks()){
                                    if(pack.getDescription().getString().contains("PepeLand Pack"))
                                        AlinLib.MINECRAFT.getResourcePackRepository().removePack(pack.getId());
                                }
                                for(Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()){
                                    if(pack.getId().contains(fileName))
                                        AlinLib.MINECRAFT.getResourcePackRepository().addPack(pack.getId());
                                }
                                AlinLib.MINECRAFT.options.updateResourcePacks(AlinLib.MINECRAFT.getResourcePackRepository());

                                new ToastBuilder().setTitle(Component.translatable("pplhelper"))
                                        .setIcon(PepelandHelper.Icons.WHITE_PEPE)
                                        .setMessage(Component.translatable("pplhelper.pack.downloaded", packInfo.get("version").getAsString())).buildAndShow();
                            }
                            else new ToastBuilder().setTitle(Component.translatable("pplhelper")).setMessage(Component.translatable("pplhelper.pack.file_broken")).setIcon(DONT).buildAndShow();
                        });
                    }
                }
            }
        });
        KeyMapping key1 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.open.projects",
                GLFW.GLFW_KEY_H, // The keycode of the key
                "pplhelper"
        ));
        KeyMapping key2 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.open.news",
                GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
                "pplhelper"
        ));
        KeyMapping key3 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.open.config",
                GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
                "pplhelper"
        ));
        KeyMapping key4 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.unfollow_project",
                GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
                "pplhelper"
        ));
        ClientTickEvents.START_CLIENT_TICK.register((s) -> {
            if(key1.consumeClick()) AlinLib.MINECRAFT.setScreen(new ProjectsScreen(AlinLib.MINECRAFT.screen));
            if(key2.consumeClick()) AlinLib.MINECRAFT.setScreen(new NewsListScreen(AlinLib.MINECRAFT.screen));
            if(key3.consumeClick()) AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(AlinLib.MINECRAFT.screen));
            if(key4.consumeClick()) selectedProject = null;
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register((s) -> TextureHelper.saveMap());
        TextureManagerEvent.INIT.register(TextureHelper::loadTextures);
        LocalizationEvents.DEFAULT_PARSER_INIT.register(starScript -> starScript.ss.set("pplhelper.world", () -> {
            Worlds world = getWorld();
            return Value.string(world == null ? "" : world.title.getString());
        }).set("pplhelper.world_short", () -> {
            Worlds world = getWorld();
            return Value.string(world == null ? "" : world.shortName);
        }));
    }

    public static boolean onlyEmotesCheck(){
        return !FabricLoader.getInstance().isModLoaded("citresewn") || config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false);
    }

    public static String getInstalledPack(){
        String packVersion = "";
        for(Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()){
            if(Localization.clearFormatCodes(pack.getDescription().getString()).contains("PepeLand Pack") && AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks().contains(pack)){
                String[] info = Localization.clearFormatCodes(pack.getDescription().getString()).split("v");
                if(info.length > 1) packVersion = info[1];
            }
        }
        return packVersion;
    }
    public static String getAvailablePack(){
        String packId = "";
        for(Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()) {
            if (Localization.clearFormatCodes(pack.getDescription().getString()).contains("PepeLand Pack") && !AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks().contains(pack)) {
                packId = pack.getId();
                break;
            }
        }
        return packId;
    }
    public interface Icons {
        ResourceLocation WHITE_PEPE = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/white_pepe.png");
        ResourceLocation PACK_INFO = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/pack_info.png");
        ResourceLocation PROJECTS = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/projects.png");
        ResourceLocation MODS = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/mods.png");
        ResourceLocation WEB = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/web.png");
    }

    public static Thread downloadPack(JsonObject packData, boolean onlyEmote, BooleanConsumer consumer){
        Thread thread = new Thread(() -> {
            try {
                String originalChecksum = packData.get("checksum").getAsString();
                String path = AlinLib.MINECRAFT.getResourcePackDirectory().resolve(String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString())).toString();
                File file = new File(path);
                if(!file.exists()) PepeLandAPI.downloadFile$queue(packData.get("url").getAsString(), AlinLib.MINECRAFT.getResourcePackDirectory().toString(), String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString()), originalChecksum, 5);
                if(file.exists() && originalChecksum.contains(toSHA(path))){
                    consumer.accept(true);
                } else {
                    if(file.exists()) file.deleteOnExit();
                    throw new RuntimeException(Component.translatable("pplhelper.pack.file_broken").getString());
                }
            } catch (Exception e) {
                LOG.error(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
                consumer.accept(false);
            }
        });
        thread.start();
        return thread;
    }

    public static String toSHA(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(filePath);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        fis.close();

        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void confirmLinkNow(Screen screen, String link) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmScreen(screen, Icons.WHITE_PEPE, Component.translatable("pplhelper"), Component.translatable("chat.link.confirmTrusted"), link));
    }
}
