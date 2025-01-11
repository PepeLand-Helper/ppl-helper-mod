package ru.kelcuprum.pplhelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.meteordev.starscript.value.Value;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.AlinLogger;
import ru.kelcuprum.alinlib.api.KeyMappingHelper;
import ru.kelcuprum.alinlib.api.events.alinlib.LocalizationEvents;
import ru.kelcuprum.alinlib.api.events.client.ClientLifecycleEvents;
import ru.kelcuprum.alinlib.api.events.client.ClientTickEvents;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfirmScreen;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.alinlib.info.World;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.screens.configs.ConfigScreen;
import ru.kelcuprum.pplhelper.gui.screens.UpdaterScreen;
import ru.kelcuprum.pplhelper.gui.screens.message.NewUpdateScreen;
import ru.kelcuprum.pplhelper.gui.screens.CommandsScreen;
import ru.kelcuprum.pplhelper.gui.screens.ModsScreen;
import ru.kelcuprum.pplhelper.gui.screens.NewsListScreen;
import ru.kelcuprum.pplhelper.gui.screens.ProjectsScreen;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.*;

public class PepelandHelper implements ClientModInitializer {
    public static final AlinLogger LOG = new AlinLogger("PPL Helper");
    public static Config config = new Config("config/pplhelper/config.json");
    public static boolean isInstalledABI = FabricLoader.getInstance().isModLoaded("actionbarinfo");
    public static boolean worldsLoaded = false;
    public static String[] worlds = new String[]{
            "МП1",
            "МП2",
            "МР",
            "МФ",
            "ТЗ",
            "Энд"
    };
    public static JsonArray commands = new JsonArray();
    public static JsonArray mods = new JsonArray();
    public static Project selectedProject;

