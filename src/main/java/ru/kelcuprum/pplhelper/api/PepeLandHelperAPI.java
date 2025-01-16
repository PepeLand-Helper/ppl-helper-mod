package ru.kelcuprum.pplhelper.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.WebAPI;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.News;
import ru.kelcuprum.pplhelper.api.components.Project;

import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.pplhelper.api.PepeLandAPI.uriEncode;
import static ru.kelcuprum.pplhelper.utils.JsonHelper.getStringInJSON;
import static ru.kelcuprum.pplhelper.utils.JsonHelper.hasJSONElement;

public class PepeLandHelperAPI {
    public static String getURI(String url){
        return getURI(url, true);
    }
    public static String getURI(String url, boolean uriEncode){
        String api = PepelandHelper.config.getString("API_URL", "https://api-h.pplmods.ru/");
        if(!api.endsWith("/")) api+="/";
        return String.format("%1$s%2$s", api, uriEncode ? uriEncode(url) : url);
    }

    public static boolean apiAvailable(){
        try {
            JsonObject content = WebAPI.getJsonObject(getURI("ping", false));
            if(content.has("error")) throw new Exception(getStringInJSON("error.message", content));
            return content.has("message") && content.has("time");
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public static JsonArray getRecommendMods(){
        try {
            return WebAPI.getJsonArray(getURI("mods"));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static JsonArray getRecommendPacks(){
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
            return new String[]{
                    Component.translatable("pplhelper.project.world.all").getString(),
                    "МП1",
                    "МП2",
                    "МР",
                    "МФ",
                    "ТЗ",
                    "Энд"
            };
        }
    }

    public static String[] getProjectCategories(){
        try {
            JsonArray array = WebAPI.getJsonObject(getURI("category")).getAsJsonArray("projects");
            String[] categories = new String[array.size()+1];
            categories[0] = Component.translatable("pplhelper.project.world.all").getString();
            int size = 1;
            for(JsonElement data : array) {
                categories[size] = !hasJSONElement("translatable", (JsonObject) data) ? getStringInJSON("name", (JsonObject) data, "")
                        : Component.translatable(getStringInJSON("name", (JsonObject) data, "")).getString();
                size++;
            }
            return categories;
        } catch (Exception ex){
            ex.printStackTrace();
            return new String[]{
                    Component.translatable("pplhelper.project.world.all").getString(),
            };
        }
    }
    public static String[] getProjectCategoriesTags(){
        try {
            JsonArray array = WebAPI.getJsonObject(getURI("category")).getAsJsonArray("projects");
            String[] categories = new String[array.size()+1];
            categories[0] = "";
            int size = 1;
            for(JsonElement data : array) {
                categories[size] =  Component.translatable(getStringInJSON("tag", (JsonObject) data, "")).getString();
                size++;
            }
            return categories;
        } catch (Exception ex){
            return new String[]{
                    "",
            };
        }
    }
    // -=-=-=-

    public static String[] getNewsCategories(){
        try {
            JsonArray array = WebAPI.getJsonObject(getURI("category")).getAsJsonArray("news");
            String[] categories = new String[array.size()+1];
            categories[0] = Component.translatable("pplhelper.project.world.all").getString();
            int size = 1;
            for(JsonElement data : array) {
                categories[size] = !hasJSONElement("translatable", (JsonObject) data) ? getStringInJSON("name", (JsonObject) data, "")
                        : Component.translatable(getStringInJSON("name", (JsonObject) data, "")).getString();
                size++;
            }
            return categories;
        } catch (Exception ex){
            return new String[]{
                    Component.translatable("pplhelper.project.world.all").getString(),
            };
        }
    }
    public static String[] getNewsCategoriesTags(){
        try {
            JsonArray array = WebAPI.getJsonObject(getURI("category")).getAsJsonArray("news");
            String[] categories = new String[array.size()+1];
            categories[0] = "";
            int size = 1;
            for(JsonElement data : array) {
                categories[size] = getStringInJSON("tag", (JsonObject) data, "");
                size++;
            }
            return categories;
        } catch (Exception ex){
            return new String[]{
                    ""
            };
        }
    }
    // -=-=-=-

    public static List<Project> getProjects(String query, String world, String category){
        try {
            JsonObject projects = WebAPI.getJsonObject(getURI("projects?query="+uriEncode(query)+
                    (world.equalsIgnoreCase(Component.translatable("pplhelper.project.world.all").getString()) ? "" : "&world="+uriEncode(world))+
                    (category.isEmpty() ? "" : "&category="+uriEncode(category)), false));
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

    public static List<News> getNews(String search, String category){
        try {
            JsonObject projects = WebAPI.getJsonObject(getURI("news?query="+uriEncode(search)+
            (category.isEmpty() ? "" : "&category="+uriEncode(category)), false));
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
