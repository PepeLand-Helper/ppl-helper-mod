package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;

import static org.apache.logging.log4j.Level.ERROR;

public class News {
    public int id;
    public String title;
    public String description;
    public String author;

    public String banner;
    public String icon;

    public String content;
    public News(JsonObject info){
        id = info.get("id").getAsInt();

        if(info.getAsJsonObject("data").has("title")) title = info.getAsJsonObject("data").get("title").getAsString();
        if(info.getAsJsonObject("data").has("description")) description = info.getAsJsonObject("data").get("description").getAsString();
        if(info.getAsJsonObject("data").has("author")) author = info.getAsJsonObject("data").get("author").getAsString();

        if(info.getAsJsonObject("data").has("banner")) banner = info.getAsJsonObject("data").get("banner").getAsString();
        if(info.getAsJsonObject("data").has("icon")) icon = info.getAsJsonObject("data").get("icon").getAsString();

        try {
            this.content = PepeLandHelperAPI.getNewsContent(this.id);
        } catch (Exception ex){
            PepelandHelper.LOG.log(ex.getMessage(), ERROR);
        }
    }
}
