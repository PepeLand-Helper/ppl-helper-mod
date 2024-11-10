package ru.kelcuprum.pplhelper.gui.message;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.gui.components.text.MessageBox;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.pplhelper.PepelandHelper;

public class ErrorScreen extends Screen {
    public Screen parent;
    public Exception error;
    public ErrorScreen(Exception ex, Screen screen){
        this(screen);
        this.error = ex;
    }
    public ErrorScreen(Screen screen) {
        super(Component.translatable("pplhelper.error"));
        this.parent = screen;
    }

    @Override
    protected void init() {
        int y = height / 2 + 30;
        addRenderableWidget(new TextBox(width-15, 5, 10, 10, Component.literal("x"), true, (s) -> onClose()));
        addRenderableOnly(new TextBox(width/2-125, y, 250, 20, title, true));
        if(error != null) addRenderableOnly(new MessageBox(width/2-125, y+25, 250, 20, Component.literal(error.getMessage() == null ? error.getClass().getName() : error.getMessage()), true));
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x7F0A2725, 0x7F134E4A);
        guiGraphics.blit(PepelandHelper.Icons.WHITE_PEPE, guiGraphics.guiWidth() / 2 - 30, guiGraphics.guiHeight() / 2 - 30, 0, 0, 60, 60, 60, 60);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }
}
