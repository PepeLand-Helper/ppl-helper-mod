package ru.kelcuprum.pplhelper.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonHelper {
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
    public static boolean getBooleanInJSON(String path, JsonObject parse, boolean defResp){
        if(!hasJSONElement(path, parse)) return defResp;
        String[] keys = path.split("\\.");
        JsonObject jsonObject = parse;
        for(String key : keys){
            if(jsonObject.has(key)){
                JsonElement json = jsonObject.get(key);
                if(json.isJsonObject()) jsonObject = (JsonObject) json;
                else if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean()) return json.getAsBoolean();
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
