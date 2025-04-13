package ru.kelcuprum.pplhelper.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.pplhelper.api.components.project.Project;
import ru.kelcuprum.pplhelper.gui.TextureHelper;
import ru.kelcuprum.pplhelper.gui.screens.pages.ProjectScreen;

import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.WHITE_PEPE;

public class ProjectButton extends Button {
    protected Project project;

    public ProjectButton(int x, int y, int width, Project project, Screen screen) {
        super(new ButtonBuilder().setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ProjectScreen(screen, project))).setTitle(Component.empty()).setStyle(GuiUtils.getSelected()).setSize(width, project.description.isEmpty() ? 20 : 40).setPosition(x, y));
        this.project = project;
        MutableComponent title = Component.empty().append(Component.literal(project.title));
        if(project.state != Project.State.BUILT){
            switch (project.state){
                case BUILD -> title.append(" (").append(Component.translatable("pplhelper.project.state.build")).append(")");
                case PLANNED -> title.append(" (").append(Component.translatable("pplhelper.project.state.planned")).append(")");
            }
        }
        setMessage(title);
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getY() < guiGraphics.guiHeight() && !(getY() <= -getHeight())) {
            int x = 5;
            ResourceLocation icon = (project.icon != null && !project.icon.isEmpty()) ? TextureHelper.getTexture(project.icon, String.format("project_%s", project.id)) : WHITE_PEPE;
            guiGraphics.blit(RenderType::guiTextured, icon, getX() + 2, getY() + 2, 0.0F, 0.0F, getHeight()-4, getHeight()-4, getHeight()-4, getHeight()-4);
            x += getHeight();
            renderString(guiGraphics, getMessage(), getX() + x, getY() + (project.description.isEmpty() ? 6 : 8));
            if(!project.description.isEmpty()) renderString(guiGraphics, project.description, getX() + x, getY() + getHeight() - 8 - AlinLib.MINECRAFT.font.lineHeight);
        }
    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, Component message, int y) {
        int k = this.getX() + height + 5;
        int l = this.getX() + this.getWidth() - 5;
        renderScrollingString(guiGraphics, font, message, k, y, l, y + font.lineHeight, -1);
    }

    protected void renderString(GuiGraphics guiGraphics, String text, int x, int y) {
        renderString(guiGraphics, Component.literal(text), x, y);
    }
    protected void renderString(GuiGraphics guiGraphics, Component text, int x, int y) {
        if (getWidth() - 50 < AlinLib.MINECRAFT.font.width(text))
            renderScrollingString(guiGraphics, AlinLib.MINECRAFT.font, text, y - 1);
        else guiGraphics.drawString(AlinLib.MINECRAFT.font, text, x, y, -1);
    }
}
