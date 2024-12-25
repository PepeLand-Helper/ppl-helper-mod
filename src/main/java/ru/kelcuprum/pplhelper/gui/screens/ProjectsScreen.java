package ru.kelcuprum.pplhelper.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.components.ProjectButton;
import ru.kelcuprum.pplhelper.gui.message.ErrorScreen;

import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;
import static ru.kelcuprum.alinlib.gui.Icons.SEARCH;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.WEB;

public class ProjectsScreen extends Screen {
    public final Screen parent;
    public ProjectsScreen(Screen screen) {
        super(Component.translatable("pplhelper.projects"));
        this.parent = screen;
    }

    private ConfigureScrolWidget scroller;
    private List<AbstractWidget> widgets = new ArrayList<>();
    private final int maxSize = 400;
    private String query = "";
    private int world = 0;
    private List<Project> lastProjects;

    @Override
    protected void init() {
        try {
            widgets = new ArrayList<>();
            int size = Math.min(maxSize, width - 10);
            int x = (width - size) / 2;
            int y = 5;
            addRenderableWidget(new ButtonBuilder(Component.literal("x"), (s) -> onClose()).setPosition(x + size - 20, 5).setWidth(20).build()); //, 20, 20,
            addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.project.web"), (s) -> PepelandHelper.confirmLinkNow(this, "https://h.pplmods.ru/projects")).setSprite(WEB).setPosition(x, 5).setWidth(20).build()); //, 20, 20,
            addRenderableWidget(new TextBuilder(title).setPosition(x+25, 5).setSize(size-50, 20).build());
            y += 25;
            int searchSize = (size - 30) / 2;
            addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.news.search"), (s) -> query = s).setValue(query).setPosition(x, y).setWidth(searchSize).build());
            String[] worlds = PepeLandHelperAPI.getWorlds();
            addRenderableWidget(new SelectorBuilder(Component.translatable("pplhelper.project.world"), (s) -> world = s.getPosition()).setList(worlds).setValue(world).setPosition(x + 5 + searchSize, y).setWidth(searchSize).build());
            addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.news.find"), (s) -> search()).setSprite(SEARCH).setPosition(x + size - 20, y).setWidth(20).build());
            y += 25;

            int finalY = y;
            this.scroller = addRenderableWidget(new ConfigureScrolWidget(x + size+1, y, 4, this.height - y, Component.empty(), scroller -> {
                scroller.innerHeight = 0;
                for (AbstractWidget widget : widgets) {
                    if (widget.visible) {
                        widget.setWidth(size);
                        widget.setPosition(x, (finalY + (int) (scroller.innerHeight - scroller.scrollAmount())));
                        scroller.innerHeight += (widget.getHeight() + ((widget instanceof ImageWidget) ? 5 : 3));
                    } else widget.setY(-widget.getHeight());
                }
                scroller.innerHeight -= 8;
            }));
            List<Project> projects = lastProjects == null ? PepeLandHelperAPI.getProjects(query, worlds[world]) : lastProjects;
            lastProjects = projects;
            if (projects.isEmpty()) {
                widgets.add(new TextBuilder(Component.translatable("pplhelper.news.not_found")).setType(TextBuilder.TYPE.MESSAGE).setAlign(TextBuilder.ALIGN.CENTER).setPosition(x, 55).setSize(size, 20).build());
                widgets.add(new ImageWidget(x, 55, size, 20, GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/ozon.png"), 640, 360, true, Component.empty()));
            } else for (Project project : projects)
                widgets.add(new ProjectButton(0, -40, DEFAULT_WIDTH(), project, this));
            addWidgetsToScroller(widgets);
        } catch(Exception ex){
            AlinLib.MINECRAFT.setScreen(new ErrorScreen(ex, parent));
        }
    }

    private void search(){
        lastProjects = null;
        rebuildWidgets();
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget);
    }

    public void addWidgetsToScroller(AbstractWidget widget) {
        this.scroller.addWidget(widget);
        this.addWidget(widget);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.enableScissor(0, 55, width, this.height);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets)
            widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        int size = Math.min(maxSize, width-10);
        int x = (width-size) / 2;
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller != null && scroller.widgets.contains(guiEventListener)) {
                if ((d >= x && d <= x + size) && e >= 55)
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
            } else if (guiEventListener.mouseClicked(d, e, i)) {
                st = false;
                selected = guiEventListener;
                break;
            }
        }

        this.setFocused(selected);
        if (i == 0)
            this.setDragging(true);

        return st;
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        int size = Math.min(maxSize, width-10);
        int x = (width-size) / 2;
        guiGraphics.fill(x-5, 0, x+size+5, height, Colors.BLACK_ALPHA); // Затемнение

        guiGraphics.fill(x+25, 5, x+size-25, 25, Colors.BLACK_ALPHA);
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

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == GLFW.GLFW_KEY_ESCAPE) {
            if (getFocused() != null && getFocused().isFocused()) {
                getFocused().setFocused(false);
                return true;
            }
        }
        if(i == GLFW.GLFW_KEY_ENTER){
            if (getFocused() != null && getFocused().isFocused() && getFocused() instanceof EditBox) {
                search();
                return true;
            }

        }
        return super.keyPressed(i, j, k);
    }
}
