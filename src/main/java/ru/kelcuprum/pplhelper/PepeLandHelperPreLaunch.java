package ru.kelcuprum.pplhelper;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.Util;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import ru.kelcuprum.alinlib.AlinLogger;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.VersionInfo;

import java.io.IOException;
import java.nio.file.Path;

public class PepeLandHelperPreLaunch implements PreLaunchEntrypoint  {

    public static Config config = new Config("config/pplhelper/config.json");
    public static final AlinLogger LOG = new AlinLogger("PPL Helper > PreLaunch");

    @Override
    public void onPreLaunch() {
        String alinlib = FabricLoader.getInstance().getModContainer("alinlib").get().getMetadata().getVersion().getFriendlyString();
        if(alinlib.startsWith("2.1.0-alpha") || alinlib.startsWith("2.1.0-beta.1")){
            TinyFileDialogs.tinyfd_messageBox("PepeLand Helper", "У вас установлена не поддерживаемая версия AlinLib! Просим вас обновить библиотеку для стабильной работы!", "error", "error", false);
            Util.getPlatform().openUri("https://modrinth.com/mod/alinlib/versions&l=fabric");
            System.exit(1);
        }
        new Thread(() -> {
            if(config.getBoolean("PPLH.AUTO_UPDATE", false) && PepeLandHelperAPI.apiAvailable()){
                PepeLandHelper.config = config;
                VersionInfo versionInfo = PepeLandHelperAPI.getAutoUpdate(config.getBoolean("UPDATER.FOLLOW_TWO_DOT_ZERO", true));
                if(versionInfo.state == VersionInfo.State.NEW_UPDATE){
                    try {
                        installUpdates(versionInfo);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public static void installUpdates(VersionInfo versionInfo) throws IOException {
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) LOG.log("не быкую, не блокирую");
        else {
            Path path = FabricLoaderImpl.INSTANCE.getModContainer("pplhelper").get().getOrigin().getPaths().getFirst();
            PepeLandAPI.downloadFile(versionInfo.file,  "./mods", path.toFile().getName());
            LOG.log("Файл загружен");
        }

        String message = "Обновление "+versionInfo.latestVersion+" было успешно загружено!\nБудьте добры, перезагрузите игру.\n\nChange-log:\n"+versionInfo.changelog;
        TinyFileDialogs.tinyfd_messageBox("PepeLand Helper | Автообновление", message, "ok", "info", false);
        System.exit(0);
    }
}
