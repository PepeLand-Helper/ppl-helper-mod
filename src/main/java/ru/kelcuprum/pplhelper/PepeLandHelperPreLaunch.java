package ru.kelcuprum.pplhelper;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
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
        if(config.getBoolean("PPLH.AUTO_UPDATE", false)){
            PepelandHelper.config = config;
            VersionInfo versionInfo = PepeLandHelperAPI.getAutoUpdate();
            if(versionInfo.state == VersionInfo.State.NEW_UPDATE){
                try {
                    installUpdates(versionInfo);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void installUpdates(VersionInfo versionInfo) throws IOException {
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) LOG.log("не быкую, не блокирую");
        else {
            Path path = FabricLoaderImpl.INSTANCE.getModContainer("pplhelper").get().getOrigin().getPaths().getFirst();
            PepeLandAPI.downloadFile(versionInfo.file,  "./mods", path.toFile().getName());
            LOG.log("Файл загружен");
        }

        String message = "Обновление "+versionInfo.latestVersion+" было успешно загружено!\nБудьте добры, перезагрузите игру.";
        TinyFileDialogs.tinyfd_messageBox("PepeLand Helper | Автообновление", message, "ok", "info", false);
        System.exit(0);
    }
}
