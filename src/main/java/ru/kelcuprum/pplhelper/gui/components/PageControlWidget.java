package ru.kelcuprum.pplhelper.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import ru.kelcuprum.alinlib.AlinLib;

public class PageControlWidget extends AbstractWidget {
    public int position;
    public int size;
    public final OnPress onPress;
    public PageControlWidget(int x, int y, int width, int height, int position, int size, OnPress onPress) {
        super(x, y, width, height, Component.empty());
        this.position = position;
        this.size = size;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        int p = (this.getHeight() - 8) / 2;
        guiGraphics.drawString(AlinLib.MINECRAFT.font, "◀", getX()+p, getY()+p, availableLeftScroll() ? -1 : 0xFFadb5bd);
        guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, String.format("%s / %s", position+1, size), getX()+(getWidth()/2), getY()+p, -1);
        guiGraphics.drawString(AlinLib.MINECRAFT.font, "▶", getRight()-p-AlinLib.MINECRAFT.font.width("▶"), getY()+p, availableRightScroll() ? -1 : 0xFFadb5bd);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if(d < getX()+getHeight() && availableLeftScroll()){
            leftScroll();
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
            onPress.onPress(this);
            return true;
        } else if(d > getRight()-getHeight() && availableRightScroll()){
            rightScroll();
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
            onPress.onPress(this);
            return true;
        }
        return false;
    }

    public boolean availableLeftScroll(){
        return position > 0;
    }
    public boolean availableRightScroll(){
        return position+1 != size;
    }

    public void leftScroll(){
        if(position - 1 != -1) position--;
    }
    public void rightScroll(){
        if(position + 1 != size) position++;
    }

    public interface OnPress {
        void onPress(PageControlWidget var1);
    }
}
