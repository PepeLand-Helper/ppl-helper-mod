package ru.kelcuprum.pplhelper.gui.screens.project;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.text.MessageBox;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.TextureHelper;
import ru.kelcuprum.pplhelper.gui.components.Blockquote;
import ru.kelcuprum.pplhelper.gui.components.HorizontalRule;
import ru.kelcuprum.pplhelper.gui.components.ScaledTextBox;

import java.util.ArrayList;
import java.util.List;

public class ProjectScreen extends Screen {
    public final Project project;
    public final Screen parent;
    public ProjectScreen(Screen screen, Project project) {
        super(Component.translatable("pplhelper.project"));
        this.project = project;
        this.parent = screen;
    }

    public int maxY = 35;

    @Override
    protected void init() {
        initPanel();
        initContent();
    }

    protected void initPanel() {
        int x = 10;
        int size = 200;
        addRenderableWidget(new TextBox(x, 5, size-25, 20, Component.translatable("pplhelper.project"), true));
        addRenderableWidget(new ButtonBuilder(Component.literal("x"), (s)->onClose()).setPosition( x+size-15, 5).setWidth(20).build()); //, 20, 20,
        int y = 35;
        int iconSize = project.icon != null && !project.icon.isEmpty() ? 41 : 0;
        if(project.icon != null && !project.icon.isEmpty()) addRenderableWidget(new ImageWidget(x, y, 36, 36, TextureHelper.getTexture(project.icon, String.format("project_%s", project.id)), 36, 36, Component.empty()));
        addRenderableWidget(new TextBox(x + iconSize, y, size - iconSize, 18, Component.literal(project.title), false));
        addRenderableWidget(new TextBox(x + iconSize, y + 18, size - iconSize, 18, Component.literal(project.creators), false));
        y += 41;
        if(project.description != null && !project.description.isEmpty()) {
            MessageBox msg = new Blockquote(x, y, size, 20, Component.literal(project.description), false);
            addRenderableWidget(msg);
            y += (5 + msg.getHeight());
        }
        MutableComponent coord = Component.empty().append(Component.translatable("pplhelper.project.coordinates"));
        if(project.coordinates$overworld != null && !project.coordinates$overworld.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.overworld")).append(": ").append(project.coordinates$overworld);
        if(project.coordinates$nether != null && !project.coordinates$nether.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.nether")).append(": ").append(project.coordinates$nether);
        if(project.coordinates$end != null && !project.coordinates$end.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.end")).append(": ").append(project.coordinates$end);
        MessageBox msg = new MessageBox(x, y, size, 20, coord, false);
        addRenderableWidget(msg);
        y += (5 + msg.getHeight());

        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.project.web"), (s) -> PepelandHelper.confirmLinkNow(this, String.format("https://h.pplmods.ru/projects/%s", project.id))).setPosition(x, y).setWidth(size).build());
        y+=25;
        if(PepelandHelper.isInstalledABI) {
            addRenderableWidget(new ButtonBuilder(Component.translatable((PepelandHelper.selectedProject == null || PepelandHelper.selectedProject.id != project.id) ? "pplhelper.project.abi" : "pplhelper.project.abi.unfollow"), (s) -> {
                PepelandHelper.selectedProject = (PepelandHelper.selectedProject == null || PepelandHelper.selectedProject.id != project.id) ? project : null;
                s.builder.setTitle(Component.translatable(PepelandHelper.selectedProject == null ? "pplhelper.project.abi" : "pplhelper.project.abi.unfollow"));
            }).setPosition(x, y).setWidth(size).build());
            y += 25;
        }

        maxY = y;
    }

    private ConfigureScrolWidget scroller;
    private List<AbstractWidget> widgets = new ArrayList<>();
    public void initContent(){
        widgets = new ArrayList<>();
        int x = 220;
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(this.width - 8, 0, 4, this.height, Component.empty(), scroller -> {
            scroller.innerHeight = 15;
            for(AbstractWidget widget : widgets){
                if(widget.visible){
                    widget.setWidth(width-225);
                    widget.setPosition(x, ((int) (scroller.innerHeight - scroller.scrollAmount())));
                    scroller.innerHeight += (widget.getHeight()+5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight-=8;
        }));
        widgets.addAll(parseMarkdown());
        addRenderableWidgets(widgets);
    }
    protected void addRenderableWidgets(@NotNull List<AbstractWidget> widgets) {
        for (AbstractWidget widget : widgets) {
            this.addRenderableWidget(widget);
        }
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        guiGraphics.fill(0, 0, width, height, Colors.BLACK_ALPHA); // Затемнение

        guiGraphics.fill(5, 5, 190, 25, Colors.BLACK_ALPHA);
        guiGraphics.fill(5, 30, 215, maxY, Colors.BLACK_ALPHA);
    }
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && scroller != null) scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return scr;
    }
    @Override
    public void tick(){
        if(scroller != null) scroller.onScroll.accept(scroller);
        super.tick();
    }

    public List<AbstractWidget> parseMarkdown(){
        List<AbstractWidget> widgets = new ArrayList<>();
        String[] strings = parse(project.content, false).split("\n");
        boolean lastIsPlain = false;
        boolean lastIsBlockQuote = false;
        String plain = "";
        String blockquote = "";
        for(String string : strings){
            if(string.startsWith("<hr") && string.endsWith(">")) {
                if(lastIsPlain){
                    lastIsPlain = false;
                    plain = parse(plain, true);
                    widgets.add(new MessageBox(225, -40, width-230, 20, Component.literal(plain.substring(0, plain.length()-(plain.endsWith("\n") ? 1 : 0))), false));
                    plain = "";
                }
                if(lastIsBlockQuote){
                    lastIsBlockQuote = false;
                    blockquote = parse(blockquote, true);
                    widgets.add(new Blockquote(225, -40, width-230, 20, Component.literal(blockquote.substring(0, blockquote.length()-(blockquote.endsWith("\n") ? 1 : 0))), false));
                    blockquote = "";
                }
                widgets.add(new HorizontalRule(225, -40, width-230));
            } else if(string.startsWith("#")){
                if(lastIsPlain){
                    lastIsPlain = false;
                    plain = parse(plain, true);
                    widgets.add(new MessageBox(225, -40, width-230, 20, Component.literal(plain.substring(0, plain.length()-(plain.endsWith("\n") ? 1 : 0))), false));
                    plain = "";
                }
                if(lastIsBlockQuote){
                    lastIsBlockQuote = false;
                    blockquote = parse(blockquote, true);
                    widgets.add(new Blockquote(225, -40, width-230, 20, Component.literal(blockquote.substring(0, blockquote.length()-(blockquote.endsWith("\n") ? 1 : 0))), false));
                    blockquote = "";
                }
                int j = 0;
                for(int i = 0; i<string.length() && string.split("")[i].equals("#"); i++) j = i;
                string = parse(string, true);
                widgets.add(new ScaledTextBox(225, -40, width-230, AlinLib.MINECRAFT.font.lineHeight+2, Component.literal(string.substring(j+(string.contains("# ") ? 1 : 0))), false, 1.5F-((float) j /6)));
            } else if(string.startsWith(">")) {
                if(lastIsPlain){
                    lastIsPlain = false;
                    plain = parse(plain, true);
                    widgets.add(new MessageBox(225, -40, width-230, 20, Component.literal(plain.substring(0, plain.length()-(plain.endsWith("\n") ? 1 : 0))), false));
                    plain = "";
                }
                if(!lastIsBlockQuote) lastIsBlockQuote = true;
                string = string.substring(string.contains("> ") ? 2 : 1);
                blockquote += string += "\n";
            } else {
                if(lastIsBlockQuote){
                    lastIsBlockQuote = false;
                    blockquote = parse(blockquote, true);
                    widgets.add(new Blockquote(225, -40, width-230, 20, Component.literal(blockquote.substring(0, blockquote.length()-(blockquote.endsWith("\n") ? 1 : 0))), false));
                    blockquote = "";
                }
                if(!lastIsPlain) lastIsPlain = true;
                if(!string.isBlank()) plain += string += "\n";
            }
        }
        if(lastIsPlain) widgets.add(new MessageBox(225, -40, width-230, 20, Component.literal(parse(plain, true).substring(0, parse(plain, true).length()-(parse(plain, true).endsWith("\n") ? 1 : 0))), false));
        if(lastIsBlockQuote) widgets.add(new Blockquote(225, -40, width-230, 20, Component.literal(parse(blockquote, true).substring(0, parse(blockquote, true).length()-(parse(blockquote, true).endsWith("\n") ? 1 : 0))), false));
        return widgets;
    }

    public String parse(String string, boolean isLine){
        String ret = string.replaceAll("\\*\\*(.+?)\\*\\*(?!\\*)", "§l$1§r")
                .replaceAll("\\*(.+?)\\*(?!\\*)", "§o$1§r")
                .replaceAll("__(.+?)__(?!_)", "§n$1§r")
                .replaceAll("_(.+?)_(?!_)", "§o$1§r")
                .replaceAll("~~(.+?)~~(?!~)", "§m$1§r")
                .replaceAll("\\|\\|(.+?)\\|\\|(?!\\|)", "§k$1§r")
                .replace("<br>\n", "\n")
                .replace("\r", "");
        if(isLine){
            ret = ret.replace("<br>", "\n");
        }
        return ret;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == GLFW.GLFW_KEY_ESCAPE) {
            if (getFocused() != null && getFocused().isFocused()) {
                getFocused().setFocused(false);
                return true;
            }
        }
        return super.keyPressed(i, j, k);
    }
}
