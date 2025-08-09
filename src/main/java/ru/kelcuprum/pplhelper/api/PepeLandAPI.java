package ru.kelcuprum.pplhelper.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Level;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.pplhelper.utils.WebUtils;
import ru.kelcuprum.pplhelper.PepeLandHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

import static ru.kelcuprum.pplhelper.PepeLandHelper.toSHA;
import static ru.kelcuprum.pplhelper.utils.WebUtils.getJsonArray;


public class PepeLandAPI {
    public static String API_URL = "https://static-api.pepeland.org/";
    public static String getURI(String url){
        return String.format("%1$s%2$s", API_URL, uriEncode(url));
    }
    public static String uriEncode(String uri){
        return URLEncoder.encode(uri, StandardCharsets.UTF_8);
    }
    public static long lastReq = System.currentTimeMillis();
    public static JsonObject info;
    public static JsonObject getPacksInfo(boolean modrinth){
        try {
            if(modrinth) {
                if(PepeLandHelperAPI.apiAvailable()) return WebUtils.getJsonObject(PepeLandHelperAPI.getURI("resourcepacks/versions", false));
                else {
                    if (lastReq > System.currentTimeMillis() && info != null) return info;
                    else {
                        try {
                            JsonArray jsonObject = getJsonArray(HttpRequest.newBuilder().uri(URI.create("https://api.modrinth.com/v2/project/pepelandrp/version")));
                            JsonObject resp = new JsonObject();
                            for (JsonElement element : jsonObject) {
                                JsonObject info = (JsonObject) element;
                                String ver = info.get("version_number").getAsString();
                                JsonObject add = new JsonObject();
                                add.addProperty("version", ver);
                                add.addProperty("url", info.get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString());
                                add.addProperty("checksum", info.get("files").getAsJsonArray().get(0).getAsJsonObject().get("hashes").getAsJsonObject().get("sha512").getAsString());
                                if (ver.contains("e")) {
                                    if (!resp.has("emotes")) resp.add("emotes", add);
                                } else {
                                    if (!resp.has("main")) resp.add("main", add);
                                }
                            }
                            info = resp;
                            lastReq = System.currentTimeMillis() + 120000;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return info;
                    }
                }
            }
            else return WebUtils.getJsonObject(getURI("resourcepack/latest.json"));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static JsonObject getPackInfo(boolean onlyEmote, boolean modrinth){
        try {
            return getPacksInfo(modrinth).getAsJsonObject(onlyEmote ? "emotes" : "main");
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static void downloadFile(String fileURL, String saveDir, String filename) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        String saveFilePath = saveDir + File.separator + filename;

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            PepeLandHelper.LOG.log("File downloaded successfully.", Level.DEBUG);
        } else {
            PepeLandHelper.LOG.log("No file to download. Server replied HTTP code: " + responseCode, Level.ERROR);
        }
        httpConn.disconnect();
    }
    public static void downloadFile$queue(String fileURL, String saveDir, String filename, String originalChecksum, boolean modrinth, int count){
        int position = 0;
        while (position < count){
            PepeLandHelper.LOG.log("Count: %s", position);
            try {
                downloadFile(fileURL, saveDir, filename);
                String path = AlinLib.MINECRAFT.getResourcePackDirectory().resolve(filename).toString();
                File file = new File(path);
                if(file.exists() && originalChecksum.contains(toSHA(path, modrinth))) position = count;
                else position++;
            } catch (Exception ex){
                position++;
            }
        }
    }
}
