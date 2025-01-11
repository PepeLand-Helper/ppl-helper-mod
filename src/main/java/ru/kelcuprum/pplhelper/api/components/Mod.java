package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import ru.kelcuprum.alinlib.WebAPI;
import ru.kelcuprum.pplhelper.PepelandHelper;

import java.util.HashMap;

public class Mod {
    public String modid;
    public String id;
    public Service service;
    public String title;
    public String description;
    public String url;
    public String icon;
    public Mod(JsonObject info){
        modid = Project.getStringInJSON("modid", info);
        id = Project.getStringInJSON("id", info);
        service = Service.getServiceByID(Project.getStringInJSON("service", info, ""));
        icon = Project.getStringInJSON("icon", info, "");
        title = Project.getStringInJSON("title", info, "");
        description = Project.getStringInJSON("description", info, "");
        if(Project.hasJSONElement("url", info)) url = Project.getStringInJSON("url", info, "");
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
            JsonObject jsonObject = cache.containsKey(url) ? cache.get(url) : WebAPI.getJsonObject(url);
            if(this.title.isBlank()) this.title = Project.getStringInJSON("name", jsonObject, "");
            if(this.description.isBlank()) this.description = Project.getStringInJSON("description", jsonObject, "");
            cache.put(url, jsonObject);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void loadModrinthInfo(){
        try{
            String url = String.format("https://api.modrinth.com/v2/project/%s", id);
            JsonObject jsonObject = cache.containsKey(url) ? cache.get(url) : WebAPI.getJsonObject(url);
            if(this.title.isBlank()) this.title = Project.getStringInJSON("title", jsonObject, "");
            if(this.description.isBlank()) this.description = Project.getStringInJSON("description", jsonObject, "");
            if(this.icon.isBlank()) this.icon = Project.getStringInJSON("icon_url", jsonObject, "");
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
                case MODRINTH -> String.format("%sproject/%s", PepelandHelper.config.getString("MODRINTH_URL", "https://modrinth.com/"), id);
                case GITHUB -> String.format("https://github.com/%s", id);
                default -> id;
            };
        }
    }
}
