package ru.kelcuprum.pplhelper.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.kelcuprum.alinlib.WebAPI;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.Project;

import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.pplhelper.api.PepeLandAPI.uriEncode;

public class PepeLandHelperAPI {
    public static String API_URL =
            PepelandHelper.config.getString("API_URL", "https://api-h.pplmods.ru/");
    //"http://localhost:6955/";
    public static String getURI(String url){
        return getURI(url, true);
    }
    public static String getURI(String url, boolean uriEncode){
        return String.format("%1$s%2$s", API_URL, uriEncode ? uriEncode(url) : url);
    }


    public static JsonArray getRecommendMods(){
        try {
            JsonArray array = WebAPI.getJsonArray(getURI("mods.json"));
            return array;
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static List<Project> getProjects(){
        try {
            JsonObject projects = WebAPI.getJsonObject(getURI("projects"));
            List<Project> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("items")) list.add(new Project(element.getAsJsonObject()));
            return list;
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static String getProjectContent(int id){
        try {
            return WebAPI.getString(getURI(String.format("projects/%s/content", id), false));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static JsonArray getCommands(){
        try {
            JsonArray array = WebAPI.getJsonArray(getURI("commands.json"));
            return array;
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
