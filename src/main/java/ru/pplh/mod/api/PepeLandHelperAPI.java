package ru.pplh.mod.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.utils.WebUtils;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.api.components.News;
import ru.pplh.mod.api.components.SearchResult;
import ru.pplh.mod.api.components.project.Page;
import ru.pplh.mod.api.components.project.Project;
import ru.pplh.mod.api.components.VersionInfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.alinlib.utils.GsonHelper.jsonElementIsNull;
import static ru.pplh.mod.api.PepeLandAPI.downloadFile;
import static ru.pplh.mod.api.PepeLandAPI.uriEncode;
import static ru.kelcuprum.alinlib.utils.GsonHelper.getStringInJSON;

public class PepeLandHelperAPI {
    public static String getURI(String url){
        return getURI(url, true);
    }
    public static String getURI(String url, boolean uriEncode){
        String api = PepeLandHelper.config.getString("API_URL", "https://api.pplh.ru/");
        if(!api.endsWith("/")) api+="/";
        return String.format("%1$s%2$s", api, uriEncode ? uriEncode(url) : url);
    }
    public static long lastCheckAPI = 0;
    public static boolean lastApiAvailable = false;
    public static boolean apiAvailable(){
        if(System.currentTimeMillis() - lastCheckAPI <= 1125){
            return lastApiAvailable;
        } else {
            lastCheckAPI = System.currentTimeMillis();
            if (PepeLandHelper.config.getBoolean("OFFLINE_MODE", false)) {
                lastApiAvailable = false;
            }
            try {
                HttpClient httpClient = new DefaultHttpClient();
                final HttpParams httpParams = httpClient.getParams();

                HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                HttpConnectionParams.setSoTimeout(httpParams, 5000);

                final HttpGet httpget = new HttpGet(getURI("ping"));
                final HttpResponse response = httpClient.execute(httpget);
                final HttpEntity entity = response.getEntity();
                JsonObject content = GsonHelper.parse(new String(entity.getContent().readAllBytes()));
                lastApiAvailable = content.has("message") && content.has("time");
            } catch (IOException ex) {
                PepeLandHelper.LOG.error("Роскомнадзор дошёл и до хелпера... пупупу...");
                lastApiAvailable = false;
            } catch (Exception ex) {
                lastApiAvailable = false;
            }
            return lastApiAvailable;
        }
    }

    public static Component getMessageFromBreakAPI(){
        if(PepeLandHelper.config.getBoolean("OFFLINE_MODE", false)){
            return Component.translatable("pplhelper.api.offline_mode");
        }
        try {
            JsonObject content = WebUtils.getJsonObject(getURI("ping", false));
            if(content.has("error")) throw new Exception(getStringInJSON("error.message", content));
            return Component.empty();
        } catch (Exception ex){
            PepeLandHelper.LOG.error("API недоступен");
            return ex.getMessage() == null ? Component.translatable("pplhelper.api.unavailable") : Component.translatable("pplhelper.api.error", ex.getMessage());
        }
    }

