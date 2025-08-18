package ru.pplh.mod.api.components.user;

import com.google.gson.JsonObject;

import static ru.kelcuprum.alinlib.utils.GsonHelper.getBooleanInJSON;
import static ru.kelcuprum.alinlib.utils.GsonHelper.getStringInJSON;

public class Role {
    public String name;
    public String title;
    // -=-=-=- Новости -=-=-=-
    public boolean EDIT_NEWS;
    public boolean CREATE_NEWS;
    public boolean DELETE_NEWS;
    public boolean DELETE_NEWS_COMMENT;
    // -=-=-=- Проекты -=-=-=-
    public boolean EDIT_PROJECTS;
    public boolean CREATE_PROJECTS;
    public boolean DELETE_PROJECTS;
    public boolean VIEW_PROJECTS;
    // -=-=-=- 1-e Апреля -=-=-=-
    public boolean TESTING_APRIL_FOOL;

    public Role(JsonObject data){
        this.name = getStringInJSON("name", data, "user");
        this.title = getStringInJSON("title", data, "Пользователь");
        if(data.has("permissions")){
            JsonObject permissions = data.getAsJsonObject("permissions");
            EDIT_NEWS = getBooleanInJSON("edit_news", permissions, false);
            CREATE_NEWS = getBooleanInJSON("create_news", permissions, false);
            DELETE_NEWS = getBooleanInJSON("delete_news", permissions, false);
            DELETE_NEWS_COMMENT = getBooleanInJSON("delete_news_comment", permissions, false);

            EDIT_PROJECTS = getBooleanInJSON("edit_projects", permissions, false);
            CREATE_PROJECTS = getBooleanInJSON("create_projects", permissions, true);
            DELETE_PROJECTS = getBooleanInJSON("delete_projects", permissions, false);
            VIEW_PROJECTS = getBooleanInJSON("view_projects", permissions, true);

            TESTING_APRIL_FOOL = getBooleanInJSON("testing_april_fool", permissions, false);
        }
    }
    // -=-=-=- Сайт -=-=-=-
    public JsonObject toJSON(){
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("title", title);
        // -=-=-=-
        JsonObject perms = new JsonObject();
        perms.addProperty("edit_news", EDIT_NEWS);
        perms.addProperty("create_news", CREATE_NEWS);
        perms.addProperty("delete_news", DELETE_NEWS);
        perms.addProperty("delete_news_comment", DELETE_NEWS_COMMENT);

        perms.addProperty("edit_projects", EDIT_PROJECTS);
        perms.addProperty("create_projects", CREATE_PROJECTS);
        perms.addProperty("delete_projects", DELETE_PROJECTS);
        perms.addProperty("view_projects", VIEW_PROJECTS);

        perms.addProperty("testing_april_fool", TESTING_APRIL_FOOL);
        // -=-=-=-
        json.add("permissions", perms);
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
