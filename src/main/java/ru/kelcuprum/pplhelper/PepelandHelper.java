package ru.kelcuprum.pplhelper;

import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.api.KeyMappingHelper;
import ru.kelcuprum.alinlib.api.events.client.ClientLifecycleEvents;
import ru.kelcuprum.alinlib.api.events.client.ClientTickEvents;
import ru.kelcuprum.alinlib.api.events.client.TextureManagerEvent;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.screens.ConfirmScreen;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.TextureHelper;
import ru.kelcuprum.pplhelper.gui.configs.ConfigScreen;
import ru.kelcuprum.pplhelper.gui.message.DownloadScreen;
import ru.kelcuprum.pplhelper.gui.message.NewUpdateScreen;
import ru.kelcuprum.pplhelper.gui.screens.ProjectsScreen;

public class PepelandHelper implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("PPL Helper");
    public static Config config = new Config("config/pplhelper/config.json");
    public static boolean isInstalledABI = FabricLoader.getInstance().isModLoaded("actionbarinfo");
    public static Project selectedProject;

    public static void log(String message) {
        log(message, Level.INFO);
    }

    public static void log(String message, Level level) {
        LOG.log(level, "[" + LOG.getName() + "] " + message);
    }


    @Override
    public void onInitializeClient() {
        log("Данный проект не является официальной частью сети серверов PepeLand", Level.WARN);
        ClientLifecycleEvents.CLIENT_FULL_STARTED.register((s) -> {
            String packVersion = getInstalledPack();
            if((config.getBoolean("PACK_UPDATES.NOTICE", true) || config.getBoolean("PACK_UPDATES.AUTO_UPDATE", true)) && !packVersion.isEmpty()){
                JsonObject packInfo = PepeLandAPI.getPackInfo(PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false));
                if(config.getBoolean("PACK_UPDATES.NOTICE", true) && !config.getBoolean("PACK_UPDATES.AUTO_UPDATE", false)){
                    if(!packInfo.get("version").getAsString().contains(packVersion))
                        AlinLib.MINECRAFT.setScreen(new NewUpdateScreen(s.screen, packVersion, packInfo));
                } else if(config.getBoolean("PACK_UPDATES.AUTO_UPDATE", false)) {
                    if(!packInfo.get("version").getAsString().contains(packVersion)) AlinLib.MINECRAFT.setScreen(new DownloadScreen(s.screen, packInfo, PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false)));
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
            if(key1.consumeClick()) AlinLib.MINECRAFT.setScreen(new ProjectsScreen().build(AlinLib.MINECRAFT.screen));
            if(key3.consumeClick()) AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(AlinLib.MINECRAFT.screen));
            if(key4.consumeClick()) selectedProject = null;
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register((s) -> TextureHelper.saveMap());
        TextureManagerEvent.INIT.register(TextureHelper::loadTextures);
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
    }

    public static void confirmLinkNow(Screen screen, String link) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmScreen(screen, Icons.WHITE_PEPE, Component.translatable("pplhelper"), Component.translatable("chat.link.confirmTrusted"), link));
    }

}
