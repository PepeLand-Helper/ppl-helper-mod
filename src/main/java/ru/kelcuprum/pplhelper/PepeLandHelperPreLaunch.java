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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        PepeLandAPI.downloadFile(versionInfo.file,  "./mods", String.format("pplhelper-%s.jar", versionInfo.latestVersion));
        boolean removed = true;
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) LOG.log("не понимаю, не удаляю");
        else {
            Path path = FabricLoaderImpl.INSTANCE.getModContainer("pplhelper").get().getOrigin().getPaths().getFirst();
            removed = path.toFile().delete();
            LOG.log(removed ? "Старая версия была удалена" : "Чет старая версия не удалилась, не понимаю, блокирую!");
        }

        String message = "Обновление "+versionInfo.latestVersion+" было успешно загружено!\nБудьте добры, перезагрузите игру";
        TinyFileDialogs.tinyfd_messageBox("PepeLand Helper | Авто-обновление", message, "ok", "info", false);
        if(!removed) TinyFileDialogs.tinyfd_messageBox("PepeLand Helper | Авто-обновление", "Нам не удалось удалить старую версию мода.\nТак-что будьте еще добры удалить старую версию.", "ok", "error", false);
        System.exit(0);
    }
}
