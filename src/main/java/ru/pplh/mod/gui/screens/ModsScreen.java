package ru.pplh.mod.gui.screens;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.api.PepeLandHelperAPI;
import ru.pplh.mod.api.components.Mod;
import ru.pplh.mod.api.components.ResourcePack;
import ru.pplh.mod.gui.components.ModButton;
import ru.pplh.mod.gui.components.RPButton;
import ru.pplh.mod.gui.screens.message.ErrorScreen;
import ru.pplh.mod.gui.screens.builder.ScreenBuilder;

import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;

public class ModsScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.mods"))
                .addPanelWidgets(PepeLandHelper.getPanelWidgets(parent, parent));
        if(!PepeLandHelper.modsLoaded) PepeLandHelper.loadStaticInformation();
        try {
            JsonArray mods = PepeLandHelper.mods;
            builder.addWidget(new TextBuilder(Component.translatable("pplhelper.mods.description")).setType(TextBuilder.TYPE.MESSAGE));
            for(JsonElement element : mods){
                JsonObject data = element.getAsJsonObject();
                builder.addWidget(new ModButton(0, -40, DEFAULT_WIDTH(), new Mod(data), parent));
            }
        } catch (Exception ex){
            return new ErrorScreen(ex, parent);
        }
        try {
            JsonArray mods = PepeLandHelper.resource_packs;
            builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.mods.packs")));
            for(JsonElement element : mods){
                JsonObject data = element.getAsJsonObject();
                builder.addWidget(new RPButton(0, -40, DEFAULT_WIDTH(), new ResourcePack(data), parent));
            }
        } catch (Exception ex){
            return new ErrorScreen(ex, parent);
        }
        return builder.build();
    }
}
