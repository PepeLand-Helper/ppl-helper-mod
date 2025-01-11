package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import static ru.kelcuprum.pplhelper.utils.JsonHelper.getStringInJSON;

public class News {
    public int id;
    public String title;
    public String description;
    public String author;

    public String banner;
    public String icon;

    public News(JsonObject info){
        id = info.get("id").getAsInt();

        title = getStringInJSON("data.title", info, "");
        description = getStringInJSON("data.description", info, "");
        author = getStringInJSON("data.author", info);

        banner = getStringInJSON("data.banner", info);
        icon = getStringInJSON("data.icon", info);
    }
    public String getContent(){
        return PepeLandHelperAPI.getNewsContent(this.id);
    }
}
