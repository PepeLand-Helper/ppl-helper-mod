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
    // -=-=-=- Проекты -=-=-=-
    public boolean EDIT_PROJECTS;
    public boolean CREATE_PROJECTS;
    public boolean DELETE_PROJECTS;
    public Role(JsonObject data){
        this.name = getStringInJSON("name", data, "user");
        this.title = getStringInJSON("title", data, "Пользователь");
        if(data.has("permissions")){
            JsonObject permissions = data.getAsJsonObject("permissions");
            EDIT_NEWS = getBooleanInJSON("edit_news", permissions, false);
            CREATE_NEWS = getBooleanInJSON("create_news", permissions, false);
            DELETE_NEWS = getBooleanInJSON("delete_news", permissions, false);

            EDIT_PROJECTS = getBooleanInJSON("edit_projects", permissions, false);
            CREATE_PROJECTS = getBooleanInJSON("create_projects", permissions, true);
            DELETE_PROJECTS = getBooleanInJSON("delete_projects", permissions, false);
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

        perms.addProperty("edit_projects", EDIT_PROJECTS);
        perms.addProperty("create_projects", CREATE_PROJECTS);
        perms.addProperty("delete_projects", DELETE_PROJECTS);
        // -=-=-=-
        json.add("permissions", perms);
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
