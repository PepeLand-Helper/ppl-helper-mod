package ru.pplh.mod.api.components.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;

import static ru.kelcuprum.alinlib.utils.GsonHelper.getBooleanInJSON;
import static ru.kelcuprum.alinlib.utils.GsonHelper.getStringInJSON;

public class Studio {
    public String name;
    public String title;
    public String icon;
    public String[] members;
    public Studio(JsonObject data){
        this.name = getStringInJSON("name", data, "pplh");
        this.title = getStringInJSON("title", data, "PepeLand Helper");
        this.icon = getStringInJSON("icon", data, "icon");
        this.members = data.has("members") ? jsonArrayToStringArray(data.getAsJsonArray("members")) : new String[]{};
    }
    public static String[] jsonArrayToStringArray(JsonArray jsonArray){
        String[] array = new String[jsonArray.size()];
        int i = 0;
        for(JsonElement element : jsonArray){
            array[i] = element.getAsString();
            i++;
        }
        return array;
    }
    public static JsonArray stringArrayToJsonArray(String[] stringArray){
        JsonArray array = new JsonArray(stringArray.length);
        for(String element : stringArray){
            array.add(element);
        }
        return array;
    }
    // -=-=-=- Сайт -=-=-=-
    public JsonObject toJSON(){
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("title", title);
        json.addProperty("icon", title);
        json.add("members", stringArrayToJsonArray(members));
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
