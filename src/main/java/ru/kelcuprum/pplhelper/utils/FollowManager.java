package ru.kelcuprum.pplhelper.utils;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.info.World;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.Project;

import static java.lang.Integer.parseInt;

public class FollowManager {
    public static int[] coordinates;
    public static String name = "";
    public static String level;
    public static TabHelper.Worlds world;
    public static Project project;


    public static Coordinates getCurrentCoordinates(){
        if(project != null) {
            int[] coordinatesProject;
            String[] coordinatesArgs = getStringProjectCoordinates(project).split(" ");
            coordinatesProject = new int[coordinatesArgs.length];
            for(int i = 0; i < coordinatesArgs.length; i++) coordinatesProject[i] = parseInt(coordinatesArgs[i]);
            return new Coordinates(project.title, getWorldProjectWithoutWorld(project), TabHelper.getWorldByShortName(project.world), coordinatesProject);
        } else if(coordinates != null){
            return new Coordinates(name, level, world, coordinates);
        } else return null;
    }

    public static boolean playerInCurrentWorld(){
        return (project != null || coordinates != null) && PepelandHelper.playerInPPL() && TabHelper.getWorld() == world;
    }
    public static boolean playerInCurrentLevel(){
        return (project != null || coordinates != null) && PepelandHelper.playerInPPL() && (project == null ? level : getWorldProject(project)).equals(World.getCodeName());
    }

    public static String getLevelName(String name){
        return switch (name){
            case "minecraft:overworld" -> AlinLib.localization.getLocalization("world.overworld");
            case "minecraft:the_nether" -> AlinLib.localization.getLocalization("world.nether");
            default -> AlinLib.localization.getLocalization("world.end");
        };
    }

    public static void setCoordinates(String name, TabHelper.Worlds world, String level, int x, int y){
        FollowManager.coordinates = new int[] {x, y};
        FollowManager.name = name;
        FollowManager.world = world;
        FollowManager.level = level;
    }
    public static void setCoordinates(Project project){
        FollowManager.project = project;
        FollowManager.name = project.title;
        FollowManager.world = TabHelper.getWorldByShortName(project.world);
        FollowManager.level = getWorldProject(project);
        FollowManager.coordinates = null;
    }
    public static void resetCoordinates(){
        FollowManager.coordinates = null;
        FollowManager.name = null;
        FollowManager.world = null;
        FollowManager.level = null;
        FollowManager.project = null;
    }

    public static float dist(int i, int j, int k, int l) {
        int m = k - i;
        int n = l - j;
        return Mth.sqrt((float) (m * m + n * n));
    }

    public static @NotNull String getStringProjectCoordinates(Project project) {
        String coordinates = "";
        if (World.getCodeName().equals("minecraft:overworld") && project.coordinates$overworld != null && !project.coordinates$overworld.isEmpty())
            coordinates = project.coordinates$overworld;
        else if (World.getCodeName().equals("minecraft:the_nether") && project.coordinates$nether != null && !project.coordinates$nether.isEmpty())
            coordinates = project.coordinates$nether;
        else if (World.getCodeName().equals("minecraft:the_end") && project.coordinates$end != null && !project.coordinates$end.isEmpty())
            coordinates = project.coordinates$end;
        if(coordinates.isEmpty()) coordinates = getStringProjectCoordinatesWithoutWorld(project);
        return coordinates.replaceAll("[^0-9 \\-.]", "");
    }
    public static @NotNull String getStringProjectCoordinatesWithoutWorld(Project project) {
        String coordinates = "";
        if (project.coordinates$overworld != null && !project.coordinates$overworld.isEmpty())
            coordinates = project.coordinates$overworld;
        else if (project.coordinates$nether != null && !project.coordinates$nether.isEmpty())
            coordinates = project.coordinates$nether;
        else if (project.coordinates$end != null && !project.coordinates$end.isEmpty())
            coordinates = project.coordinates$end;
        return coordinates.replaceAll("[^0-9 \\-.]", "");
    }
    public static @NotNull String getWorldProjectWithoutWorld(Project project) {
        String coordinates = "";
        if (project.coordinates$overworld != null && !project.coordinates$overworld.isEmpty())
            coordinates = "minecraft:overworld";
        else if (project.coordinates$nether != null && !project.coordinates$nether.isEmpty())
            coordinates = "minecraft:the_nether";
        else if (project.coordinates$end != null && !project.coordinates$end.isEmpty())
            coordinates = "minecraft:the_end";
        return coordinates;
    }
    public static @NotNull String getWorldProject(Project project) {
        String coordinates = "";
        if (project.coordinates$overworld != null && !project.coordinates$overworld.isEmpty() && World.getCodeName().equals("minecraft:overworld"))
            coordinates = "minecraft:overworld";
        else if (project.coordinates$nether != null && !project.coordinates$nether.isEmpty() && World.getCodeName().equals("minecraft:the_nether"))
            coordinates = "minecraft:the_nether";
        else if (project.coordinates$end != null && !project.coordinates$end.isEmpty() && World.getCodeName().equals("minecraft:the_end"))
            coordinates = "minecraft:the_end";
        if(coordinates.isEmpty()) coordinates = getWorldProjectWithoutWorld(project);
        return coordinates;
    }


    public record Coordinates(String name, String level, TabHelper.Worlds world, int[] coordinates) {
        public String getStringCoordinates() {
                StringBuilder builder = new StringBuilder().append(this.coordinates()[0]);
                for (int i = 1; i < this.coordinates().length; i++) builder.append(" ").append(this.coordinates()[i]);
                return builder.toString();
            }
        }
}
