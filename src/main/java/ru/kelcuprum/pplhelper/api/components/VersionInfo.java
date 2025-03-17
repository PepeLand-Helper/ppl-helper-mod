package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import ru.kelcuprum.pplhelper.PepelandHelper;

public class VersionInfo {
    public State state;
    public String version;
    public String latestVersion = "";
    public String page = "";
    public String file = "";
    public String changelog = "";
    public VersionInfo(JsonObject object){
        if(object.get("status").getAsString().equals("new_update")) state = State.NEW_UPDATE;
        else if(object.get("status").getAsString().equals("unpublished")) state = State.UNPUBLISHED;
        else state = State.LATEST;
        this.version = object.get("checked_version").getAsString();
        if(state == State.NEW_UPDATE){
            JsonObject update = object.getAsJsonObject("update");
            latestVersion = update.get("version").getAsString();
            changelog = update.get("changelog").getAsString();
            page = update.get("page").getAsString();
            file = update.get("url").getAsString();
        } else{
            latestVersion = version;
        }
    }
    public VersionInfo(String curVersion){
        state = State.UNPUBLISHED;
        version = curVersion;
        latestVersion = curVersion;
    }

    public enum State{
        LATEST(),
        NEW_UPDATE(),
        UNPUBLISHED()
    }
}
