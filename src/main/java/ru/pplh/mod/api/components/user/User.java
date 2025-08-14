package ru.pplh.mod.api.components.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.pplh.mod.utils.WebUtils;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.api.components.News;
import ru.pplh.mod.api.components.project.Project;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import static ru.pplh.mod.api.PepeLandHelperAPI.getURI;

public class User {
    public String nickname;
    public String username;
    public String description;
    public String id;
    public String avatar;
    public Role role = new Role(new JsonObject());
    public Studio studio = new Studio(new JsonObject());
    public User(JsonObject object){
        if(object.has("nickname")) nickname = object.get("nickname").getAsString();
        if(object.has("username")) username = object.get("username").getAsString();
        if(object.has("description")) description = object.get("description").getAsString();
        if(object.has("id")) id = object.get("id").getAsString();
        if(object.has("avatar")) avatar = object.get("avatar").getAsString();
        if(object.has("role")) role = new Role(object.getAsJsonObject("role"));
        if(object.has("studio")) studio = new Studio(object.getAsJsonObject("studio"));
    }

    public List<Project> getProjects(){
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(getURI("projects?id="+id+"&page_size="+Integer.MAX_VALUE, false)));
            if(PepeLandHelper.user != null) builder.header("Authorization", "Bearer "+ PepeLandHelper.config.getString("oauth.access_token", ""));
            JsonObject projects = WebUtils.getJsonObject(builder);
            List<Project> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("page")) list.add(new Project(element.getAsJsonObject(), true));
            return list;
        } catch (Exception ex){
            PepeLandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<News> getNews(){
        try {
            JsonObject projects = WebUtils.getJsonObject(getURI("news?id="+id+"&page_size="+Integer.MAX_VALUE, false));
            List<News> list = new ArrayList<>();
            for(JsonElement element : projects.getAsJsonArray("page")) list.add(new News(element.getAsJsonObject(), true));
            return list;
        } catch (Exception ex){
            PepeLandHelper.LOG.error(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
            return new ArrayList<>();
        }
    }
}
