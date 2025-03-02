package ru.kelcuprum.pplhelper.gui.components.oneshot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

public class OneShotPauseButton extends AbstractButton {
    protected OnPress onPress;
    protected boolean isInit = false;
    public OneShotPauseButton(int i, int j, int k, int l, Component component, OnPress onPress) {
        super(i, j, k, l, component);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        if(onPress != null) onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        if(isHoveredOrFocused() && active) guiGraphics.blitSprite(RenderType::guiTextured, ResourceLocation.fromNamespaceAndPath("pplhelper", "pause/oneshot_pause_button"), getX(), getY(), getWidth(), getHeight());
        int k = this.active ? 16777215 : 10526880;
        renderString(guiGraphics, Minecraft.getInstance().font, k | Mth.ceil(this.alpha * 255.0F) << 24);
        if(!isInit) isInit = true;
    }
    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int i){
        guiGraphics.drawCenteredString(font, this.getMessage(),this.getX() + (this.getWidth()) / 2, this.getY() + (this.getHeight() - 8) / 2, i);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
    @Override
    public void setFocused(boolean bl) {
        super.setFocused(bl);
        if(bl && !isHovered() && isInit) Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("kelui", "oneshot_menu_cursor")), 1.0F));
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    public interface OnPress {
        void onPress(OneShotPauseButton button);
    }
}
