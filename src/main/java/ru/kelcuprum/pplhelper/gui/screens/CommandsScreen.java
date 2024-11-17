package ru.kelcuprum.pplhelper.gui.screens;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.components.text.MessageBox;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.gui.components.Blockquote;
import ru.kelcuprum.pplhelper.gui.configs.ConfigScreen;
import ru.kelcuprum.pplhelper.gui.configs.UpdaterScreen;
import ru.kelcuprum.pplhelper.gui.message.ErrorScreen;

import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.MODS;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.PROJECTS;

public class CommandsScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent, Component.translatable("pplhelper"))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.news")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new NewsListScreen(parent))).setIcon(WIKI))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.projects")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ProjectsScreen(parent))).setIcon(PROJECTS))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.commands")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new CommandsScreen().build(parent))).setIcon(LIST))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.mods")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ModsScreen().build(parent))).setIcon(MODS))

                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.pack")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new UpdaterScreen().build(parent))).setIcon(PepelandHelper.Icons.PACK_INFO))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setIcon(OPTIONS))

                .addWidget(new TextBox(Component.translatable("pplhelper.commands")))
                .addWidget(new MessageBox(Component.translatable("pplhelper.commands.description")));
        try {
            JsonArray mods = PepeLandHelperAPI.getCommands();
            for(JsonElement element : mods){
                JsonObject data = element.getAsJsonObject();
                CategoryBox cat = new CategoryBox(Component.literal(Localization.fixFormatCodes("&l"+data.get("title").getAsString()+"&r")));
                if(data.has("description")) cat.addValue(new MessageBox(Component.literal(data.get("description").getAsString()), false));
                for(JsonElement element1 : data.getAsJsonArray("commands")){
                    JsonObject cmd = element1.getAsJsonObject();
                    Blockquote box = new Blockquote(Component.literal(Localization.fixFormatCodes("&l/"+cmd.get("cmd").getAsString()+"&r - " + cmd.get("description").getAsString())), false, (s) -> {
                                AlinLib.MINECRAFT.keyboardHandler.setClipboard("/" + cmd.get("cmd").getAsString());
                                new ToastBuilder().setTitle(Component.translatable("pplhelper"))
                                        .setIcon(PepelandHelper.Icons.WHITE_PEPE)
                                        .setMessage(Component.translatable("pplhelper.commands.notice.description")).buildAndShow();
                            }
                    );
                    cat.addValue(box);
                }
                cat.changeState(false);
                builder.addWidget(cat);
            }
        } catch (Exception ex){
            return new ErrorScreen(ex, parent);
        }
        return builder.build();
    }
}
