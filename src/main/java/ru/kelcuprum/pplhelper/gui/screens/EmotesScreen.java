package ru.kelcuprum.pplhelper.gui.screens;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.Mod;
import ru.kelcuprum.pplhelper.api.components.ResourcePack;
import ru.kelcuprum.pplhelper.gui.components.ModButton;
import ru.kelcuprum.pplhelper.gui.components.RPButton;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;
import ru.kelcuprum.pplhelper.gui.screens.message.ErrorScreen;

import java.util.HashMap;

import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;

public class EmotesScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.emotes"))
                .addPanelWidgets(PepelandHelper.getPanelWidgets(parent, parent));

        try {
            HashMap<String, String> stringHashMap = PepelandHelper.getEmotesPath();
            builder.addWidget(new TextBuilder(Component.translatable("pplhelper.emotes.description")).setType(TextBuilder.TYPE.MESSAGE));
            if(stringHashMap.isEmpty()) builder.addWidget(new TextBuilder(Component.literal("Тут пустовато :(")));
            else{
                for (String key : stringHashMap.keySet()){
                    String[] war = key.split("/");
                    String name = war[war.length-1].split("\\.")[0];
                    if(!name.equals("black")){
                        builder.addWidget(new ButtonBuilder(Component.literal(stringHashMap.get(key) + " - " + name)).setOnPress((s) -> {
                            AlinLib.MINECRAFT.keyboardHandler.setClipboard(stringHashMap.get(key));
                            new ToastBuilder().setTitle(Component.literal("PepeLand Helper")).setMessage(Component.translatable("pplhelper.emotes.copy", stringHashMap.get(key))).buildAndShow();
                        }));
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return new ErrorScreen(ex, parent);
        }
        return builder.build();
    }
}
