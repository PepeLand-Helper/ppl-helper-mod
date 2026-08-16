package ru.pplh.mod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.Level;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import ru.kelcuprum.alinlib.AlinLogger;
import ru.kelcuprum.alinlib.WebAPI;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.pplh.mod.api.OAuth;
import ru.pplh.mod.api.PepeLandAPI;
import ru.pplh.mod.api.PepeLandHelperAPI;
import ru.pplh.mod.api.components.VersionInfo;
import ru.pplh.mod.api.components.user.User;
import ru.pplh.mod.utils.WebUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static net.minecraft.world.item.Items.NETHER_STAR;
import static ru.pplh.mod.api.OAuth.Objects.NOT_FOUND;
import static ru.pplh.mod.api.PepeLandAPI.uriEncode;
import static ru.pplh.mod.utils.WebUtils.httpClient;

public class PepeLandHelperPreLaunch implements PreLaunchEntrypoint  {

    public static Config config = new Config("config/pplhelper/config.json");
    public static final AlinLogger LOG = new AlinLogger("PPL Helper > PreLaunch");

    @Override
    public void onPreLaunch() {
        Optional<ModContainer> april = FabricLoader.getInstance().getModContainer("pplhelper_april");
        User user = loadUser();
        boolean isApril = isAprilFool();
        if(!isApril) isApril = config.getBoolean("IM_A_TEST_SUBJECT.APRIL") && user != null && user.role.TESTING_APRIL_FOOL;
        if(isApril){
            try {
                HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(PepeLandHelperAPI.getURI("/april")));
                if(user != null) builder.header("Authorization", "Bearer "+ PepeLandHelper.config.getString("oauth.access_token", ""));
                JsonObject notFound = WebAPI.getJsonObject(builder);
                if(notFound == NOT_FOUND) return;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String minecraft = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString();
                if(april.isEmpty()){
                    try {
                        boolean downloaded = downloadFile(PepeLandHelperAPI.getURI(String.format("april/get?version=%s", minecraft),false),  "./mods", String.format("april_fool-mc%s.jar", minecraft), user);
                        if(downloaded) {
                            LOG.log("Файл загружен");
                            String message = "Обновление l;ajksda было успешно загружено!\nБудьте добры, перезагрузите игру.\n\nChange-log:\n- ladlkajdljasldad\n- s;kljda;ldasl;dja\n- l;69696969";
                            TinyFileDialogs.tinyfd_messageBox("????Land Helper | lhafdals;l;hasfd", message, "ok", "info", 0);
                            System.exit(0);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        } else {
            if(april.isPresent()){
                try {
                    april.get().getOrigin().getPaths().getFirst().toFile().delete();
                    TinyFileDialogs.tinyfd_messageBox("PepeLand Helper", "Перезагрузите игру, пожалуйста.", "ok", "info", 0);
                    System.exit(0);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static User loadUser() {
        String token = config.getString("oauth.access_token", "");
        if (token.isBlank()) return null;
        return OAuth.getUser(token);
    }

    public static boolean isAprilFool() {
        return (LocalDate.now().getMonthValue() == 4 && LocalDate.now().getDayOfMonth() == 1);
    }

    public static void installUpdates(VersionInfo versionInfo) throws IOException {
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) LOG.log("не быкую, не блокирую");
        else {
            Path path = FabricLoaderImpl.INSTANCE.getModContainer("pplhelper").get().getOrigin().getPaths().getFirst();
            PepeLandAPI.downloadFile(versionInfo.file,  "./mods", path.toFile().getName());
            LOG.log("Файл загружен");
        }

        String message = "Обновление "+versionInfo.latestVersion+" было успешно загружено!\nБудьте добры, перезагрузите игру.\n\nChange-log:\n"+versionInfo.changelog;
        TinyFileDialogs.tinyfd_messageBox("PepeLand Helper | Автообновление", message, "ok", "info", 0);
        System.exit(0);
    }

    public static boolean downloadFile(String fileURL, String saveDir, String filename, User user){
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(fileURL));
            if(user != null) builder.header("Authorization", "Bearer "+ PepeLandHelper.config.getString("oauth.access_token", ""));
            if (httpClient == null) {
                httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(10L)).build();
            }
            HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            if(response.statusCode() != 200) return false;
            byte[] bytes = response.body();
            String saveFilePath = saveDir + File.separator + filename;
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
            outputStream.write(bytes);
            outputStream.close();
            PepeLandHelper.LOG.log("File downloaded successfully.", Level.DEBUG);
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
