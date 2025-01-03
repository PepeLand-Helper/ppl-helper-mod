package ru.kelcuprum.pplhelper.gui.screens;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.Mod;
import ru.kelcuprum.pplhelper.gui.components.ModButton;
import ru.kelcuprum.pplhelper.gui.screens.message.ErrorScreen;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;

public class ModsScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.mods"))
                .addPanelWidgets(PepelandHelper.getPanelWidgets(parent, parent))

                .addWidget(new TextBuilder(Component.translatable("pplhelper.mods.description")).setType(TextBuilder.TYPE.MESSAGE));
        try {
            JsonArray mods = PepeLandHelperAPI.getRecommendMods();
            if(PepeLandHelperAPI.isError(mods)) throw PepeLandHelperAPI.getError(mods);
            for(JsonElement element : mods){
                JsonObject data = element.getAsJsonObject();
                builder.addWidget(new ModButton(0, -40, DEFAULT_WIDTH(), new Mod(data), parent));
            }
        } catch (Exception ex){
            return new ErrorScreen(ex, parent);
        }
        return builder.build();
    }
}