    public static AbstractBuilder[] getPanelWidgets(Screen parent, Screen current) {
        boolean api = PepeLandHelperAPI.apiAvailable();
        return new AbstractBuilder[]{
                new ButtonBuilder(Component.translatable("pplhelper.news")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new NewsListScreen(current))).setIcon(WIKI).setCentered(false).setSize(20, 20),
                new ButtonBuilder(Component.translatable("pplhelper.projects")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ProjectsScreen(current))).setIcon(PROJECTS).setCentered(false).setSize(20, 20),
                new ButtonBuilder(Component.translatable("pplhelper.commands")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new CommandsScreen().build(parent))).setIcon(COMMANDS).setCentered(false).setSize(20, 20).setActive(api),
                new ButtonBuilder(Component.translatable("pplhelper.mods")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ModsScreen().build(parent))).setIcon(Icons.MODS).setCentered(false).setSize(20, 20).setActive(api),
                new ButtonBuilder(Component.translatable("pplhelper.pack")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new UpdaterScreen().build(parent))).setIcon(Icons.PACK_INFO).setCentered(false).setSize(20, 20),
        };
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
        LOG.log("-=-=-=-=-=-=-=-", Level.WARN);
        LOG.log("Данный проект не является официальной частью сети серверов PepeLand", Level.WARN);
        LOG.log("-=-=-=-=-=-=-=-", Level.WARN);
        // -=-=-=- Ресурс пак -=-=-=-
        FabricLoader.getInstance().getModContainer("pplhelper").ifPresent(s -> {
            ResourceManagerHelper.registerBuiltinResourcePack(GuiUtils.getResourceLocation("pplhelper","icons"), s, Component.translatable("resourcePack.pplhelper.icons"), ResourcePackActivationType.NORMAL);
        });
        // -=-=-=- Сбор информации (НЕ ВАШИХ!) и авто-обновление -=-=-=-
        ClientLifecycleEvents.CLIENT_FULL_STARTED.register((s) -> {
            loadStaticInformation();
            String packVersion = getInstalledPack();
            if ((config.getBoolean("PACK_UPDATES.NOTICE", true) || config.getBoolean("PACK_UPDATES.AUTO_UPDATE", true)) && !packVersion.isEmpty()) {
                JsonObject packInfo = PepeLandAPI.getPackInfo(onlyEmotesCheck());
                if (config.getBoolean("PACK_UPDATES.NOTICE", true) && !config.getBoolean("PACK_UPDATES.AUTO_UPDATE", false)) {
                    if (!packInfo.get("version").getAsString().contains(packVersion))
                        AlinLib.MINECRAFT.setScreen(new NewUpdateScreen(s.screen, packVersion, packInfo));
                } else if (config.getBoolean("PACK_UPDATES.AUTO_UPDATE", false)) {
                    if (!packInfo.get("version").getAsString().contains(packVersion)) {
                        PepelandHelper.downloadPack(packInfo, onlyEmotesCheck(), (ss) -> {
                            if (ss) {
                                String fileName = String.format("pepeland-%1$s-v%2$s.zip", onlyEmotesCheck() ? "emotes" : "main", packInfo.get("version").getAsString());
                                AlinLib.MINECRAFT.getResourcePackRepository().reload();
                                for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks()) {
                                    if (pack.getDescription().getString().contains("PepeLand Pack"))
                                        AlinLib.MINECRAFT.getResourcePackRepository().removePack(pack.getId());
                                }
                                for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()) {
                                    if (pack.getId().contains(fileName))
                                        AlinLib.MINECRAFT.getResourcePackRepository().addPack(pack.getId());
                                }
                                AlinLib.MINECRAFT.options.updateResourcePacks(AlinLib.MINECRAFT.getResourcePackRepository());

                                new ToastBuilder().setTitle(Component.translatable("pplhelper"))
                                        .setIcon(PepelandHelper.Icons.WHITE_PEPE)
                                        .setMessage(Component.translatable("pplhelper.pack.downloaded", packInfo.get("version").getAsString())).buildAndShow();
                            } else
                                new ToastBuilder().setTitle(Component.translatable("pplhelper")).setMessage(Component.translatable("pplhelper.pack.file_broken")).setIcon(DONT).buildAndShow();
                        });
                    }
                }
            }
        });
        // -=-=-=- Команда -=-=-=-
        ClientCommandRegistrationCallback.EVENT.register(PPLHelperCommand::register);
        // -=-=-=- Бинды -=-=-=-
        KeyMapping key1 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.open.projects",
                GLFW.GLFW_KEY_H, // The keycode of the key
                "pplhelper"
        ));
        KeyMapping key2 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.open.config",
                GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
                "pplhelper"
        ));
        KeyMapping key3 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.unfollow_project",
                GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
                "pplhelper"
        ));
        ClientTickEvents.START_CLIENT_TICK.register((s) -> {
            if (restartTime != -1 || joinTime != -1) updateBossBar();
            updateCoordinatesBB();
            if (key1.consumeClick()) AlinLib.MINECRAFT.setScreen(new ProjectsScreen(AlinLib.MINECRAFT.screen));
            if (key2.consumeClick()) AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(AlinLib.MINECRAFT.screen));
            if (key3.consumeClick() && selectedProject != null) selectedProject = null;
        });
        // -=-=-=- Локализация -=-=-=-
        LocalizationEvents.DEFAULT_PARSER_INIT.register(starScript -> starScript.ss.set("pplhelper.world", () -> {
                    TabHelper.Worlds world = TabHelper.getWorld();
                    return Value.string(world == null ? "" : world.title.getString());
                }).set("pplhelper.world_short", () -> {
                    TabHelper.Worlds world = TabHelper.getWorld();
                    return Value.string(world == null ? "" : world.shortName);
                }).set("pplhelper.tps", () -> Value.number(TabHelper.getTPS()))
                .set("pplhelper.online", () -> Value.number(TabHelper.getOnline()))
                .set("pplhelper.max_online", () -> Value.number(TabHelper.getMaxOnline()))

                .set("pplhelper.selected_project.name", () -> Value.string(selectedProject == null ? "" : selectedProject.title))
                .set("pplhelper.selected_project.description", () -> Value.string(selectedProject == null ? "" : selectedProject.description))
                .set("pplhelper.selected_project.creators", () -> Value.string(selectedProject == null ? "" : selectedProject.creators))
                .set("pplhelper.selected_project.id", () -> Value.number(selectedProject == null ? 0 : selectedProject.id))
                .set("pplhelper.selected_project.coordinates", () -> Value.string(selectedProject == null ? "" : getStringSelectedProjectCoordinates())));
    }

    public static void loadStaticInformation(){
        try{
            if(PepeLandHelperAPI.apiAvailable()) {
                commands = PepeLandHelperAPI.getCommands();
                mods = PepeLandHelperAPI.getRecommendMods();
                worlds = PepeLandHelperAPI.getWorlds();
                worldsLoaded = true;
            } else
                new ToastBuilder().setTitle(Component.translatable("pplhelper.api"))
                        .setMessage(Component.translatable("pplhelper.api.unavailable"))
                        .setIcon(WHITE_PEPE)
                        .setType(ToastBuilder.Type.ERROR).buildAndShow();
        } catch (Exception ex){
            Exception exc = new Exception("Ошибка загрузки информации\n"+ex.getMessage());
            exc.setStackTrace(ex.getStackTrace());
            exc.printStackTrace();
            new ToastBuilder().setTitle(Component.literal("Ошибка загрузки информации")).setMessage(Component.literal(ex.getMessage())).setIcon(WHITE_PEPE).setType(ToastBuilder.Type.ERROR).buildAndShow();
        }
    }

    public static @NotNull String getStringSelectedProjectCoordinates() {
        String coordinates = "";
        if(World.getCodeName().equals("minecraft:overworld") && PepelandHelper.selectedProject.coordinates$overworld != null && !PepelandHelper.selectedProject.coordinates$overworld.isEmpty())
            coordinates = PepelandHelper.selectedProject.coordinates$overworld;
        else if(World.getCodeName().equals("minecraft:the_nether") && PepelandHelper.selectedProject.coordinates$nether != null && !PepelandHelper.selectedProject.coordinates$nether.isEmpty())
            coordinates = PepelandHelper.selectedProject.coordinates$nether;
        else if(World.getCodeName().equals("minecraft:the_end") && PepelandHelper.selectedProject.coordinates$end != null && !PepelandHelper.selectedProject.coordinates$end.isEmpty())
            coordinates = PepelandHelper.selectedProject.coordinates$end;
        return coordinates.replaceAll("[^0-9 \\-.]", "");
    }
    public static float dist(int i, int j, int k, int l) {
        int m = k - i;
        int n = l - j;
        return Mth.sqrt((float)(m * m + n * n));
    }

    public static long restartTime = 0;
    public static long joinTime = 0;
    public static UUID rtUUID = UUID.randomUUID();
    public static UUID jtUUID = UUID.randomUUID();
    public static LerpingBossEvent rtBossBar;
    public static LerpingBossEvent jtBossBar;

    public static void updateBossBar() {
        if (restartTime == 0) {
            restartTime = -1;
            if (rtBossBar != null) {
                rtBossBar = null;
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createRemovePacket(rtUUID));
            }
        } else if (!playerInPPL()) {
            if (rtBossBar != null) {
                rtBossBar = null;
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createRemovePacket(rtUUID));
            }
        } else {
            long rest = restartTime - System.currentTimeMillis();
            if (rest <= 0 || !config.getBoolean("TIMER.RESTART", true)) {
                restartTime = 0;
            } else {
                rtBossBar = new LerpingBossEvent(rtUUID, Component.translatable("pplhelper.restart", getTimestamp(rest)), (float) rest/300000,
                        (rest >= 180000 ? BossEvent.BossBarColor.GREEN : rest >= 60000 ? BossEvent.BossBarColor.YELLOW : BossEvent.BossBarColor.RED),
                        BossEvent.BossBarOverlay.NOTCHED_20, false, false, false);
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createAddPacket(rtBossBar));
            }
        }

        if (joinTime == 0) {
            joinTime = -1;
            if (jtBossBar != null) {
                jtBossBar = null;
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createRemovePacket(jtUUID));
            }
        } else if (!playerInPPL() || TabHelper.getWorld() != TabHelper.Worlds.LOBBY) {
            if (jtBossBar != null) {
                jtBossBar = null;
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createRemovePacket(jtUUID));
            }
        } else {
            long rest = joinTime - System.currentTimeMillis();
            if (rest <= 0 || !config.getBoolean("TIMER.JOIN", true)) {
                joinTime = 0;
            } else {
                jtBossBar = new LerpingBossEvent(jtUUID, Component.translatable("pplhelper.join", getTimestamp(rest)), (float) rest/15000,
                        BossEvent.BossBarColor.RED,
                        BossEvent.BossBarOverlay.PROGRESS, false, false, false);
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createAddPacket(jtBossBar));
            }
        }
    }

    public static UUID spUUID = UUID.randomUUID();
    public static LerpingBossEvent spBossBar;
    private static long lastMaxNear = 0;
    public static void updateCoordinatesBB(){
        if (selectedProject == null || !playerInPPL() || (PepelandHelper.config.getBoolean("SPROJECT.ABI", true) && isInstalledABI)) {
            if (spBossBar != null) {
                spBossBar = null;
                lastMaxNear = 0;
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createRemovePacket(spUUID));
            }
        } else {
            if(TabHelper.getWorld() == null) return;
            String parsedCoordinates = getStringSelectedProjectCoordinates();
            if(parsedCoordinates.isEmpty()) return;
            LocalPlayer p = AlinLib.MINECRAFT.player;
            long near = 0;
            String huy = "";
            if(p != null && PepelandHelper.selectedProject.world.equalsIgnoreCase(TabHelper.getWorld().shortName)) {
                String[] args = parsedCoordinates.split(" ");
                near = (long) dist(parseInt(args[0]), parseInt(args[args.length-1]),p.getBlockX(), p.getBlockZ());
                if(near <= PepelandHelper.config.getNumber("SELECTED_PROJECT.AUTO_HIDE", 15).longValue()){
                    PepelandHelper.selectedProject = null;
                    lastMaxNear = 0;
                    return;
                } else huy = String.format(" (%s блоков от вас)", near);
            }
            lastMaxNear = Math.max(lastMaxNear, near);
            spBossBar = new LerpingBossEvent(spUUID, Component.translatable("pplhelper.selected_project", PepelandHelper.selectedProject.world, parsedCoordinates, huy), (float) near / lastMaxNear,
                        BossEvent.BossBarColor.GREEN,
                        BossEvent.BossBarOverlay.PROGRESS, false, false, false);
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createAddPacket(spBossBar));
        }
    }

    public static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    public static boolean onlyEmotesCheck() {
        return !FabricLoader.getInstance().isModLoaded("citresewn") || config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false);
    }

    public static String getInstalledPack() {
        String packVersion = "";
        for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()) {
            if (Localization.clearFormatCodes(pack.getDescription().getString()).contains("PepeLand Pack") && AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks().contains(pack)) {
                String[] info = Localization.clearFormatCodes(pack.getDescription().getString()).split("v");
                if (info.length > 1) packVersion = info[1];
            }
        }
        return packVersion;
    }

    public static String getAvailablePack() {
        String packId = "";
        for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()) {
            if (Localization.clearFormatCodes(pack.getDescription().getString()).contains("PepeLand Pack") && !AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks().contains(pack)) {
                packId = pack.getId();
                break;
            }
        }
        return packId;
    }

    public static boolean playerInPPL() {
        return config.getBoolean("IM_A_TEST_SUBJECT", false) || (AlinLib.MINECRAFT.getCurrentServer() != null && AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net"));
    }

    public interface Icons {
        ResourceLocation WHITE_PEPE = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/white_pepe.png");
        ResourceLocation PEPE = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/pepe.png");
        ResourceLocation PACK_INFO = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/pack_info.png");
        ResourceLocation PROJECTS = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/projects.png");
        ResourceLocation COMMANDS = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/commands.png");
        ResourceLocation MODS = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/mods.png");
        ResourceLocation WEB = GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/web.png");
    }

    public static Thread downloadPack(JsonObject packData, boolean onlyEmote, BooleanConsumer consumer) {
        Thread thread = new Thread(() -> {
            try {
                String originalChecksum = packData.get("checksum").getAsString();
                String path = AlinLib.MINECRAFT.getResourcePackDirectory().resolve(String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString())).toString();
                File file = new File(path);
                if (!file.exists())
                    PepeLandAPI.downloadFile$queue(packData.get("url").getAsString(), AlinLib.MINECRAFT.getResourcePackDirectory().toString(), String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString()), originalChecksum, 5);
                if (file.exists() && originalChecksum.contains(toSHA(path))) {
                    consumer.accept(true);
                } else {
                    if (file.exists()) file.deleteOnExit();
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
