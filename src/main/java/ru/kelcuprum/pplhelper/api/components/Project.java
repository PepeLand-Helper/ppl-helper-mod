package ru.kelcuprum.pplhelper.api.components;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;

import static ru.kelcuprum.alinlib.utils.GsonHelper.*;

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

        title = getStringInJSON("data.title", info, "");
        description = getStringInJSON("data.description", info, "");
        creators = getStringInJSON("data.creators", info, Component.translatable("pplhelper.project.unknown_creators").getString());
        author = getStringInJSON("data.author", info);

        icon = getStringInJSON("data.icon", info);
        banner = getStringInJSON("data.banner", info);
        if(!jsonElementIsNull("data.coordinates", info)){
            world = getStringInJSON("data.coordinates.world", info);
            coordinates$overworld = getStringInJSON("data.coordinates.overworld", info);
            coordinates$nether = getStringInJSON("data.coordinates.nether", info);
            coordinates$end = getStringInJSON("data.coordinates.end", info);
        }
    }
    public String getContent(){
        return PepeLandHelperAPI.getProjectContent(this.id);
    }
}
