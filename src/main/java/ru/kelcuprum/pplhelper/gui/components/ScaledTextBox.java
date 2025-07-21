package ru.kelcuprum.pplhelper.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
//#if MC >= 12106
import org.joml.Matrix3x2f;
//#endif
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
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
        super(new TextBuilder(label, onPress).setAlign(isCenter ? TextBuilder.ALIGN.CENTER : TextBuilder.ALIGN.LEFT).setPosition(x, y).setSize(width, height));
        this.scale = scale;
        this.isCentered = isCenter;
        this.setHeight((int) (height*scale));
    }

    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        //#if MC >= 12106
        guiGraphics.pose().pushMatrix();
        //#elseif MC >= 12102
        //$$ guiGraphics.pose().pushPose();
        //#endif
        guiGraphics.pose().translate(getX() + 6, getY() + ((float) (getHeight() - 8) / 2)
        //#if MC >= 12102
        //$$ , 0f
        //#endif
        );
        guiGraphics.pose().scale(scale, scale
                //#if MC >= 12102
                //$$ , scale
                //#endif
        );
        if(isDoesNotFit()){
            if(isCentered) guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, AlinLib.MINECRAFT.font.plainSubstrByWidth(this.getMessage().getString(), (int) ((width-30)/scale))+"...", width/2, 0, -1);
            else guiGraphics.drawString(AlinLib.MINECRAFT.font, AlinLib.MINECRAFT.font.plainSubstrByWidth(this.getMessage().getString(), (int) ((width-30)/scale))+"...", 0, 0, -1);
            if(isHovered) {
                //#if MC >= 12106
                guiGraphics.setTooltipForNextFrame(AlinLib.MINECRAFT.font, getMessage(), i, j);
                //#elseif MC >= 12102
                //$$ guiGraphics.pose().popPose();
                //$$ guiGraphics.renderTooltip(AlinLib.MINECRAFT.font, AlinLib.MINECRAFT.font.split(getMessage(), width - 10), i, j);
                //$$ guiGraphics.pose().pushPose();
                //$$ guiGraphics.pose().translate(getX() + 6, getY() + (float) (this.getHeight() - 8) / 2, 0);
                //$$ guiGraphics.pose().scale(scale, scale, scale);
                //#endif
            }
        } else if (this.isCentered) guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, this.getMessage(), width/2, 0, -1);
        else guiGraphics.drawString(AlinLib.MINECRAFT.font, this.getMessage(), 0,0, -1);

        guiGraphics.pose()
                //#if MC >= 12106
                .popMatrix();
        //#elseif MC >= 12102
        //$$ .popPose();
        //#endif
    }

    private boolean isDoesNotFit(){
        int size = (int) ((AlinLib.MINECRAFT.font.width(this.getMessage())+10)*scale);
        return (size) > this.getWidth();
    }


}
