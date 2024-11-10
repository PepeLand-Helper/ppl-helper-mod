package ru.kelcuprum.pplhelper.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.gui.components.text.MessageBox;
import ru.kelcuprum.alinlib.gui.styles.AbstractStyle;

public class Blockquote extends MessageBox {
    public Blockquote(Component label) {
        super(label);
    }

    public Blockquote(Component label, OnPress onPress) {
        super(label, onPress);
    }

    public Blockquote(Component label, AbstractStyle style, OnPress onPress) {
        super(label, style, onPress);
    }

    public Blockquote(Component label, boolean isCenter) {
        super(label, isCenter);
    }

    public Blockquote(Component label, boolean isCenter, OnPress onPress) {
        super(label, isCenter, onPress);
    }

    public Blockquote(Component label, boolean isCenter, AbstractStyle style, OnPress onPress) {
        super(label, isCenter, style, onPress);
    }

    public Blockquote(int x, int y, Component label, boolean isCenter) {
        super(x, y, label, isCenter);
    }

    public Blockquote(int x, int y, Component label, boolean isCenter, OnPress onPress) {
        super(x, y, label, isCenter, onPress);
    }

    public Blockquote(int x, int y, Component label, boolean isCenter, AbstractStyle style, OnPress onPress) {
        super(x, y, label, isCenter, style, onPress);
    }

    public Blockquote(int x, int y, int width, int height, Component label, boolean isCenter) {
        super(x, y, width, height, label, isCenter);
    }

    public Blockquote(int x, int y, int width, int height, Component label, boolean isCenter, OnPress onPress) {
        super(x, y, width, height, label, isCenter, onPress);
    }

    public Blockquote(int x, int y, int width, int height, Component label, AbstractStyle style, boolean isCenter) {
        super(x, y, width, height, label, style, isCenter);
    }

    public Blockquote(int x, int y, int width, int height, Component label, boolean isCenter, AbstractStyle style, OnPress onPress) {
        super(x, y, width, height, label, isCenter, style, onPress);
    }
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
            this.renderText(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fill(this.getX()+1, this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), 0x5d134e4a);
        guiGraphics.fill(this.getX(), this.getY(), this.getX()+1, this.getY()+this.getHeight(), 0xff257570);
    }
}
