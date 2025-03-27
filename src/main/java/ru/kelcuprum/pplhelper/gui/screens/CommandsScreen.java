package ru.kelcuprum.pplhelper.gui.screens;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.gui.screens.message.ErrorScreen;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

public class CommandsScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.commands"))
                .addPanelWidgets(PepeLandHelper.getPanelWidgets(parent, parent))
                .addWidget(new TextBuilder(Component.translatable("pplhelper.commands.description")).setType(TextBuilder.TYPE.MESSAGE));
        try {
            JsonArray mods = PepeLandHelperAPI.getCommands();
            if(PepeLandHelperAPI.isError(mods)) throw PepeLandHelperAPI.getError(mods);
            for(JsonElement element : mods){
                JsonObject data = element.getAsJsonObject();
                CategoryBox cat = new CategoryBox(Component.literal(Localization.fixFormatCodes("&l"+data.get("title").getAsString()+"&r")));
                if(data.has("description")) cat.addValue(new TextBuilder(Component.literal(data.get("description").getAsString())).setType(TextBuilder.TYPE.MESSAGE));
                for(JsonElement element1 : data.getAsJsonArray("commands")){
                    JsonObject cmd = element1.getAsJsonObject();
                    TextBox box = new TextBuilder(Component.literal(Localization.fixFormatCodes("&l/"+cmd.get("cmd").getAsString()+"&r - " + cmd.get("description").getAsString())), (s) -> {
                                AlinLib.MINECRAFT.keyboardHandler.setClipboard("/" + cmd.get("cmd").getAsString());
                                new ToastBuilder().setTitle(Component.translatable("pplhelper"))
                                        .setIcon(PepeLandHelper.Icons.WHITE_PEPE)
                                        .setMessage(Component.translatable("pplhelper.commands.notice.description")).buildAndShow();
                            }
                    ).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(0xFF13A10E).build();
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
