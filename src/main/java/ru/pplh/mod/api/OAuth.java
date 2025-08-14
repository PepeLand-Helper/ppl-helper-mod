package ru.pplh.mod.api;

import com.google.gson.JsonObject;
import express.Express;
import express.middleware.CorsOptions;
import express.middleware.Middleware;
import express.utils.MediaType;
import express.utils.Status;
import net.minecraft.util.GsonHelper;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.AlinLogger;
import ru.pplh.mod.utils.WebUtils;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.api.components.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

import static java.lang.Integer.parseInt;
import static ru.pplh.mod.PepeLandHelper.config;
import static ru.pplh.mod.api.PepeLandAPI.uriEncode;
import static ru.kelcuprum.alinlib.utils.GsonHelper.getStringInJSON;

public class OAuth {
    public static boolean state = false;
    public static boolean corsSetting = false;
    public static AlinLogger logger = new AlinLogger("PPL Helper/OAuth");
    public static Express app = new Express(config.getString("oauth.hostname", "127.0.0.1"));
    public static String htmlAuth;

    public static String getURI(String url){
        return getURI(url, true);
    }
    public static String getURI(String url, boolean uriEncode){
        String api = PepeLandHelper.config.getString("oauth.url", "https://auth.pplh.ru/");
        if(!api.endsWith("/")) api+="/";
        if(url.startsWith("/")) url=url.substring(1);
        return String.format("%1$s%2$s", api, uriEncode ? uriEncode(url) : url);
    }

    public static void run() {
        app = new Express(config.getString("oauth.hostname", "127.0.0.1"));
        try {
            InputStream releaseFile = PepeLandHelper.class.getResourceAsStream("/www/auth.html");
            if(releaseFile != null) htmlAuth = new String(releaseFile.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        if (!corsSetting) {
            corsSetting = true;
            CorsOptions corsOptions = new CorsOptions();
            corsOptions.setOrigin("*");
            corsOptions.setAllowCredentials(true);
            corsOptions.setHeaders(new String[]{"GET", "POST"});
        }
        app.use(Middleware.cors());
        app.use((req, res) -> logger.log(String.format("%s сделал запрос на %s", req.getIp(), req.getPath())));
        app.all("/", (req, res) -> {
            String location = String.format("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=%1$s&redirect_uri=%2$s",
                    uriEncode(config.getString("oauth.client_id", "n0wrmdd14dpmcmlkfwmtgel5bu1ev5")),
                    uriEncode(config.getString("oauth.redirect_uri", "http://localhost:11430/auth")));
            res.redirect(location);
        });
        app.all("/avatar/:id", (req, res) -> res.redirect(getURI("avatar/"+req.getParam("id"),false)));
        app.all("/auth", (req, res) -> {
            if (req.getContentType().toLowerCase().contains("application/json") || (req.getQuery("json") != null && req.getQuery("json").equalsIgnoreCase("true")) || htmlAuth == null) {
                if (req.getQuery("code").isEmpty()) {
                    res.setStatus(Status._401);
                    res.json(Objects.UNAUTHORIZED);
                    return;
                }
                try {
                    JsonObject jsonObject = WebUtils.getJsonObject(getURI("login", false)+"?code="+req.getQuery("code")+"&mc_uuid="+ AlinLib.MINECRAFT.getUser().getProfileId() +"&json=true"+"&ruri="+uriEncode(config.getString("oauth.redirect_uri", "http://127.0.0.1:11430/auth")));
                    if(jsonObject.has("error"))
                        res.setStatus(parseInt(getStringInJSON("error.code", jsonObject, "500")));
                    else {
                        config.setString("oauth.access_token", getStringInJSON("access", jsonObject, ""));
                        PepeLandHelper.loadUser(true);
                    }
                    res.json(jsonObject);
                } catch (Exception e) {
                    res.setStatus(500);
                    res.json(getErrorObject(e));
                }

            } else {
                res.setContentType(MediaType._html);
                res.send(htmlAuth);
            }
        });

        // ---> Player

        // ---> Not found
        app.all((req, res) -> {
            res.setStatus(404);
            res.json(Objects.NOT_FOUND);
        });
        app.listen(parseInt(config.getString("oauth.port", "11430")));
        logger.log("API Started");
        logger.log("Open: http://localhost:%s", parseInt(config.getString("oauth.port", "11430")));
        state = true;
    }

    public static void stop() {
        if (state) {
            app.stop();
            logger.log("API Stopped");
        } else logger.warn("API not running");
    }

    public static JsonObject getErrorObject(Exception ex){
        JsonObject object = Objects.INTERNAL_SERVER_ERROR;
        object.get("error").getAsJsonObject().addProperty("message", ex.getMessage() == null ? ex.getClass().toString() : ex.getMessage());
        return object;
    }

    public static User getUser(String token){
        try{
            JsonObject object = WebUtils.getJsonObject(HttpRequest.newBuilder(URI.create(getURI("user/me", false))).header("Authorization", "Bearer "+  token));
            PepeLandHelper.LOG.log(object.toString());
            if(object.has("error")) return null;
            else return new User(object);
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    public static User getUserByID(String id){
        try {
            JsonObject object = WebUtils.getJsonObject(getURI(String.format("user/%s", id), false));
            if(object.has("error")) return null;
            return new User(object);
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public interface Objects {
        JsonObject NOT_FOUND = GsonHelper.parse("{\"error\":{\"code\":404,\"codename\":\"Not found\",\"message\":\"Method not found\"}}");
        JsonObject INTERNAL_SERVER_ERROR = GsonHelper.parse("{\"error\":{\"code\":500,\"codename\":\"Internal Server Error\",\"message\":\"\"}}");
        JsonObject UNAUTHORIZED = GsonHelper.parse("{\"error\": {\"code\": 401,\"codename\": \"Unauthorized\",\"message\": \"You not authorized\"}}");
        JsonObject BAD_REQUEST = GsonHelper.parse("{\"error\": {\"code\": 400,\"codename\": \"Bad Request\",\"message\": \"The required arguments are missing!\"}}");
    }
}
