package ru.kelcuprum.pplhelper.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.pplhelper.api.components.News;
import ru.kelcuprum.pplhelper.gui.screens.NewsListScreen;
import ru.kelcuprum.pplhelper.gui.screens.project.NewsScreen;

public class NewsButton extends Button {
    protected News news;

    public NewsButton(int x, int y, int width, News news, Screen screen) {
        super(new ButtonBuilder().setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new NewsScreen(new NewsListScreen().build(screen), news))).setTitle(Component.empty()).setStyle(GuiUtils.getSelected()).setSize(width, 40).setPosition(x, y));
        this.news = news;
        setMessage(Component.literal(news.title));
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getY() < guiGraphics.guiHeight() && !(getY() <= -getHeight())) {
            int x = 5;
            renderString(guiGraphics, news.title, getX() + x, getY() + 8);
            renderString(guiGraphics, news.description, getX() + x, getY() + height - 8 - AlinLib.MINECRAFT.font.lineHeight);
        }
    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, Component message, int x, int y, int color) {
        int k = this.getX() + x;
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
