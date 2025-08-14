package ru.pplh.mod.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
//#if MC >= 12106
import net.minecraft.client.renderer.RenderPipelines;
//#endif
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.pplh.mod.api.OAuth;
import ru.pplh.mod.api.components.user.User;
import ru.pplh.mod.gui.TextureHelper;

import static ru.pplh.mod.PepeLandHelper.Icons.PEPE;

public class UserCard extends Button {
    protected User track;
    protected boolean isShort;

    public UserCard(int x, int y, int width, User track) {
        this(x, y, width, track, false);
    }
    public UserCard(int x, int y, int width, User track, boolean isShort) {
        super(new ButtonBuilder().setTitle(Component.empty()).setStyle(GuiUtils.getSelected()).setSize(width, isShort ? 20 : 40).setPosition(x, y).setActive(false));
        this.track = track;
        this.isShort = isShort;
    }

    @Override
    public @NotNull Component getMessage(){
        MutableComponent component = Component.empty().append(track.nickname == null ? track.username : track.nickname);
        if(isShort) component.append(" / ").append(track.role.title);
        return component;
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getY() < guiGraphics.guiHeight() && !(getY() <= -getHeight())) {
            ResourceLocation icon = track.avatar.isBlank() ? PEPE : TextureHelper.getTexture(OAuth.getURI(track.avatar,false), "avatarka_"+track.id);
            guiGraphics.blit(
                    //#if MC >= 12106
                    RenderPipelines.GUI_TEXTURED,
                    //#elseif MC >= 12102
                    //$$ RenderType::guiTextured,
                    //#endif
                    icon, getX() + 2, getY() + 2, 0.0F, 0.0F, getHeight()-4, getHeight()-4, getHeight()-4, getHeight()-4);
            if(isShort) renderString(guiGraphics, getMessage().getString(), getX() + getHeight() + 5, getY() + (this.getHeight() - 8) / 2);
            else {
                renderString(guiGraphics, getMessage().getString(), getX() + 45, getY() + 8);
                renderString(guiGraphics, track.role.title, getX() + 45, getY() + height - 8 - AlinLib.MINECRAFT.font.lineHeight);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, Component message, int y) {
        int k = this.getX() + 5 + getHeight();
        int l = this.getX() + this.getWidth() - 5;
        renderScrollingString(guiGraphics, font, message, k, y, l, y + font.lineHeight, -1);
    }

    protected void renderString(GuiGraphics guiGraphics, String text, int x, int y) {
        if (getWidth() - (10+getHeight()) < AlinLib.MINECRAFT.font.width(text)) renderScrollingString(guiGraphics, AlinLib.MINECRAFT.font, Component.literal(text), y - 1);
        else guiGraphics.drawString(AlinLib.MINECRAFT.font, text, x, y, -1);
    }
}
