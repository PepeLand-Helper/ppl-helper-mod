package ru.kelcuprum.pplhelper.gui.components;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.Mod;
import ru.kelcuprum.pplhelper.gui.TextureHelper;
import ru.kelcuprum.pplhelper.gui.screens.ModsScreen;

public class ModButton extends Button {
    protected Mod track;
    private final boolean isInstalled;

    public ModButton(int x, int y, int width, Mod track, Screen screen) {
        super(new ButtonBuilder().setOnPress((s) -> PepelandHelper.confirmLinkNow(new ModsScreen().build(screen), track.url)).setTitle(Component.empty()).setStyle(GuiUtils.getSelected()).setSize(width, 40).setPosition(x, y));
        this.isInstalled = FabricLoader.getInstance().isModLoaded(track.modid);
        this.track = track;
        setMessage(Component.empty().append(track.title).append(" ").append(isInstalled ? Component.translatable("pplhelper.mods.installed") : Component.empty()));
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getY() < guiGraphics.guiHeight() && !(getY() <= -getHeight())) {
            ResourceLocation icon = TextureHelper.getTexture(track.icon, track.modid, 128, 128);
            guiGraphics.blit(
                    //#if MC >= 12102
                    RenderType::guiTextured,
                    //#endif
                    icon, getX() + 2, getY() + 2, 0.0F, 0.0F, 36, 36, 36, 36);
            renderString(guiGraphics, getMessage().getString(), getX() + 45, getY() + 8);
            renderString(guiGraphics, track.description, getX() + 45, getY() + height - 8 - AlinLib.MINECRAFT.font.lineHeight);
        }
    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, Component message, int x, int y, int color) {
        int k = this.getX() + x + 40;
        int l = this.getX() + this.getWidth() - x;
        renderScrollingString(guiGraphics, font, message, k, y, l, y + font.lineHeight, color);
    }

    protected void renderString(GuiGraphics guiGraphics, String text, int x, int y) {
        if (getWidth() - 50 < AlinLib.MINECRAFT.font.width(text)) {
            renderScrollingString(guiGraphics, AlinLib.MINECRAFT.font, Component.literal(text), 5, y - 1, -1);
        } else {
            guiGraphics.drawString(AlinLib.MINECRAFT.font, text, x, y, -1);
        }
    }
}
