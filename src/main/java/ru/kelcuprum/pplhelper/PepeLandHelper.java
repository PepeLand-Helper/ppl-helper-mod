package ru.kelcuprum.pplhelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;
import org.meteordev.starscript.value.Value;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.AlinLogger;
import ru.kelcuprum.alinlib.api.KeyMappingHelper;
import ru.kelcuprum.alinlib.api.events.alinlib.LocalizationEvents;
import ru.kelcuprum.alinlib.api.events.client.ClientLifecycleEvents;
import ru.kelcuprum.alinlib.api.events.client.ClientTickEvents;
import ru.kelcuprum.alinlib.api.events.client.GuiRenderEvents;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfirmScreen;
import ru.kelcuprum.alinlib.gui.screens.DialogScreen;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.alinlib.info.World;
import ru.kelcuprum.alinlib.utils.StealthManager;
import ru.kelcuprum.pplhelper.abi.ABIManager;
import ru.kelcuprum.pplhelper.api.OAuth;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.user.User;
import ru.kelcuprum.pplhelper.command.PPLHelperCommand;
import ru.kelcuprum.pplhelper.gui.screens.*;
import ru.kelcuprum.pplhelper.gui.screens.configs.ConfigScreen;
import ru.kelcuprum.pplhelper.gui.screens.message.NewUpdateScreen;
import ru.kelcuprum.pplhelper.gui.style.VanillaLikeStyle;
import ru.kelcuprum.pplhelper.interactive.InteractiveManager;
import ru.kelcuprum.pplhelper.test.GUIRender;
import ru.kelcuprum.pplhelper.test.LevelTick;
import ru.kelcuprum.pplhelper.utils.FollowManager;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static net.minecraft.world.item.Items.NETHER_STAR;
import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.*;

public class PepeLandHelper implements ClientModInitializer {
    public static final AlinLogger LOG = new AlinLogger("PPL Helper");
    public static User user = null;
    public static Config config = new Config("config/pplhelper/config.json");
    public static boolean isInstalledABI = FabricLoader.getInstance().isModLoaded("actionbarinfo");
    public static boolean isInstalledSailStatus = FabricLoader.getInstance().isModLoaded("sailstatus");
    public static boolean worldsLoaded = false;
    public static String[] worlds = new String[]{"МП1","МП2","МР","МФ","ТЗ","Энд"};
    public static JsonArray commands = new JsonArray();
    public static JsonArray mods = new JsonArray();
    // -=-=-=- Категории -=-=-=-
    public static boolean categoriesAndTags = false;
    public static String[] pc = new String[]{":("};
    public static String[] pct = new String[]{":("};
    public static String[] nc = new String[]{":("};
    public static String[] nct = new String[]{":("};
    public static String[] sc = new String[]{"PepeLand 9"};
    public static String[] sct = new String[]{"ppl9"};
    public static VanillaLikeStyle vanillaLikeStyle = new VanillaLikeStyle();

