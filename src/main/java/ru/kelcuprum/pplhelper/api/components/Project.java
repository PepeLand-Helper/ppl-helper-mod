package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;

import static org.apache.logging.log4j.Level.ERROR;

public class Project {
    public int id;
    public String title;
    public String description;
    public String creators;
    public String author;

    public String icon;
    public String banner;

    public String world;
    public String coordinates$overworld;
    public String coordinates$nether;
    public String coordinates$end;

    public String content;
    public Project(JsonObject info){
        id = info.get("id").getAsInt();

        if(!info.getAsJsonObject("data").get("title").isJsonNull()) title = info.getAsJsonObject("data").get("title").getAsString();
        if(!info.getAsJsonObject("data").get("description").isJsonNull()) description = info.getAsJsonObject("data").get("description").getAsString();
        if(!info.getAsJsonObject("data").get("creators").isJsonNull()) creators = info.getAsJsonObject("data").get("creators").getAsString();
        if(!info.getAsJsonObject("data").get("author").isJsonNull()) author = info.getAsJsonObject("data").get("author").getAsString();

        if(!info.getAsJsonObject("data").get("icon").isJsonNull()) icon = info.getAsJsonObject("data").get("icon").getAsString();
        if(!info.getAsJsonObject("data").get("banner").isJsonNull()) banner = info.getAsJsonObject("data").get("banner").getAsString();
        if(!info.getAsJsonObject("data").get("coordinates").isJsonNull()){
            if(!info.getAsJsonObject("data").get("coordinates").getAsJsonObject().get("world").isJsonNull()) world = info.getAsJsonObject("data").get("coordinates").getAsJsonObject().get("world").getAsString();
            if(!info.getAsJsonObject("data").get("coordinates").getAsJsonObject().get("overworld").isJsonNull()) coordinates$overworld = info.getAsJsonObject("data").get("coordinates").getAsJsonObject().get("overworld").getAsString();
            if(!info.getAsJsonObject("data").get("coordinates").getAsJsonObject().get("nether").isJsonNull()) coordinates$nether = info.getAsJsonObject("data").get("coordinates").getAsJsonObject().get("nether").getAsString();
            if(!info.getAsJsonObject("data").get("coordinates").getAsJsonObject().get("end").isJsonNull()) coordinates$end = info.getAsJsonObject("data").get("coordinates").getAsJsonObject().get("end").getAsString();
        }
        try {
            this.content = PepeLandHelperAPI.getProjectContent(this.id);
        } catch (Exception ex){
            PepelandHelper.log(ex.getMessage(), ERROR);
        }
    }
}
