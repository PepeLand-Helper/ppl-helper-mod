package ru.kelcuprum.pplhelper.interactive;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Interactive {
    public final String id;
    public final String status;
    public final List<Action> actions;

    public Interactive(String id, JsonObject jsonObject){
        this.id = id;
        this.status = jsonObject.get("status").getAsString();
        List<Action> actionList = new ArrayList<>();
        for(JsonElement jsonElement : jsonObject.getAsJsonArray("data"))
            actionList.add(new Action((JsonObject) jsonElement));
        this.actions = actionList;
    }
}