    @Override
    public void onInitializeClient() {
        LOG.log("-=-=-=-=-=-=-=-", Level.WARN);
        LOG.log("Данный проект не является официальной частью сети серверов PepeLand", Level.WARN);
        LOG.log("-=-=-=-=-=-=-=-", Level.WARN);
        StealthManager.registerActiveManager(() -> {
            boolean isActive = false;
            if (config.getBoolean("STEALTH", false) && playerInPPL() && TabHelper.getWorld() != null) {
                if (config.getBoolean("STEALTH.CURRENT_WORLD", true)) {
                    if (config.getBoolean(String.format("STEALTH.WORLD.%s", TabHelper.getWorld().shortName.toUpperCase()), true))
                        isActive = true;
                } else isActive = true;
            }
            return isActive;
        });
        World.register("minecraft:world_art", "Мир артов");
        World.register("minecraft:world_art_old", "Мир старых артов");
        if (isInstalledABI && !isABILegacy()) ABIManager.register();
        loadUser(false);
        // -=-=-=- -=-=-=-
        // -=-=-=- Ресурс пак -=-=-=-
        FabricLoader.getInstance().getModContainer("pplhelper").ifPresent(s ->
                ResourceManagerHelper.registerBuiltinResourcePack(GuiUtils.getResourceLocation("pplhelper", "icons"), s, Component.translatable("resourcePack.pplhelper.icons"), ResourcePackActivationType.NORMAL)
        );

        // -=-=-=- Сбор информации (НЕ ВАШИХ! [возможно позже]) и авто-обновление -=-=-=-
        ClientLifecycleEvents.CLIENT_FULL_STARTED.register((s) -> {
            gameStarted = true;
            new Thread(() -> {
                loadStaticInformation();
                if(!config.getBoolean("Q.END_9", false)) {
                    Screen parent = AlinLib.MINECRAFT.screen;
                    s.execute(() -> {
                        s.setScreen(new DialogScreen(parent, new String[]{
                                "[Привет.]",
                                "[Спешу сообщить некоторые новости.]",
                                "[К концу июля PepeLand Helper перейдет в режим чтения.]",
                                "[Также, после окончания 9-ого сезона хелпер уйдет на долгий перерыв...]",
                                "[До начала сезона/экспериментов.]",
                                "[Как повезёт.]",
                                "[Вся информация на сайте pplh.ru.]",
                                "[Удачи.]"
                        }, () -> {
                            s.setScreen(parent);
                            config.setBoolean("Q.END_9", true);
                            checkModUpdates(s);
                        }));
                    });
                } else {
                    checkModUpdates(s);
                }
            }).start();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register((s) -> {
            OAuth.stop();
        });
        //TODO: НЕ ЗАВЕРШЕНО !!!!!!!!!
//        ClientTickEvents.START_CLIENT_TICK.register((s) -> {
//            if(playerInPPL() && s.player != null){
//                InteractiveManager.checkPlayerPosition(s.player);
//            }
//        });
//        // -=-=-=- Тесты -=-=-=-
//        if(isTestSubject()){
//            GuiRenderEvents.RENDER.register(new GUIRender());
//            ClientTickEvents.START_CLIENT_TICK.register(new LevelTick());
//        }
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
        KeyMapping key4 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.stealth",
                GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
                "pplhelper"
        ));
        KeyMapping key5 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.stealth.world",
                GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
                "pplhelper"
        ));
        KeyMapping key6 = KeyMappingHelper.register(new KeyMapping(
                "pplhelper.key.stealth.common_world",
                GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
                "pplhelper"
        ));
        ClientTickEvents.START_CLIENT_TICK.register((s) -> {
            if (gameStarted && loginAval != (user == null)) {
                loginAval = user == null;
                if (loginAval) OAuth.run();
                else OAuth.stop();
            }
            if (lastWorld != TabHelper.getWorld() && s.getCurrentServer() != null) {
                lastLobby = (lastWorld == TabHelper.Worlds.LOBBY);
                if (lastLobby) joinTime = System.currentTimeMillis() + 15000;

                lastWorld = TabHelper.getWorld();
            }
            if (restartTime != -1 || joinTime != -1) updateBossBar();
            updateCoordinatesBB();
            if (key1.consumeClick()) {
                if(PepeLandHelperAPI.apiAvailable()) AlinLib.MINECRAFT.setScreen(new ProjectsScreen(AlinLib.MINECRAFT.screen));
                else new ToastBuilder().setTitle(Component.translatable("pplhelper.api")).setMessage(PepeLandHelperAPI.getMessageFromBreakAPI()).setType(ToastBuilder.Type.ERROR).setIcon(WHITE_PEPE).buildAndShow();
            }
            if (key2.consumeClick()) AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(AlinLib.MINECRAFT.screen));
            if (key3.consumeClick() && FollowManager.getCurrentCoordinates() != null) FollowManager.resetCoordinates();
            if (key4.consumeClick()) config.setBoolean("STEALTH", !config.getBoolean("STEALTH", false));
            if (key5.consumeClick() && TabHelper.getWorld() != null && config.getBoolean("STEALTH.CURRENT_WORLD", true))
                config.setBoolean(String.format("STEALTH.WORLD.%s", TabHelper.getWorld().shortName.toUpperCase()), !config.getBoolean(String.format("STEALTH.WORLD.%s", TabHelper.getWorld().shortName.toUpperCase()), true));
            if (key6.consumeClick())
                config.setBoolean("STEALTH.CURRENT_WORLD", !config.getBoolean("STEALTH.CURRENT_WORLD", true));
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
        );
    }

    public static AbstractBuilder[] getPanelWidgets(Screen parent, Screen current) {
        boolean apiEnable = PepeLandHelperAPI.apiAvailable();
        AbstractBuilder[] buttons = new AbstractBuilder[]{
                new ButtonBuilder(Component.translatable("pplhelper.news")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new NewsListScreen(current))).setIcon(WIKI).setCentered(false),
                new ButtonBuilder(Component.translatable("pplhelper.projects")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ProjectsScreen(current))).setIcon(PROJECTS).setCentered(false),
                new ButtonBuilder(Component.translatable("pplhelper.projects.archived")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ArchivedProjectsScreen(current))).setIcon(WHITE_PEPE).setCentered(false),
                new ButtonBuilder(Component.translatable("pplhelper.commands")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new CommandsScreen().build(parent))).setIcon(COMMANDS).setCentered(false),
                new ButtonBuilder(Component.translatable("pplhelper.emotes"))
                        .setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new EmotesScreen().build(parent)))
                        .setIcon(getInstalledPack() == null ? CLOWNFISH : GuiUtils.getResourceLocation("myemotes", "textures/font/emotes/clueless.png"))
                        .setCentered(false).setActive(getInstalledPack() != null),
                new ButtonBuilder(Component.translatable("pplhelper.mods")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ModsScreen().build(parent))).setIcon(Icons.MODS).setCentered(false),
                new ButtonBuilder(Component.translatable("pplhelper.pack")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new UpdaterScreen().build(parent))).setIcon(Icons.PACK_INFO).setCentered(false),
                getProfileButton(parent)
        };
        if(!apiEnable) buttons = new AbstractBuilder[]{
                new TextBuilder(PepeLandHelperAPI.getMessageFromBreakAPI()).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(Colors.CLOWNFISH),
                new ButtonBuilder(Component.translatable("pplhelper.emotes"))
                        .setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new EmotesScreen().build(parent)))
                        .setIcon(getInstalledPack() == null ? CLOWNFISH : GuiUtils.getResourceLocation("myemotes", "textures/font/emotes/clueless.png"))
                        .setCentered(false).setActive(getInstalledPack() != null),
                new ButtonBuilder(Component.translatable("pplhelper.pack")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new UpdaterScreen().build(parent))).setIcon(Icons.PACK_INFO).setCentered(false)
        };
        return buttons;
    }

    public static ButtonBuilder getProfileButton(Screen parent) {
        ButtonBuilder builder = new ButtonBuilder(Component.translatable(user == null ? "pplhelper.oauth.login" : "pplhelper.oauth.profile"));
        builder.setIcon(WIKI).setCentered(false);
        builder.setOnPress((s) -> {
            if (user == null)
                confirmLinkNow(AlinLib.MINECRAFT.screen, String.format("http://localhost:%s", parseInt(config.getString("oauth.port", "11430"))));
            else AlinLib.MINECRAFT.setScreen(new ProfileScreen(parent, user));
        });
        return builder;
    }

    public static void loadUser(boolean withToast) {
        String token = config.getString("oauth.access_token", "");
        if (token.isBlank()) return;
        user = OAuth.getUser(token);
        if (user != null && withToast)
            new ToastBuilder().setTitle(Component.translatable("pplhelper")).setMessage(Component.translatable("pplhelper.oauth.hello", user.nickname == null ? user.username : user.nickname)).setIcon(NETHER_STAR).buildAndShow();
    }

    public static void executeCommand(LocalPlayer player, String command) {
        if (command.startsWith("/")) {
            command = command.substring(1);
            player.connection.sendCommand(command);
        } else {
            player.connection.sendChat(command);
        }
    }

    private static TabHelper.Worlds lastWorld = null;
    private static boolean lastLobby = false;

    private static boolean gameStarted = false;
    private static boolean loginAval = false;

    public static String[] emotes = new String[]{};

    public static boolean isABILegacy() {
        return FabricLoader.getInstance().getModContainer("actionbarinfo").get().getMetadata().getVersion().getFriendlyString().startsWith("1.");
    }
    public static void checkModUpdates(Minecraft s){
//        if (PepeLandHelperAPI.apiAvailable()) {
//            VersionInfo versionInfo = PepeLandHelperAPI.getAutoUpdate(config.getBoolean("UPDATER.FOLLOW_TWO_DOT_ZERO", true));
//            if (versionInfo.state != VersionInfo.State.LATEST && PepeLandHelper.config.getBoolean("PPLH.NOTICE", true)) {
//                if (versionInfo.state == VersionInfo.State.NEW_UPDATE) {
//                    s.execute(() -> s.setScreen(new NewUpdateScreen$Helper(s.screen, versionInfo)));
//                } else {
//                    checkPackUpdates();
//                    if (!FabricLoader.getInstance().isDevelopmentEnvironment()) new ToastBuilder()
//                            .setTitle(Component.literal("PepeLand Helper"))
//                            .setMessage(Component.literal("У вас не опубликованная версия!"))
//                            .setIcon(WARNING)
//                            .setType(ToastBuilder.Type.ERROR)
//                            .buildAndShow();
//                }
//            } else checkPackUpdates();
//        }
        checkPackUpdates();

    }
    public static void checkPackUpdates() {
        try {
            String packVersion = getInstalledPackVersion();
            boolean modrinth = config.getBoolean("PACK.MODRINTH", true);
            if ((config.getBoolean("PACK_UPDATES.NOTICE", true) || config.getBoolean("PACK_UPDATES.AUTO_UPDATE", true)) && !packVersion.isEmpty()) {
                JsonObject packInfo = PepeLandAPI.getPackInfo(onlyEmotesCheck(), modrinth);
                if (config.getBoolean("PACK_UPDATES.NOTICE", true) && !config.getBoolean("PACK_UPDATES.AUTO_UPDATE", false)) {
                    if (!packInfo.get("version").getAsString().equals(packVersion))
                        AlinLib.MINECRAFT.execute(() -> AlinLib.MINECRAFT.setScreen(new NewUpdateScreen(AlinLib.MINECRAFT.screen, packVersion, packInfo, modrinth)));
                } else if (config.getBoolean("PACK_UPDATES.AUTO_UPDATE", false)) {
                    if (!packInfo.get("version").getAsString().equals(packVersion)) {
                        PepeLandHelper.downloadPack(packInfo, onlyEmotesCheck(), (ss) -> {
                            if (ss) {
                                String fileName = String.format("pepeland-%1$s-v%2$s.zip", onlyEmotesCheck() ? "emotes" : "main", packInfo.get("version").getAsString());
                                AlinLib.MINECRAFT.getResourcePackRepository().reload();
                                for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks()) {
                                    if (pack.getDescription().getString().toLowerCase().contains("pepeland pack"))
                                        AlinLib.MINECRAFT.getResourcePackRepository().removePack(pack.getId());
                                }
                                for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()) {
                                    if (pack.getId().contains(fileName))
                                        AlinLib.MINECRAFT.getResourcePackRepository().addPack(pack.getId());
                                }
                                AlinLib.MINECRAFT.options.updateResourcePacks(AlinLib.MINECRAFT.getResourcePackRepository());

                                new ToastBuilder().setTitle(Component.translatable("pplhelper"))
                                        .setIcon(Icons.WHITE_PEPE)
                                        .setMessage(Component.translatable("pplhelper.pack.downloaded", packInfo.get("version").getAsString())).buildAndShow();
                            } else
                                new ToastBuilder().setTitle(Component.translatable("pplhelper")).setMessage(Component.translatable("pplhelper.pack.file_broken")).setIcon(DONT).buildAndShow();
                        }, modrinth);
                    }
                }
            }
        } catch (Exception ex) {
            new ToastBuilder().setTitle(Component.translatable("pplhelper")).setMessage(Component.literal("Произошла ошибка авто-обновления, загляните в логи!")).setType(ToastBuilder.Type.ERROR).setIcon(DONT).buildAndShow();
            ex.printStackTrace();
        }
    }
    public static void loadStaticInformation() {
        new Thread(() -> {
            try {
                if (PepeLandHelperAPI.apiAvailable()) {
                    commands = PepeLandHelperAPI.getCommands();
                    mods = PepeLandHelperAPI.getRecommendMods();
                    //
                    pc = PepeLandHelperAPI.getProjectCategories();
                    pct = PepeLandHelperAPI.getProjectCategoriesTags();
                    nc = PepeLandHelperAPI.getNewsCategories();
                    nct = PepeLandHelperAPI.getNewsCategoriesTags();
                    categoriesAndTags = true;
                    //
                    worlds = PepeLandHelperAPI.getWorlds();
                    worldsLoaded = true;
                } else
                    new ToastBuilder().setTitle(Component.translatable("pplhelper.api"))
                            .setMessage(Component.translatable("pplhelper.api.unavailable"))
                            .setIcon(WHITE_PEPE)
                            .setType(ToastBuilder.Type.ERROR).buildAndShow();
            } catch (Exception ex) {
                Exception exc = new Exception("Ошибка загрузки информации\n" + ex.getMessage());
                exc.setStackTrace(ex.getStackTrace());
                exc.printStackTrace();
                new ToastBuilder().setTitle(Component.literal("Ошибка загрузки информации")).setMessage(Component.literal(ex.getMessage())).setIcon(WHITE_PEPE).setType(ToastBuilder.Type.ERROR).buildAndShow();
            }
        }).start();
    }

    public static boolean isAprilFool() {
        return AlinLib.isAprilFool() || PepeLandHelper.config.getBoolean("IM_A_TEST_SUBJECT.APRIL", false);
    }

    public static boolean isPWGood() {
        return AlinLib.MINECRAFT.getGameProfile().getName().equals("PWGoood") || AlinLib.MINECRAFT.getGameProfile().getName().equals("_PWGood_") || AlinLib.MINECRAFT.getGameProfile().getName().equals("CyCeKu") || PepeLandHelper.config.getBoolean("IM_A_TEST_SUBJECT.PWGOOD", false);
    }

    public static boolean isPPLStreamer() {
        return false;
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
                rtBossBar = new LerpingBossEvent(rtUUID, Component.translatable("pplhelper.restart", getTimestamp(rest)), (float) rest / 300000,
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
                jtBossBar = new LerpingBossEvent(jtUUID, Component.translatable("pplhelper.join", getTimestamp(rest)), (float) rest / 15000,
                        BossEvent.BossBarColor.RED,
                        BossEvent.BossBarOverlay.PROGRESS, false, false, false);
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createAddPacket(jtBossBar));
            }
        }
    }

    public static UUID spUUID = UUID.randomUUID();
    public static LerpingBossEvent spBossBar;
    private static long lastMaxNear = 0;

    public static void updateCoordinatesBB() {
        FollowManager.Coordinates coordinates = FollowManager.getCurrentCoordinates();
        if (coordinates == null || !playerInPPL() || (PepeLandHelper.config.getBoolean("SPROJECT.ABI", true) && isInstalledABI)) {
            if (spBossBar != null) {
                spBossBar = null;
                lastMaxNear = 0;
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createRemovePacket(spUUID));
            }
        } else {
            if (TabHelper.getWorld() == null) return;
            String parsedCoordinates = coordinates.getStringCoordinates();
            LocalPlayer p = AlinLib.MINECRAFT.player;
            long near = 0;
            String huy = "";
            if (!FollowManager.playerInCurrentLevel())
                huy += " (" + FollowManager.getLevelName(coordinates.level()) + ")";
            if (p != null && FollowManager.playerInCurrentWorld() && FollowManager.playerInCurrentLevel()) {
                near = (long) FollowManager.dist(coordinates.coordinates()[0], coordinates.coordinates()[coordinates.coordinates().length - 1], p.getBlockX(), p.getBlockZ());
                if (near <= PepeLandHelper.config.getNumber("SELECTED_PROJECT.AUTO_HIDE", 5).longValue()) {
                    FollowManager.resetCoordinates();
                    lastMaxNear = 0;
                    return;
                } else huy = String.format(" (%s блоков от вас)", near);
            }
            lastMaxNear = Math.max(lastMaxNear, near);
            if (coordinates.world() != null) {
                spBossBar = new LerpingBossEvent(spUUID, Component.translatable("pplhelper.selected_project", coordinates.world().shortName, parsedCoordinates, huy), (float) near / lastMaxNear,
                        BossEvent.BossBarColor.GREEN,
                        BossEvent.BossBarOverlay.PROGRESS, false, false, false);
                AlinLib.MINECRAFT.gui.getBossOverlay().update(ClientboundBossEventPacket.createAddPacket(spBossBar));
            }
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
        return config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false); // !FabricLoader.getInstance().isModLoaded("citresewn") ||
    }
    public static String getInstalledPackVersion() {
        String packVersion = "";
        for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()) {
            String desc = Localization.clearFormatCodes(pack.getDescription().getString()).toLowerCase();
            if (desc.contains("pepeland pack") && AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks().contains(pack)) {
                String[] info = Localization.clearFormatCodes(pack.getDescription().getString()).split("v");
                if (info.length > 1) packVersion = info[1];
            }
        }
        return packVersion;
    }
    public static String[] getEmotes() throws IOException {
        String[] emotes = new String[]{};
        if (getInstalledPack() == null) return emotes;
        InputStream is = getInstalledPack().open().getResource(PackType.CLIENT_RESOURCES, ResourceLocation.withDefaultNamespace("font/uniform.json")).get();
        JsonObject font = GsonHelper.parse(isToString(is));
        JsonArray provider = font.getAsJsonArray("providers");
        emotes = new String[provider.size()];
        int i = 0;
        for (JsonElement element : provider) {
            JsonObject emote = (JsonObject) element;
            emotes[i] = emote.getAsJsonArray("chars").get(0).getAsString();
            LOG.log(emotes[i]);
            i++;
        }
        return emotes;
    }
    private static HashMap<String, String> lastEmotes = null;
    public static HashMap<String, String> getEmotesPath() throws IOException {
        HashMap<String, String> emotes = new HashMap<>();
        if (getInstalledPack() == null) return emotes;
        if (lastEmotes == null) {
            InputStream is = getInstalledPack().open().getResource(PackType.CLIENT_RESOURCES, ResourceLocation.withDefaultNamespace("font/uniform.json")).get();
            JsonObject font = GsonHelper.parse(isToString(is));
            JsonArray provider = font.getAsJsonArray("providers");
            for (JsonElement element : provider) {
                JsonObject emote = (JsonObject) element;
                emotes.put(emote.get("file").getAsString(), emote.getAsJsonArray("chars").get(0).getAsString());
            }
            lastEmotes = emotes;
        } else emotes = lastEmotes;
        return emotes;
    }

    public static String isToString(InputStream is) throws IOException {
        byte[] requestBodyBytes = is.readAllBytes();
        return new String(requestBodyBytes);
    }

    public static Pack getInstalledPack() {
        Pack packVersion = null;
        for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()) {
            String desc = Localization.clearFormatCodes(pack.getDescription().getString()).toLowerCase();
            if (desc.contains("pepeland pack") && AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks().contains(pack)) {
                String[] info = Localization.clearFormatCodes(pack.getDescription().getString()).split("v");
                if (info.length > 1) packVersion = pack;
            }
        }
        return packVersion;
    }

    public static String getAvailablePack() {
        String packId = "";
        for (Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()) {
            if (Localization.clearFormatCodes(pack.getDescription().getString()).toLowerCase().contains("pepeland pack") && !AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks().contains(pack)) {
                packId = pack.getId();
                break;
            }
        }
        return packId;
    }

    public static boolean playerInPPL() {
        return isTestSubject() || (AlinLib.MINECRAFT.getCurrentServer() != null && AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net"));
    }
    public static boolean isTestSubject(){
        return config.getBoolean("IM_A_TEST_SUBJECT", false) || FabricLoader.getInstance().isDevelopmentEnvironment();
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

    public static Thread downloadPack(JsonObject packData, boolean onlyEmote, BooleanConsumer consumer, boolean modrinth) {
        Thread thread = new Thread(() -> {
            try {
                String originalChecksum = packData.get("checksum").getAsString();
                String path = AlinLib.MINECRAFT.getResourcePackDirectory().resolve(String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString())).toString();
                File file = new File(path);
                if (!file.exists())
                    PepeLandAPI.downloadFile$queue(packData.get("url").getAsString(), AlinLib.MINECRAFT.getResourcePackDirectory().toString(), String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString()), originalChecksum, modrinth, 5);
                if (file.exists() && originalChecksum.contains(toSHA(path, modrinth))) {
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

    public static String toSHA(String filePath, boolean modrinth) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(modrinth ? "SHA-512" : "SHA-256");
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
