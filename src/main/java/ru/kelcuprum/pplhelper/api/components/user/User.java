package ru.kelcuprum.pplhelper.api.components.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.kelcuprum.alinlib.WebAPI;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.api.components.News;
import ru.kelcuprum.pplhelper.api.components.Project;

import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.pplhelper.api.PepeLandHelperAPI.getURI;

public class User {
    public String nickname;
    public String username;
    public String id;
    public String avatar;
    public Role role = new Role(new JsonObject());
    public Studio studio = new Studio(new JsonObject());
    public User(JsonObject object){
        if(object.has("nickname")) nickname = object.get("nickname").getAsString();
        if(object.has("username")) username = object.get("username").getAsString();
        if(object.has("id")) id = object.get("id").getAsString();
        if(object.has("avatar")) avatar = object.get("avatar").getAsString();
        if(object.has("role")) role = new Role(object.getAsJsonObject("role"));
        if(object.has("studio")) studio = new Studio(object.getAsJsonObject("studio"));
    }

    public List<Project> getProjects(){
        try {
            JsonObject projects = WebAPI.getJsonObject(getURI("projects?id="+id, false));
            List<Project> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("items")) list.add(new Project(element.getAsJsonObject()));
            return list;
        } catch (Exception ex){
            PepeLandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<News> getNews(){
        try {
            JsonObject projects = WebAPI.getJsonObject(getURI("news?id="+id, false));
            List<News> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("items")) list.add(new News(element.getAsJsonObject()));
            return list;
        } catch (Exception ex){
            PepeLandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new ArrayList<>();
        }
    }
}
