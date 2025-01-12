package ru.kelcuprum.pplhelper.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.pplhelper.api.OAuth;
import ru.kelcuprum.pplhelper.api.components.user.User;
import ru.kelcuprum.pplhelper.gui.TextureHelper;

import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.PEPE;

public class UserCard extends Button {
    protected User track;

    public UserCard(int x, int y, int width, User track) {
        super(new ButtonBuilder().setTitle(Component.empty()).setStyle(GuiUtils.getSelected()).setSize(width, 40).setPosition(x, y).setActive(false));
        this.track = track;
    }

    @Override
    public @NotNull Component getMessage(){
        return Component.empty().append(track.nickname == null ? track.username : track.nickname);
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getY() < guiGraphics.guiHeight() && !(getY() <= -getHeight())) {
            ResourceLocation icon = track.avatar.isBlank() ? PEPE : TextureHelper.getTexture(OAuth.getURI(track.avatar,false), "avatarka_"+track.id);
            guiGraphics.blit(RenderType::guiTextured, icon, getX() + 2, getY() + 2, 0.0F, 0.0F, 36, 36, 36, 36);
            renderString(guiGraphics, getMessage().getString(), getX() + 45, getY() + 8);
            renderString(guiGraphics, track.role.title, getX() + 45, getY() + height - 8 - AlinLib.MINECRAFT.font.lineHeight);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, Component message, int y) {
        int k = this.getX() + 5 + 40;
        int l = this.getX() + this.getWidth() - 5;
        renderScrollingString(guiGraphics, font, message, k, y, l, y + font.lineHeight, -1);
    }

    protected void renderString(GuiGraphics guiGraphics, String text, int x, int y) {
        if (getWidth() - 50 < AlinLib.MINECRAFT.font.width(text)) renderScrollingString(guiGraphics, AlinLib.MINECRAFT.font, Component.literal(text), y - 1);
        else guiGraphics.drawString(AlinLib.MINECRAFT.font, text, x, y, -1);
    }
}
