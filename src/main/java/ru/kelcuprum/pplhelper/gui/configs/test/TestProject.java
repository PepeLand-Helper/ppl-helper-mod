package ru.kelcuprum.pplhelper.gui.configs.test;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import ru.kelcuprum.pplhelper.api.components.Project;

public class TestProject extends Project {
    public TestProject() {
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
    }
}
