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

    public News(JsonObject info){
        id = info.get("id").getAsInt();

        title = Project.getStringInJSON("data.title", info);
        description = Project.getStringInJSON("data.description", info);
        author = Project.getStringInJSON("data.author", info);

        banner = Project.getStringInJSON("data.banner", info);
        icon = Project.getStringInJSON("data.icon", info);
    }
    public String getContent(){
        return PepeLandHelperAPI.getNewsContent(this.id);
    }
}
