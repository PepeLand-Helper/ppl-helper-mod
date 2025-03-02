package ru.kelcuprum.pplhelper.gui.components.oneshot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

public class OneShotTitleButton extends AbstractButton {
    protected OnPress onPress;
    protected boolean isInit = false;
    public OneShotTitleButton(int i, int j, int k, int l, Component component, OnPress onPress) {
        super(i, j, k, l, component);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        if(onPress != null) onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        int k = (this.active && this.isHoveredOrFocused()) ? 16777215 : 10526880;
        renderString(guiGraphics, Minecraft.getInstance().font, k | Mth.ceil(this.alpha * 255.0F) << 24);
        if(!isInit) isInit = true;
    }
    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int i){
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(getX()+((isFocused() ? 5 : -5)*getValuePos()), getY(), 1);
        guiGraphics.drawString(font, this.getMessage(), ((this.getHeight() - 8) / 2), (this.getHeight() - 8) / 2, i);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    private double getValuePos(){
        double value = (double) (System.currentTimeMillis() - timeFocused) / 125;
        if(value >= 1.0) value = 1.0;
        return value;
    }

    private long timeFocused = System.currentTimeMillis();
    @Override
    public void setFocused(boolean bl) {
        super.setFocused(bl);
        if((System.currentTimeMillis() - timeFocused) >= 125) timeFocused = System.currentTimeMillis();
        if(bl && !isHovered() && isInit) Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("kelui", "oneshot_menu_cursor")), 1.0F));
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("kelui", "oneshot_menu_decision")), 1.0F));
    }

    public interface OnPress {
        void onPress(OneShotTitleButton button);
    }
}
