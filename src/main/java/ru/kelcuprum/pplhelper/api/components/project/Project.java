package ru.kelcuprum.pplhelper.api.components.project;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.pplhelper.api.OAuth;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.user.User;
import ru.kelcuprum.pplhelper.utils.LitematicaManager;

import java.io.File;
import java.nio.file.Path;

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
    public State state;

    //
    public boolean schematicEnable;
    public String schematicRotate;
    public String schematicMirror;

    public int schematicX;
    public int schematicY;
    public int schematicZ;

    public Project(JsonObject info){
        id = info.get("id").getAsInt();

        title = getStringInJSON("data.title", info, "");
        description = getStringInJSON("data.description", info, "");
        creators = getStringInJSON("data.creators", info, Component.translatable("pplhelper.project.unknown_creators").getString());
        author = getStringInJSON("data.author", info);

        icon = getStringInJSON("data.icon", info);
        banner = getStringInJSON("data.banner", info);
        state = getStateByID(getNumberInJSON("data.state", info, 0).intValue());

        schematicEnable = getBooleanInJSON("data.schematic.enable", info, false);
        if(schematicEnable){
            schematicRotate = getStringInJSON("data.schematic.rotate", info, "none");
            schematicMirror = getStringInJSON("data.schematic.mirror", info, "none");

            schematicX = getNumberInJSON("data.schematic.x", info, 0).intValue();
            schematicY = getNumberInJSON("data.schematic.y", info, 0).intValue();
            schematicZ = getNumberInJSON("data.schematic.z", info, 0).intValue();
        }
        if(!jsonElementIsNull("data.coordinates", info)){
            world = getStringInJSON("data.coordinates.world", info);
            coordinates$overworld = getStringInJSON("data.coordinates.overworld", info);
            coordinates$nether = getStringInJSON("data.coordinates.nether", info);
            coordinates$end = getStringInJSON("data.coordinates.end", info);
        }
    }

    public JsonObject toJSON(){
        JsonObject project = new JsonObject();
        project.addProperty("title", title);
        project.addProperty("description", description);
        project.addProperty("creators", creators);
        project.addProperty("author", author);

        project.addProperty("icon", icon);
        project.addProperty("banner", banner);
        project.addProperty("state", getIDByState(state));
        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("world", world);
        coordinates.addProperty("overworld", coordinates$overworld);
        coordinates.addProperty("nether", coordinates$nether);
        coordinates.addProperty("end", coordinates$end);
        project.add("coordinates", coordinates);
        JsonObject schematic = new JsonObject();
        schematic.addProperty("enable", schematicEnable);
        schematic.addProperty("rotate", schematicRotate);
        schematic.addProperty("mirror", schematicMirror);
        schematic.addProperty("x", schematicX);
        schematic.addProperty("y", schematicY);
        schematic.addProperty("z", schematicZ);
        project.add("schematic", schematic);
        return project;
    }

    public String getContent(){
        return PepeLandHelperAPI.getProjectContent(this.id);
    }

    public Page[] getPages() {return PepeLandHelperAPI.getProjectPages(this.id);}

    public User getAuthor(){
        return OAuth.getUserByID(author);
    }

    public void loadSchematic(){
        if(!schematicEnable || !FabricLoader.getInstance().isModLoaded("litematica")) return;
        PepeLandHelperAPI.downlaodProjectSchematic(id);
        File file = Path.of(String.format("./schematics/pplhelper-%s.litematic", id)).toFile();
        LitematicaManager.loadSchematic(file, this);
    }

    public static State getStateByID(int id){
        return switch (id){
            case 1 -> State.BUILD;
            case 2 -> State.PLANNED;
            default -> State.BUILT;
        };
    }
    public static int getIDByState(State state){
        return switch (state){
            case State.BUILD -> 1;
            case State.PLANNED -> 2;
            default -> 0;
        };
    }

    public enum State {
        BUILT(),
        BUILD(),
        PLANNED()
    }
}
