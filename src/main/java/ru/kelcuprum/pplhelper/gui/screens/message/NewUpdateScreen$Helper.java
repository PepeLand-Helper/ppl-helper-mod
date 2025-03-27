package ru.kelcuprum.pplhelper.gui.screens.message;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.PepeLandHelperPreLaunch;
import ru.kelcuprum.pplhelper.api.components.VersionInfo;

public class NewUpdateScreen$Helper extends Screen {
    public Screen parent;
    public VersionInfo versionInfo;
    public NewUpdateScreen$Helper(Screen screen, VersionInfo versionInfo) {
        super(Component.translatable("pplhelper.update.avalible"));
        this.parent = screen;
        this.versionInfo = versionInfo;
    }

    TextBox msg;
    @Override
    protected void init() {
        addRenderableOnly(new TextBuilder(title).setPosition(width/2-125, 10).setSize(250, 20).build());
        msg = (TextBox) addRenderableOnly(new TextBuilder( Component.translatable("pplhelper.update.avalible.description", versionInfo.version, versionInfo.latestVersion))
                .setType(TextBuilder.TYPE.MESSAGE).setAlign(TextBuilder.ALIGN.CENTER).setPosition(width/2-125, 40).setSize(250, 40).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.update.avalible.accept"))
                .setOnPress((s) -> install())
                .setPosition(width/2-150, height-50).setWidth(148).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.update.avalible.auto"))
                .setOnPress((s) -> {
                    PepeLandHelper.config.setBoolean("PPLH.AUTO_UPDATE", true);
                    install();
                })
                .setPosition(width/2-150, height-25).setWidth(148).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.update.avalible.later")).setOnPress((s) -> onClose()).setPosition(width/2+2, height-50).setWidth(148).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.update.avalible.unfollow")).setOnPress((s) -> {
            PepeLandHelper.config.setBoolean("PPLH.NOTICE", false);
            PepeLandHelper.config.setBoolean("PPLH.AUTO_UPDATE", false);
            onClose();
        }).setPosition(width/2+2, height-25).setWidth(148).build());
    }

    public void install(){
        try {
            PepeLandHelperPreLaunch.installUpdates(versionInfo);
        } catch (Exception ex){
            ex.printStackTrace();
            AlinLib.MINECRAFT.setScreen(new ErrorScreen(ex, parent));
        }
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
        PepeLandHelper.checkPackUpdates();
    }
}
