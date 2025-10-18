package ru.pplh.mod.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.PepeLandHelper;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static ru.pplh.mod.utils.JsonHelper.*;

public class TradeManager {
    public static ArrayList<Category> categories = new ArrayList<>();
    public static HashMap<String, Category> mapCategories = new HashMap<>();
    public static Category activeCategory = null;

    public static void loadInfo(JsonObject jsonObject){
        categories = new ArrayList<>();
        mapCategories = new HashMap<>();
        JsonArray jsonArray = jsonObject.getAsJsonArray("categories");
        int radius = jsonObject.get("radius").getAsNumber().intValue();
        for(JsonElement jsonElement : jsonArray){
            JsonObject jsonObject1 = (JsonObject) jsonElement;
            String[] coordinates = jsonObject1.get("position").getAsString().toLowerCase().replaceAll("x", ""+radius).replaceAll("z", ""+radius).split(" ");
            int[] coords = new int[coordinates.length];
            int i = 0;
            for(String s : coordinates){
                coords[i] = parseInt(s);
                i++;
            }
            String[] sizes = jsonObject1.get("sizes").getAsString().split(" ");
            if(sizes.length != 3) throw new RuntimeException("sizes != 3");
            double[] size = new double[sizes.length];
            i = 0;
            for(String s : sizes){
                size[i] = parseInt(s);
                i++;
            }
            Vec3 vec3 = null;
            if(coords.length == 2)
                vec3 = new Vec3(FollowManager.getCord(coords[0]), 0, FollowManager.getCord(coords[1]));
            else if(coords.length == 3) vec3 = new Vec3(FollowManager.getCord(coords[0]), FollowManager.getCord(coords[1]), FollowManager.getCord(coords[2]));
            else throw new RuntimeException("coords.length != 2/3");
            PepeLandHelper.LOG.log("%s %s %s", vec3.x, vec3.y, vec3.z);

            Vec3 pos1 = new Vec3(vec3.x-(size[0]/2), vec3.y-(size[1]/2), vec3.z-(size[2]/2));
            Vec3 pos2 = new Vec3(vec3.x+(size[0]/2), vec3.y+(size[1]/2), vec3.z+(size[2]/2));
            int color = (int) Long.parseLong(getStringInJSON("color", jsonObject1, "FFFFFFFF").toUpperCase(), 16);
            Category category = new Category(jsonObject1.get("name").getAsString(), ResourceLocation.parse(jsonObject1.get("icon").getAsString()), color,vec3, pos1, pos2);
            categories.add(category);
            mapCategories.put(category.name, category);
        }
    }

    public static Category getItemCategory(String id){
        Category category = null;
        if(PepeLandHelper.tradeRegistry.keySet().isEmpty()) return null;
        else {
            String finalCategoryName = "";
            for(String categoryName : PepeLandHelper.tradeRegistry.keySet()){
                JsonArray jsonArray = PepeLandHelper.tradeRegistry.getAsJsonArray(categoryName);
                boolean isValidCategory = false;
                for(JsonElement jsonElement : jsonArray){
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    String itemID = getStringInJSON("registry", jsonObject, null);
                    if(itemID == null) throw new RuntimeException("registry == null");
                    else if(itemID.equalsIgnoreCase(id)){
                        isValidCategory = true;
                        break;
                    }
                }
                if(isValidCategory){
                    finalCategoryName = categoryName;
                    break;
                }
            }

            if(hasJSONElement(String.format("aligns.%s", finalCategoryName), PepeLandHelper.trade)){
                finalCategoryName = getStringInJSON(String.format("aligns.%s", finalCategoryName), PepeLandHelper.trade, finalCategoryName);
            }
            category = mapCategories.getOrDefault(finalCategoryName, null);
        }
        return category;
    }
    public static String getItemRussianName(String id){
        String finalCategoryName = "";
        if(PepeLandHelper.tradeRegistry.keySet().isEmpty()) return null;
        else {
            for(String categoryName : PepeLandHelper.tradeRegistry.keySet()){
                JsonArray jsonArray = PepeLandHelper.tradeRegistry.getAsJsonArray(categoryName);
                for(JsonElement jsonElement : jsonArray){
                    JsonObject jsonObject = (JsonObject) jsonElement;
                    String itemID = getStringInJSON("registry", jsonObject, null);
                    if(itemID == null) throw new RuntimeException("registry == null");
                    else if(itemID.equalsIgnoreCase(id)){
                        finalCategoryName = getStringInJSON("item", jsonObject, itemID);
                        break;
                    }
                }
            }
        }
        return finalCategoryName;
    }

    public record Category(String name, ResourceLocation icon, int color, Vec3 center, Vec3 pos1, Vec3 pos2){}
}
