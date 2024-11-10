package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;

public class Mod {
    public String modid;
    public String title;
    public String description;
    public String url;
    public String icon;
    public Mod(JsonObject info){
        modid = info.get("modid").getAsString();
        title = info.get("title").getAsString();
        description = info.get("description").getAsString();
        url = info.get("url").getAsString();
        icon = info.get("icon").getAsString();
    }
}
