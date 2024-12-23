package ru.kelcuprum.pplhelper.gui.components;

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
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.TextureHelper;
import ru.kelcuprum.pplhelper.gui.screens.project.ProjectScreen;

public class ProjectButton extends Button {
    protected Project project;

    public ProjectButton(int x, int y, int width, Project project, Screen screen) {
        super(new ButtonBuilder().setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ProjectScreen(screen, project))).setTitle(Component.empty()).setStyle(GuiUtils.getSelected()).setSize(width, 40).setPosition(x, y));
        this.project = project;
        setMessage(Component.literal(project.title));
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getY() < guiGraphics.guiHeight() && !(getY() <= -getHeight())) {
            int x = 5;
            if(project.icon != null && !project.icon.isEmpty()) {
                ResourceLocation icon = TextureHelper.getTexture(project.icon, String.format("project_%s", project.id));
                guiGraphics.blit(
                        //#if MC >= 12102
                        RenderType::guiTextured,
                        //#endif
                        icon, getX() + 2, getY() + 2, 0.0F, 0.0F, 36, 36, 36, 36);
                x+=40;
            }
            renderString(guiGraphics, project.title, getX() + x, getY() + 8);
            renderString(guiGraphics, project.description, getX() + x, getY() + height - 8 - AlinLib.MINECRAFT.font.lineHeight);
        }
    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, Component message, int x, int y, int color) {
        int k = this.getX() + x + (project.icon != null && !project.icon.isEmpty() ? 40 : 0);
        int l = this.getX() + this.getWidth() - x;
        renderScrollingString(guiGraphics, font, message, k, y, l, y + font.lineHeight, color);
    }

    protected void renderString(GuiGraphics guiGraphics, String text, int x, int y) {
        if (getWidth() - (project.icon != null && !project.icon.isEmpty() ? 50 : 10) < AlinLib.MINECRAFT.font.width(text))
            renderScrollingString(guiGraphics, AlinLib.MINECRAFT.font, Component.literal(text), 5, y - 1, -1);
        else guiGraphics.drawString(AlinLib.MINECRAFT.font, text, x, y, -1);
    }
}
