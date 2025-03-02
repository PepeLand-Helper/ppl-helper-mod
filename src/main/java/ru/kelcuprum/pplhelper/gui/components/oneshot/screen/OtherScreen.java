package ru.kelcuprum.pplhelper.gui.components.oneshot.screen;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.gui.components.oneshot.OneShotTitle;
import ru.kelcuprum.pplhelper.gui.components.oneshot.OneShotTitleButton;

import java.util.Objects;

import static ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder.ALIGN.LEFT;

public class OtherScreen extends Screen {
    protected final Screen parent;

    public OtherScreen(Screen parent) {
        super(Component.translatable("pwshot.other"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int bHeight = font.lineHeight + 4;
        int bHeight2 = (bHeight + 3);
        int y = 43 + 24;
        int bWidth = font.width("...");
        Component[] texts = {
                Component.translatable("pwshot.mods"),
                Component.translatable("gui.advancements"),
                Component.translatable("gui.stats"),
                Component.translatable("options.language"),
                Component.translatable("menu.shareToLan"),
                Component.translatable("pwshot.options")
        };
        for (Component text : texts) {
            int i = font.width(text) + 5;
            bWidth = Math.max(bWidth, i);
        }

        addRenderableWidget(new OneShotTitle(20, y - 34, width - 30, font.lineHeight * 3, getTitle()));
        addRenderableWidget(new OneShotTitleButton(30, y + 10, bWidth, bHeight, texts[0], (s) -> {
            assert this.minecraft.player != null;
            this.minecraft.setScreen(ModMenuApi.createModsScreen(this));
        }));
        y += bHeight2;

        addRenderableWidget(new OneShotTitleButton(30, y + 10, bWidth, bHeight, texts[1], (s) -> this.minecraft.setScreen(new AdvancementsScreen(Objects.requireNonNull(this.minecraft.getConnection()).getAdvancements()))));
        y += bHeight2;

        addRenderableWidget(new OneShotTitleButton(30, y + 10, bWidth, bHeight, texts[2], (s) -> {
            assert this.minecraft.player != null;
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
        }));
        y += bHeight2;

        addRenderableWidget(new OneShotTitleButton(30, y + 10, bWidth, bHeight, texts[3], (s) -> {
            assert this.minecraft.player != null;
            this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
        }));
        y += bHeight2;

        if (this.minecraft.hasSingleplayerServer() && !Objects.requireNonNull(this.minecraft.getSingleplayerServer()).isPublished()) {
            addRenderableWidget(new OneShotTitleButton(30, y + 10, bWidth, bHeight, texts[4], (s) -> {
                assert this.minecraft.player != null;
                this.minecraft.setScreen(new ShareToLanScreen(this));
            }));
            y += bHeight2;
        }
        addRenderableWidget(new OneShotTitleButton(30, y + 10, bWidth, bHeight, texts[5], (s) -> {
            assert this.minecraft.player != null;
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));
        y += bHeight2;
    }

    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.fill(0, 0, width, height, 0x7f000000);
    }
}
