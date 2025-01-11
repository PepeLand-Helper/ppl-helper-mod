package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;

public class Project {
    public int id;
    public String title;
    public String description;
    public String creators;
    public String author;

    public String icon;
    public String banner;

    public String world;
    public String coordinates$overworld;
    public String coordinates$nether;
    public String coordinates$end;

    public Project(JsonObject info){
        id = info.get("id").getAsInt();

        title = getStringInJSON("data.title", info);
        description = getStringInJSON("data.description", info);
        creators = getStringInJSON("data.creators", info, Component.translatable("pplhelper.project.unknown_creators").getString());
        author = getStringInJSON("data.author", info);

        icon = getStringInJSON("data.icon", info);
        banner = getStringInJSON("data.banner", info);
        if(hasJSONElement("data.coordinates", info)){
            world = getStringInJSON("data.coordinates.world", info);
            coordinates$overworld = getStringInJSON("data.coordinates.overworld", info);
            coordinates$nether = getStringInJSON("data.coordinates.nether", info);
            coordinates$end = getStringInJSON("data.coordinates.end", info);
        }
    }
    public String getContent(){
        return PepeLandHelperAPI.getProjectContent(this.id);
    }

    public static String getStringInJSON(String path, JsonObject parse){
        return getStringInJSON(path, parse, null);
    }

    public static String getStringInJSON(String path, JsonObject parse, String defResp){
        if(!hasJSONElement(path, parse)) return defResp;
        String[] keys = path.split("\\.");
        JsonObject jsonObject = parse;
        for(String key : keys){
            if(jsonObject.has(key)){
                JsonElement json = jsonObject.get(key);
                if(json.isJsonObject()) jsonObject = (JsonObject) json;
                else if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isString() && !json.getAsString().isBlank()) return json.getAsString();
            }
        }
        return defResp;
    }
    public static boolean hasJSONElement(String path, JsonObject parse){
        String[] keys = path.split("\\.");
        JsonObject jsonObject = parse;
        for(String key : keys){
            if(jsonObject.has(key)){
                JsonElement json = jsonObject.get(key);
                if(json.isJsonObject() && !keys[keys.length-1].equals(key)) jsonObject = (JsonObject) json;
                else if(!json.isJsonNull()) return true;
            }
        }
        return false;
    }
}
