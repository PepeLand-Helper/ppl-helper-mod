package ru.kelcuprum.pplhelper.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class HorizontalRule extends AbstractWidget {
    public HorizontalRule(int x, int y, int width) {
        this(x, y, width, 1);
    }
    public HorizontalRule(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fill(getX(), getY(), getRight(), getBottom(), 0x7FFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
