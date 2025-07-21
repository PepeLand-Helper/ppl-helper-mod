package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import ru.kelcuprum.pplhelper.api.OAuth;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.user.User;

import static ru.kelcuprum.alinlib.utils.GsonHelper.getStringInJSON;

public class News {
    public int id;
    public String title;
    public String description;
    public String author;
    public String studio;

    public String banner;
    public String icon;

    public News(JsonObject info, boolean isSearch){
        id = info.get("id").getAsInt();

        title = getStringInJSON("data.title", info, "");
        description = getStringInJSON("data.description", info, "");
        author = getStringInJSON(isSearch ? "data.author.id" : "data.author", info);
        studio = getStringInJSON("data.studio", info);

        banner = getStringInJSON("data.banner", info);
        icon = getStringInJSON("data.icon", info);
    }
    public String getContent(){
        return PepeLandHelperAPI.getNewsContent(this.id);
    }

    public User getAuthor(){
        return OAuth.getUserByID(author);
    }
}
