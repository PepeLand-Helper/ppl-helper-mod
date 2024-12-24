package ru.kelcuprum.pplhelper.gui.message;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.pplhelper.PepelandHelper;

public class NewUpdateScreen extends Screen {
    public final Screen parent;
    public final String oldVer;
    public final JsonObject pack;
    public NewUpdateScreen(Screen screen, String oldVer, JsonObject pack) {
        super(Component.translatable("pplhelper.pack.update.avalible"));
        this.parent = screen;
        this.oldVer = oldVer;
        this.pack = pack;
    }
    TextBox msg;
    @Override
    protected void init() {
        addRenderableOnly(new TextBuilder(title).setPosition(width/2-125, 10).setSize(250, 20).build());
        msg = (TextBox) addRenderableOnly(new TextBuilder( Component.translatable("pplhelper.pack.update.avalible.description", oldVer, pack.get("version").getAsString()))
                .setType(TextBuilder.TYPE.MESSAGE).setAlign(TextBuilder.ALIGN.CENTER).setPosition(width/2-125, 40).setSize(250, 40).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.update.avalible.accept"))
                .setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new DownloadScreen(parent, pack, PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false))))
                .setPosition(width/2-150, height-50).setWidth(148).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.update.avalible.auto"))
                .setOnPress((s) -> {
                    AlinLib.MINECRAFT.setScreen(new DownloadScreen(parent, pack, PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false)));
                    PepelandHelper.config.setBoolean("PACK_UPDATES.AUTO_UPDATE", true);
                })
                .setPosition(width/2-150, height-25).setWidth(148).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.update.avalible.later")).setOnPress((s) -> onClose()).setPosition(width/2+2, height-50).setWidth(148).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.update.avalible.unfollow")).setOnPress((s) -> {
            PepelandHelper.config.setBoolean("PACK_UPDATES.NOTICE", false);
            PepelandHelper.config.setBoolean("PACK_UPDATES.AUTO_UPDATE", false);
            onClose();
        }).setPosition(width/2+2, height-25).setWidth(148).build());
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x7F0A2725, 0x7F134E4A);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }
}