    public static JsonObject getCommonInformation(){
        try {
            return WebUtils.getJsonObject(getURI("common_info", false));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    // -=-=-=-
    public static VersionInfo getAutoUpdate(boolean followTwoDotZero){
        String ver = FabricLoader.getInstance().getModContainer("pplhelper").get().getMetadata().getVersion().getFriendlyString();
        String minecraft = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString();
        if(ver.contains("+")) ver = ver.split("\\+")[0];
        try {
            JsonObject jsonObject = WebUtils.getJsonObject(getURI("versions?version="+uriEncode(ver)+"&allow_two="+followTwoDotZero+"&mc="+minecraft, false));
            if(isError(jsonObject)) throw new RuntimeException(jsonObject.getAsJsonObject("error").get("message").getAsString());
            return new VersionInfo(jsonObject);
        } catch (Exception ex){
            ex.printStackTrace();
            return new VersionInfo(ver);
        }
    }
    // -=-=-=-

    public static SearchResult getProjects(String query, String world, String category, int page){
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(getURI("projects?query="+uriEncode(query)+
                    "&page="+page+
                    (world.equalsIgnoreCase(Component.translatable("pplhelper.project.world.all").getString()) ? "" : "&world="+uriEncode(world))+
                    (category.isEmpty() ? "" : "&category="+uriEncode(category)), false)));
            if(PepeLandHelper.user != null) builder.header("Authorization", "Bearer "+ PepeLandHelper.config.getString("oauth.access_token", ""));
            JsonObject projects = WebUtils.getJsonObject(builder);
            ArrayList<Project> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("page")) list.add(new Project(element.getAsJsonObject(), true));
            return new SearchResult(list, projects.getAsJsonObject("info").get("max_pages").getAsInt());
        } catch (Exception ex){
            PepeLandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new SearchResult(new ArrayList(), 1);
        }
    }

    public static JsonObject getProject(int id){
        try {
            return WebUtils.getJsonObject(getURI(String.format("projects/%s", id), false));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static JsonObject getProjectInteractive(int id){
        try {
            JsonObject jsonObject = WebUtils.getJsonObject(getURI(String.format("projects/%s/interactive", id), false));
            if(isError(jsonObject)) throw getError(jsonObject);
            else return jsonObject;
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static String getProjectContent(int id){
        try {
            return WebUtils.getString(getURI(String.format("projects/%s/content", id), false));
        } catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }
    public static String getProjectPageContent(int id, String pageID){
        try {
            return WebUtils.getString(getURI(String.format("projects/%s/pages/%s", id, pageID), false));
        } catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }
    public static Page[] getProjectPages(int id){
        try {
            JsonObject jsonObject = WebUtils.getJsonObject(getURI(String.format("projects/%s/pages", id), false));
            if(isError(jsonObject)) throw new RuntimeException(getError(jsonObject));
            Page[] pages = new Page[jsonObject.getAsJsonObject("pages").keySet().size()];
            int i = 0;
            for(String pageID : jsonObject.getAsJsonObject("pages").keySet()){
                pages[i] = new Page(id, pageID, jsonObject.getAsJsonObject("pages").getAsJsonObject(pageID).get("name").getAsString());
                i++;
            }
            return pages;
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static void uploadProjectSchematicFile(byte[] file, int id) throws IOException, InterruptedException {
        if(PepeLandHelper.user == null) return;
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(getURI(String.format("projects/%s?schematic=true", id))));
        builder.header("Authorization", "Bearer "+ PepeLandHelper.config.getString("oauth.access_token", ""));
        builder.POST(HttpRequest.BodyPublishers.ofString("hehehe"));
        JsonObject object = net.minecraft.util.GsonHelper.parse(WebUtils.getString(builder));

        if(isError(object)) throw new RuntimeException(object.getAsJsonObject("error").get("message").getAsString());
        else PepeLandHelper.LOG.log(object.get("message").getAsString());
    }
    public static void updateProject(Project project) throws IOException, InterruptedException {
        if(PepeLandHelper.user == null) return;
        JsonObject data = new JsonObject();
        data.add("data", project.toJSON());
        data.addProperty("content", getProjectContent(project.id));
        JsonObject object = WebUtils.getJsonObject(HttpRequest.newBuilder(URI.create(getURI(String.format("projects/%s", project.id))))
                .header("Authorization", "Bearer "+ PepeLandHelper.config.getString("oauth.access_token", ""))
                .POST(HttpRequest.BodyPublishers.ofString(data.toString())));
        if(isError(object)) throw new RuntimeException(object.getAsJsonObject("error").get("message").getAsString());
        else PepeLandHelper.LOG.log(object.get("message").getAsString());
    }
    public static void downlaodProjectSchematic(int id){
        try {
            downloadFile(getURI(String.format("projects/%s/schematic", id), false), AlinLib.MINECRAFT.gameDirectory.toPath() + "/schematics", String.format("pplhelper-%s.litematic", id));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    // -=-=-=-
    public static SearchResult getArchivedProjects(String query, String season, String category, int page){
        try {
            JsonObject projects = WebUtils.getJsonObject(getURI("archive/"+season+"?query="+uriEncode(query)+
                    "&page="+page+"&page_size=20"+
                    (category.isEmpty() ? "" : "&category="+uriEncode(category)), false));
            ArrayList<Project> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("page")) list.add(new Project(element.getAsJsonObject(), true));
            return new SearchResult(list, projects.getAsJsonObject("info").get("max_pages").getAsInt());
        } catch (Exception ex){
            PepeLandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new SearchResult(new ArrayList(), 1);
        }
    }
    public static String getArchivedProjectContent(int id, String season){
        try {
            return WebUtils.getString(getURI(String.format("archive/%s/%s/content", season, id), false));
        } catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }
    // -=-=-=-
    public static SearchResult getNews(String search, String category){
        try {
            JsonObject projects = WebUtils.getJsonObject(getURI("news?query="+uriEncode(search)+
            (category.isEmpty() ? "" : "&category="+uriEncode(category)), false));
            List<News> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("page")) list.add(new News(element.getAsJsonObject(), true));
            return new SearchResult(list, projects.getAsJsonObject("info").get("max_pages").getAsInt());
        } catch (Exception ex){
            PepeLandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new SearchResult(new ArrayList(), 1);
        }
    }
    public static String getNewsContent(int id){
        try {
            return WebUtils.getString(getURI(String.format("news/%s/content", id), false));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static boolean isError(JsonElement json){
        return json.isJsonObject() && json.getAsJsonObject().has("error");
    }

    public static Exception getError(JsonElement json){
        JsonObject jsonObject = json.getAsJsonObject().getAsJsonObject("error");
        return new Exception(jsonObject.has("message") && !jsonObject.get("message").getAsString().isBlank() ? jsonObject.get("message").getAsString() : jsonObject.get("codename").getAsNumber().intValue() + " "+ jsonObject.get("codename").getAsString());
    }
}
