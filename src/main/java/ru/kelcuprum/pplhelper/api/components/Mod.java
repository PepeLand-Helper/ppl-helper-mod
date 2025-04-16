package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import ru.kelcuprum.pplhelper.utils.WebUtils;
import ru.kelcuprum.pplhelper.PepeLandHelper;

import java.util.HashMap;

import static ru.kelcuprum.alinlib.utils.GsonHelper.getStringInJSON;
import static ru.kelcuprum.alinlib.utils.GsonHelper.jsonElementIsNull;

public class Mod {
    public String modid;
    public String id;
    public Service service;
    public String title;
    public String description;
    public String url;
    public String icon;
    public Mod(JsonObject info){
        modid = getStringInJSON("modid", info);
        id = getStringInJSON("id", info);
        service = Service.getServiceByID(getStringInJSON("service", info, ""));
        icon = getStringInJSON("icon", info, "");
        title = getStringInJSON("title", info, "");
        description = getStringInJSON("description", info, "");
        if(!jsonElementIsNull("url", info)) url = getStringInJSON("url", info, "");
        else this.url = Service.getServiceURL(service, id);
        new Thread(() -> {
            switch (service){
                case MODRINTH -> loadModrinthInfo();
                case GITHUB -> loadGitHubInfo();
            }
        }).start();
    }

    public static HashMap<String, JsonObject> cache = new HashMap<>();
    public void loadGitHubInfo(){
        try{
            String url = String.format("https://api.github.com/repos/%s", id);
            JsonObject jsonObject = cache.containsKey(url) ? cache.get(url) : WebUtils.getJsonObject(url);
            if(this.title.isBlank()) this.title = getStringInJSON("name", jsonObject, "");
            if(this.description.isBlank()) this.description = getStringInJSON("description", jsonObject, "");
            cache.put(url, jsonObject);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void loadModrinthInfo(){
        try{
            String url = String.format("https://api.modrinth.com/v2/project/%s", id);
            JsonObject jsonObject = cache.containsKey(url) ? cache.get(url) : WebUtils.getJsonObject(url);
            if(this.title.isBlank()) this.title = getStringInJSON("title", jsonObject, "");
            if(this.description.isBlank()) this.description = getStringInJSON("description", jsonObject, "");
            if(this.icon.isBlank()) this.icon = getStringInJSON("icon_url", jsonObject, "");
            cache.put(url, jsonObject);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // -=-=-=-=-=-=-

    public enum Service{
        MODRINTH("Modrinth"),
        GITHUB("GitHub"),
        INTERNET("Internet");
        public final String name;
        public final String iconChar;
        Service(String name){
            this(name, "\uD999");
        }
        Service(String name, String iconChar){
            this.name = name;
            this.iconChar = iconChar;
        }
        static Service getServiceByID(String id){
            return switch (id){
                case "modrinth" -> MODRINTH;
                case "github" -> GITHUB;
                default -> INTERNET;
            };
        }
        static String getServiceURL(Service service, String id){
            return switch (service){
                case MODRINTH -> String.format("%sproject/%s", PepeLandHelper.config.getString("MODRINTH_URL", "https://modrinth.com/"), id);
                case GITHUB -> String.format("https://github.com/%s", id);
                default -> id;
            };
        }
    }
}
