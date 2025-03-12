package ru.kelcuprum.pplhelper.api.components.projects;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.pplhelper.api.components.Project;

import static java.lang.Integer.parseInt;

public class FollowProject extends Project {
    public FollowProject(String world, String coordinates, String mcWorld) {
        super(GsonHelper.parse("{\n" +
                "    \"id\": -1,\n" +
                "    \"data\": {\n" +
                "        \"title\": \"Тестовый проект\",\n" +
                "        \"banner\": \"https://wf.kelcu.ru/PPLHelper/9/matr2/1.png\",\n" +
                "        \"icon\": \"https://wf.kelcu.ru/icons/alinlib.png\",\n" +
                "        \"description\": \"ДЛФЫОАВДЛЖФЫОЛВДЖФЫЛВДЖФЫЛВДЖФЫЛВЖДФЛВ\",\n" +
                "        \"content\": \"\",\n" +
                "        \"creators\": \"Kel_Caffeine\",\n" +
                "        \"author\": \"849654148706205758\",\n" +
                "        \"coordinates\": {\n" +
                "            \"world\": \"МФ\",\n" +
                "            \"overworld\": \"69 69\",\n" +
                "            \"nether\": \"69 69\",\n" +
                "            \"end\": null\n" +
                "        }\n" +
                "    }\n" +
                "}"));
        this.id = Integer.MIN_VALUE;

        this.title = "";
        this.description = "";
        this.creators = "";
        this.author = "";

        this.icon = "";
        this.banner = "";
        this.world = world;
        switch (mcWorld){
            case "minecraft:the_end" -> coordinates$end = coordinates;
            case "minecraft:the_nether" -> {
                coordinates$nether = coordinates.replaceAll("[^0-9 \\-.]", "");
                String[] args = coordinates$nether.split(" ");
                coordinates$overworld = String.format("%s %s", parseInt(args[0])*8, parseInt(args[args.length - 1])*8);
            }
            case "minecraft:overworld" -> {
                coordinates$overworld = coordinates.replaceAll("[^0-9 \\-.]", "");
                String[] args = coordinates$overworld.split(" ");
                coordinates$nether = String.format("%s %s", parseInt(args[0])/8, parseInt(args[args.length - 1])/8);
            }
            default -> coordinates$overworld = coordinates;
        }
    }
}
