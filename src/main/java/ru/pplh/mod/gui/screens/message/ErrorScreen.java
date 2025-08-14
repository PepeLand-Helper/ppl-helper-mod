package ru.pplh.mod.gui.screens.message;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
//#if MC >= 12106
import net.minecraft.client.renderer.RenderPipelines;
//#endif
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.apache.logging.log4j.Level;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.pplh.mod.gui.screens.message.component.DescriptionBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.kelcuprum.alinlib.gui.Colors.BLACK_ALPHA;

public class ErrorScreen extends Screen {
    public Screen parent;
    public Exception error;
    public ErrorScreen(Exception ex, Screen screen){
        this(screen);
        this.error = ex;
    }
    public ErrorScreen(Screen screen) {
        super(Component.translatable("pplhelper.error"));
        this.parent = screen;
    }

    DescriptionBox descriptionBox;
    TextBox description;

    @Override
    protected void init() {
        // Левая панель
        if(error != null){
            addRenderableWidget(new TextBuilder(Component.translatable("pplhelper.error.stacktrace")).setPosition(7, 7).setSize(176, 20).build());

            StringBuilder builder = new StringBuilder(String.format("%s > %s\n", error.getClass().getName(), error.getMessage()));
            for(StackTraceElement stack : error.getStackTrace()) builder.append(String.format("\n%s:%s - %s;", stack.getFileName(), stack.getLineNumber(), stack.getMethodName()));
            descriptionBox = new DescriptionBox(7, 29, 176, height-86, Component.empty());
            descriptionBox.setDescription(Component.literal(builder.toString()));

            addRenderableWidget(descriptionBox);

            addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.error.save"), (s) -> {
                saveCrashReport();
            }).setPosition(5, height-50).setSize(180, 20).build());
            addRenderableWidget(new ButtonBuilder(CommonComponents.GUI_BACK, (s) -> {
                onClose();
            }).setPosition(5, height-25).setSize(180, 20).build());

            // Описание
            int maxWidth = 0;
            for(FormattedCharSequence arg : AlinLib.MINECRAFT.font.split(Component.translatable("pplhelper.error.description"), Math.min(width-195, 500)))
                maxWidth = Math.max(maxWidth, AlinLib.MINECRAFT.font.width(arg));
            description = new TextBuilder(Component.translatable("pplhelper.error.description")).setType(TextBuilder.TYPE.MESSAGE).build();
            description.setWidth(maxWidth);

            description.setPosition(195 + ((width-200) / 2 - description.getWidth() / 2), height/2 - description.getHeight() / 2);
            addRenderableWidget(description);
        } else {
            addRenderableWidget(new TextBuilder(Component.literal("x"), (s) -> onClose()).setPosition(width-15, 5).setSize(10, 10).build());
            int maxWidth = 0;
            for(FormattedCharSequence arg : AlinLib.MINECRAFT.font.split(Component.translatable("pplhelper.error.description"), Math.min(width-195, 500)))
                maxWidth = Math.max(maxWidth, AlinLib.MINECRAFT.font.width(arg));
            description = new TextBuilder(Component.translatable("pplhelper.error.description")).setType(TextBuilder.TYPE.MESSAGE).build();
            description.setWidth(maxWidth);

            description.setPosition((width) / 2 - description.getWidth() / 2, height/2 - description.getHeight() / 2);
            addRenderableWidget(description);
        }
    }

    public void saveCrashReport(){
        StringBuilder builder = new StringBuilder(String.format("%s > %s\n", error.getClass().getName(), error.getMessage()));
        for(StackTraceElement stack : error.getStackTrace()) builder.append(String.format("\n%s (%s:%s) - %s", stack.getClassName(), stack.getFileName(), stack.getLineNumber(), stack.getMethodName()));
        Path configFile = Path.of(String.format("config/pplhelper/crashreports/%s.txt", System.currentTimeMillis()));
        try {
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, builder.toString());

            Util.getPlatform().openPath(configFile);
            onClose();
        } catch (IOException e) {
            AlinLib.LOG.log(e.getLocalizedMessage(), Level.ERROR);
        }
    }
    public double startFireX = Math.random();
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        int fireSize = (int) Math.max(32, height * 0.35);
        int startX = (int) -(fireSize*startFireX);
        for(int l = 0; true; l++){
            if(fireSize*l+startX > width) break;
            guiGraphics.blitSprite(
                    //#if MC >= 12106
                    RenderPipelines.GUI_TEXTURED,
                    //#elseif MC >= 12102
                    //$$ RenderType::guiTextured,
                    //#endif
                    GuiUtils.getResourceLocation("pplhelper", "error/fire_0"), fireSize*l+startX, height-fireSize, fireSize, fireSize);
        }
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x7f245965, 0x7F9f1b46);

        // Левая панель
        if(error != null) guiGraphics.fill(5, 5, 185, height-55, BLACK_ALPHA);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (mouseX <= descriptionBox.getRight()) {
            if (descriptionBox.visible && (mouseX >= descriptionBox.getX() && mouseX <= descriptionBox.getRight()) && (mouseY >= 40 && mouseY <= height - 30)) {
                scr = descriptionBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        }
        return scr;
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }
}
