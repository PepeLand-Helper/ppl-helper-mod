package ru.kelcuprum.pplhelper.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.WebAPI;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.TabHelper;
import ru.kelcuprum.pplhelper.api.components.News;
import ru.kelcuprum.pplhelper.api.components.Project;

import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.pplhelper.api.PepeLandAPI.uriEncode;

public class PepeLandHelperAPI {
    public static String getURI(String url){
        return getURI(url, true);
    }
    public static String getURI(String url, boolean uriEncode){
        String api = PepelandHelper.config.getString("API_URL", "https://api-h.pplmods.ru/");
        if(!api.endsWith("/")) api+="/";
        return String.format("%1$s%2$s", api, uriEncode ? uriEncode(url) : url);
    }


    public static JsonArray getRecommendMods(){
        try {
            return WebAPI.getJsonArray(getURI("mods"));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }public static JsonArray getRecommendPacks(){
        try {
            return WebAPI.getJsonArray(getURI("resourcepacks"));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static JsonArray getCommands(){
        try {
            return WebAPI.getJsonArray(getURI("commands"));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static String[] getWorlds(){
        try {
            JsonArray array = WebAPI.getJsonArray(getURI("worlds"));
            String[] worlds = new String[array.size()+1];
            worlds[0] = Component.translatable("pplhelper.project.world.all").getString();
            for(int i = 1; i<array.size()+1;i++) worlds[i] = array.get(i-1).getAsString();
            return worlds;
        } catch (Exception ex){
            return new String[0];
        }
    }

    public static List<Project> getProjects(String query, String world){
        try {
            JsonObject projects = WebAPI.getJsonObject(getURI("projects?query="+uriEncode(query)+(world.equalsIgnoreCase(Component.translatable("pplhelper.project.world.all").getString()) ? "" : "&world="+uriEncode(world)), false));
            List<Project> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("items")) list.add(new Project(element.getAsJsonObject()));
            return list;
        } catch (Exception ex){
            PepelandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new ArrayList<>();
        }
    }

    public static JsonObject getProject(int id){
        try {
            return WebAPI.getJsonObject(getURI(String.format("projects/%s", id), false));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static String getProjectContent(int id){
        try {
            return WebAPI.getString(getURI(String.format("projects/%s/content", id), false));
        } catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }

    public static List<News> getNews(String search){
        try {
            JsonObject projects = WebAPI.getJsonObject(getURI("news?query="+uriEncode(search), false));
            List<News> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("items")) list.add(new News(element.getAsJsonObject()));
            return list;
        } catch (Exception ex){
            PepelandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new ArrayList<>();
        }
    }
    public static String getNewsContent(int id){
        try {
            return WebAPI.getString(getURI(String.format("news/%s/content", id), false));
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
