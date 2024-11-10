package ru.kelcuprum.pplhelper.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;

import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_HEIGHT;
import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;

public class ScaledTextBox extends TextBox {
    public final float scale;
    public ScaledTextBox(Component label, float scale) {
        this(0, 0, GuiUtils.DEFAULT_WIDTH(), 20, label, true, scale, null);
    }

    public ScaledTextBox(Component label, float scale, OnPress onPress) {
        this(0, 0, GuiUtils.DEFAULT_WIDTH(), 20, label, true, scale, onPress);
    }

    public ScaledTextBox(Component label, boolean isCenter, float scale) {
        this(label, isCenter, scale, null);
    }

    public ScaledTextBox(Component label, boolean isCenter, float scale, OnPress onPress) {
        this(0, 0, label, isCenter, scale, onPress);
    }

    public ScaledTextBox(int x, int y, Component label, boolean isCenter, float scale) {
        this(x, y, label, isCenter, scale, null);
    }

    public ScaledTextBox(int x, int y, Component label, boolean isCenter, float scale, OnPress onPress) {
        this(x, y, DEFAULT_WIDTH(), DEFAULT_HEIGHT, label, isCenter, scale, onPress);
    }

    public ScaledTextBox(int x, int y, int width, int height, Component label, boolean isCenter, float scale) {
        this(x, y, width, height, label, isCenter, scale, null);
    }

    boolean isCentered;

    public ScaledTextBox(int x, int y, int width, int height, Component label, boolean isCenter, float scale, OnPress onPress) {
        super(x, y, width, height, label, isCenter, onPress);
        this.scale = scale;
        this.isCentered = isCenter;
        this.setHeight((int) (height*scale));
    }

    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
        guiGraphics.enableScissor(getX(), getY(), getRight(), getBottom());
        if(isDoesNotFit()){
//            renderScrollingString(guiGraphics, AlinLib.MINECRAFT.font, 2, -1);
            if(isCentered) guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, AlinLib.MINECRAFT.font.plainSubstrByWidth(this.getMessage().getString(), (int) ((width-20)/scale))+"...", (int) ((this.getX() + (float) this.getWidth() / 2) / scale), (int) ((this.getY() + (float) ((this.getHeight()/scale) - 8) / 2)/scale), 16777215);
            else guiGraphics.drawString(AlinLib.MINECRAFT.font, AlinLib.MINECRAFT.font.plainSubstrByWidth(this.getMessage().getString(), (int) ((width-30)/scale))+"...", (int) ((this.getX() + (float) ((this.getHeight()/scale) - 8) / 2)/scale), (int) ((this.getY() + (float) ((this.getHeight()/scale) - 8) / 2)/scale), 16777215);
            if(isHovered) {
                guiGraphics.pose().popPose();
                guiGraphics.disableScissor();
                guiGraphics.renderTooltip(AlinLib.MINECRAFT.font, AlinLib.MINECRAFT.font.split(getMessage(), width - 10), i, j);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.enableScissor(getX(), getY(), getRight(), getBottom());
            }
        } else if (this.isCentered) {
            guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, this.getMessage(), (int) ((this.getX() + (float) this.getWidth() / 2) / scale), (int) ((this.getY() + (float) ((this.getHeight()/scale) - 8) / 2)/scale), 16777215);
        } else {
            guiGraphics.drawString(AlinLib.MINECRAFT.font, this.getMessage(), (int) ((this.getX() + (float) ((this.getHeight()/scale) - 8) / 2)/scale), (int) ((this.getY() + (float) ((this.getHeight()/scale) - 8) / 2)/scale), 16777215);
        }
        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
    }

    private boolean isDoesNotFit(){
        int size = (int) ((AlinLib.MINECRAFT.font.width(this.getMessage())+10)*scale);
        return (size) > this.getWidth();
    }


}
