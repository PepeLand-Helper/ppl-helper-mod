package ru.kelcuprum.pplhelper.gui.screens.message;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.PepeLandHelper;

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
        addRenderableWidget(new TextBuilder(Component.literal("x"), (s) -> onClose()).setPosition(width-15, 5).setSize(10, 10).build());
        addRenderableOnly(new TextBuilder(title).setPosition(width/2-125, y).setSize(250, 20).build());
        if(error != null) addRenderableOnly(new TextBuilder(Component.literal(error.getMessage() == null ? error.getClass().getName() : error.getMessage()))
                .setType(TextBuilder.TYPE.MESSAGE).setAlign(TextBuilder.ALIGN.CENTER).setPosition(width/2-125, y+25).setSize(250, 20).build());
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x7F0A2725, 0x7F134E4A);
        guiGraphics.blit(RenderType::guiTextured, PepeLandHelper.Icons.WHITE_PEPE, guiGraphics.guiWidth() / 2 - 30, guiGraphics.guiHeight() / 2 - 30, 0, 0, 60, 60, 60, 60);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }
}
