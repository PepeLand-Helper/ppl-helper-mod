package ru.pplh.mod.gui.screens;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.gui.screens.builder.ScreenBuilder;
import ru.pplh.mod.gui.screens.message.ErrorScreen;

import java.util.HashMap;

public class EmotesScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.emotes"))
                .addPanelWidgets(PepeLandHelper.getPanelWidgets(parent, parent));

        try {
            HashMap<String, String> stringHashMap = PepeLandHelper.getEmotesPath();
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
