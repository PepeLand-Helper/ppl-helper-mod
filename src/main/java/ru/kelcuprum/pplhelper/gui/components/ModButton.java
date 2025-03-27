package ru.kelcuprum.pplhelper.gui.components;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.api.components.Mod;
import ru.kelcuprum.pplhelper.gui.TextureHelper;
import ru.kelcuprum.pplhelper.gui.screens.ModsScreen;

import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.PEPE;

public class ModButton extends Button {
    protected Mod track;

    public ModButton(int x, int y, int width, Mod track, Screen screen) {
        super(new ButtonBuilder().setOnPress((s) -> PepeLandHelper.confirmLinkNow(new ModsScreen().build(screen), track.url)).setTitle(Component.empty()).setStyle(GuiUtils.getSelected()).setSize(width, 40).setPosition(x, y));
        this.track = track;
    }

    @Override
    public @NotNull Component getMessage(){
        boolean isInstalled = FabricLoader.getInstance().isModLoaded(track.modid);
        return Component.empty().append(track.title).append(" ").append(isInstalled ? Component.translatable("pplhelper.mods.installed") : Component.empty());
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getY() < guiGraphics.guiHeight() && !(getY() <= -getHeight())) {
            ResourceLocation icon = track.icon.isBlank() ? PEPE : TextureHelper.getTexture(track.icon, track.modid);
            guiGraphics.blit(RenderType::guiTextured, icon, getX() + 2, getY() + 2, 0.0F, 0.0F, 36, 36, 36, 36);
            renderString(guiGraphics, getMessage().getString(), getX() + 45, getY() + 8);
            renderString(guiGraphics, track.description, getX() + 45, getY() + height - 8 - AlinLib.MINECRAFT.font.lineHeight);
        }
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
