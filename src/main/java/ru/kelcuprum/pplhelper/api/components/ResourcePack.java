package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import ru.kelcuprum.alinlib.WebAPI;
import ru.kelcuprum.pplhelper.PepelandHelper;

import java.util.HashMap;

public class ResourcePack {
    public String id;
    public Service service;
    public String title = "";
    public String description = "";
    public String url = "";
    public String icon = "";
    public ResourcePack(JsonObject info){
        id = info.get("id").getAsString();
        service = Service.getServiceByID(info.get("service").getAsString());
        if(info.has("icon")) icon = info.get("icon").getAsString();
        if(info.has("title")) title = info.get("title").getAsString();
        if(info.has("description")) description = info.get("description").getAsString();
        if(info.has("url")) url = info.get("url").getAsString();
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
            if(this.title.isEmpty()) this.title = jsonObject.get("name").getAsString();
            if(this.description.isEmpty()) this.description = jsonObject.get("description").getAsString();
            cache.put(url, jsonObject);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void loadModrinthInfo(){
        try{
            String url = String.format("https://api.modrinth.com/v2/project/%s", id);
            JsonObject jsonObject = cache.containsKey(url) ? cache.get(url) : WebAPI.getJsonObject(url);
            if(this.title.isEmpty()) this.title = jsonObject.get("title").getAsString();
            if(this.description.isEmpty()) this.description = jsonObject.get("description").getAsString();
            if(this.icon.isEmpty()) this.icon = jsonObject.get("icon_url").getAsString();
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
