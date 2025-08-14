package ru.pplh.mod.gui.screens.pages;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.api.PepeLandHelperAPI;
import ru.pplh.mod.api.components.project.Page;
import ru.pplh.mod.api.components.project.Project;
import ru.pplh.mod.gui.components.BannerWidget;
import ru.pplh.mod.gui.components.UserCard;
import ru.pplh.mod.gui.components.VerticalConfigureScrolWidget;
import ru.pplh.mod.gui.screens.message.ErrorScreen;
import ru.pplh.mod.utils.FollowManager;
import ru.pplh.mod.utils.MarkdownParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.kelcuprum.alinlib.gui.Icons.EXIT;

public class ProjectScreen extends Screen {
    public final Project project;
    public final Screen parent;
    public String content = "";
    public String mainContent = "";
    Page[] pages = new Page[0];
    public String season;

    public ProjectScreen(Screen screen, Project project, String season) {
        super(Component.translatable(season == null || season.isEmpty() ? "pplhelper.project" : "pplhelper.project.archived"));
        this.project = project;
        this.parent = screen;
        this.season = season;
        try {
            if(season == null || season.isEmpty()) {
                this.content = project.getContent();
                this.mainContent = this.content;
                pages = project.getPages();
            } else {
                this.content = PepeLandHelperAPI.getArchivedProjectContent(project.id, season);
                this.mainContent = this.content;
                pages = new Page[0];
            }
        } catch (Exception ex){
            AlinLib.MINECRAFT.setScreen(new ErrorScreen(ex, screen));
        }
    }

    @Override
    protected void init() {
        initSidebar();
        initContent();
    }
    // -=-=-=- Контент
    private ConfigureScrolWidget scroller;
    private List<AbstractWidget> widgets = new ArrayList<>();
    // -=-=-=- Страницы
    private VerticalConfigureScrolWidget scroller_pages;
    private List<AbstractWidget> widgets_pages = new ArrayList<>();
    // -=-=-=- Информация
    private ConfigureScrolWidget panel_scroller;
    private List<AbstractWidget> panel_widgets = new ArrayList<>();

    private final int maxSize = 550;
    private final int panelSize = 200;

    public void initSidebar(){
        int x = 10, y = 35;
        panel_widgets = new ArrayList<>();
        int size = panelSize-20;
        this.panel_scroller = addRenderableWidget(new ConfigureScrolWidget(x+size+1, y, 3, this.height - y - 6, Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            for (AbstractWidget widget : panel_widgets) {
                if (widget.visible) {
                    if (widget instanceof ImageWidget) widget.setWidth(Math.min(widget.getWidth(), size));
                    else widget.setWidth(size);
                    widget.setPosition(x, (y + (int) (scroller.innerHeight - scroller.scrollAmount())));
                    scroller.innerHeight += (widget.getHeight() + ((widget instanceof ImageWidget) ? 5 : (widget instanceof BannerWidget) ? 7 : 3));
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight -= 8;
        }));

        double scale = (double) size / maxSize;
        addRenderableWidget(new TextBuilder(title).setPosition(10, 10).setSize(size-20, 20).build());
        addRenderableWidget(new ButtonBuilder(CommonComponents.GUI_BACK, (s) -> onClose()).setSprite(EXIT).setPosition(10+size-20, 10).setSize(20, 20).build());
        if (project.banner != null && !project.banner.isEmpty())
            panel_widgets.add(new BannerWidget(x, -160, size, (int) (160 * scale), project.banner, String.format("project_banner_%s", project.id), Component.empty()));
        panel_widgets.add(new TextBuilder(Component.empty().append(Component.literal(project.title).setStyle(Style.EMPTY.withBold(true))).append(project.description.isEmpty() ? "" : "\n").append(project.description)).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(size, 20).build());
        panel_widgets.add(new TextBuilder(Component.translatable("pplhelper.project.creators", project.creators)).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(size, 20).build());
        panel_widgets.add(new TextBuilder(Component.translatable("pplhelper.project.state", project.state == Project.State.BUILT ? Component.translatable("pplhelper.project.state.built") : project.state == Project.State.BUILD ? Component.translatable("pplhelper.project.state.build") : Component.translatable("pplhelper.project.state.planned"))).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(size, 20).build());
        MutableComponent coord = Component.empty().append(Component.translatable("pplhelper.project.coordinates", project.world));
        if (project.coordinates$overworld != null && !project.coordinates$overworld.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.overworld")).append(": ").append(project.coordinates$overworld);
        if (project.coordinates$nether != null && !project.coordinates$nether.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.nether")).append(": ").append(project.coordinates$nether);
        if (project.coordinates$end != null && !project.coordinates$end.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.end")).append(": ").append(project.coordinates$end);
        TextBox msg = (TextBox) new TextBuilder(coord).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, y).setSize(size, 20).build();
        panel_widgets.add(msg);
        panel_widgets.add(new ButtonBuilder(Component.translatable((FollowManager.project == null || FollowManager.project.id != project.id) ? "pplhelper.project.follow" : "pplhelper.project.unfollow"), (s) -> {
            if(FollowManager.project == null || FollowManager.project.id != project.id) FollowManager.setCoordinates(project); else FollowManager.resetCoordinates();
            s.builder.setTitle(Component.translatable(FollowManager.project == null ? "pplhelper.project.follow" : "pplhelper.project.unfollow"));
        }).setPosition(x, y).setWidth(size).build());
        if(FabricLoader.getInstance().isModLoaded("litematica") && ((project.schematicEnable  && PepeLandHelper.playerInPPL()) || (PepeLandHelper.user != null && Objects.equals(PepeLandHelper.user.id, project.author)))){
            panel_widgets.add(new HorizontalRuleBuilder(Component.translatable("pplhelper.project.schematic")).setPosition(x, y).build());
            if(project.schematicEnable && PepeLandHelper.playerInPPL()){
                panel_widgets.add(new ButtonBuilder(Component.translatable("pplhelper.project.schematic.download"), (s) -> {
                    project.loadSchematic();
                }).setPosition(x, y).setWidth(size).build());
            }
            if(PepeLandHelper.user != null && Objects.equals(PepeLandHelper.user.id, project.author)){
                panel_widgets.add(new ButtonBuilder(Component.translatable("pplhelper.project.schematic.upload_short"), (s) -> {
                    PepeLandHelper.confirmLinkNow(this, String.format("https://pplh.ru/projects/%s/schematic", project.id));
                }).setPosition(x, y).setWidth(size).build());
            }
        }
        if((season == null || season.isEmpty()) && project.author != null) {
            panel_widgets.add(new HorizontalRuleBuilder().setTitle(Component.translatable("pplhelper.project.author")).build());
            panel_widgets.add(new UserCard(x, y, size, project.getAuthor(), true));
        }
        addWidgetsToScroller(panel_widgets, panel_scroller);
    }
    public void initContent() {
        widgets = new ArrayList<>();
        widgets_pages = new ArrayList<>();
        int size = Math.min(maxSize, width - 15 - panelSize);
        int x = (width - size - panelSize) / 2 + panelSize;
        int y = 35;
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(x + size + 1, y, 3, this.height - y - 6, Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            for (AbstractWidget widget : widgets) {
                if (widget.visible) {
                    if (widget instanceof ImageWidget) widget.setWidth(Math.min(widget.getWidth(), size));
                    else widget.setWidth(size);
                    widget.setPosition(x, (y + (int) (scroller.innerHeight - scroller.scrollAmount())));
                    scroller.innerHeight += (widget.getHeight() + ((widget instanceof ImageWidget) ? 5 : (widget instanceof BannerWidget) ? 7 : 3));
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight -= 8;
        }));
        this.scroller_pages = addRenderableWidget(new VerticalConfigureScrolWidget(x, y-4, size, 3, Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            for (AbstractWidget widget : widgets_pages) {
                if (widget.visible) {
                    widget.setPosition((x + (int) (scroller.innerHeight - scroller.scrollAmount())), y-25);
                    scroller.innerHeight += (widget.getWidth() + 5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight -= 13;
        }));
        Component description = Component.translatable(mainContent.isEmpty() ? "pplhelper.project.description.empty" : "pplhelper.project.description");
        if(pages.length == 0) addRenderableWidget(new TextBuilder(description).setPosition(x, 10).setSize(size, 20).build());
        else {
            widgets_pages.add(new ButtonBuilder(description, (s) -> {
                content = project.getContent();
                mainContent = content;
                rebuildContentWidgets();
            }).setWidth(12+minecraft.font.width(description)).build());
            for(Page page : pages){
                if(page != null) widgets_pages.add(new ButtonBuilder(Component.literal(page.name), (s) -> {
                    content = page.getContent();
                    rebuildContentWidgets();
                }).setWidth(12+minecraft.font.width(page.name)).build());
            }
        }
        addWidgetsToScroller(widgets_pages, scroller_pages);
        if(!content.isEmpty()) widgets.addAll(MarkdownParser.parse(content, x, size, String.format("project_%s_", project.id) + "%s", this));
        else widgets.add(new ImageWidget(x, 35, size, 20, GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/amogus.png"), 256, 32, true, Component.empty()));
        addWidgetsToScroller(widgets);
    }

    public void rebuildContentWidgets(){
        for(AbstractWidget widget : widgets) removeWidget(widget);
        initContent();
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets) {
        addWidgetsToScroller(widgets, this.scroller);
    }


    public void addWidgetsToScroller(AbstractWidget widget) {
       addWidgetsToScroller(widget, this.scroller);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets, ConfigureScrolWidget scroller) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget, scroller);
    }
    public void addWidgetsToScroller(AbstractWidget widget, ConfigureScrolWidget scroller) {
        widget.setY(-100);
        scroller.addWidget(widget);
        this.addWidget(widget);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets, VerticalConfigureScrolWidget scroller) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget, scroller);
    }
    public void addWidgetsToScroller(AbstractWidget widget, VerticalConfigureScrolWidget scroller) {
        widget.setY(-100);
        scroller.addWidget(widget);
        this.addWidget(widget);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        int size = Math.min(maxSize, width - 15 - panelSize);
        int x = (width - size - panelSize) / 2 + panelSize;
        guiGraphics.enableScissor(x, 10, x+size, 30);
        if (scroller_pages != null) for (AbstractWidget widget : scroller_pages.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
        guiGraphics.enableScissor(0, 35, width, this.height-5);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, i, j, f);
        if (panel_scroller != null) for (AbstractWidget widget : panel_scroller.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        int size = Math.min(maxSize, width - 15 - panelSize);
        int x = (width - size - panelSize) / 2 + panelSize;

        int panelX = 10, panel_size = panelSize - 10;
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller != null && scroller.widgets.contains(guiEventListener)) {
                if ((d >= x && d <= x + size) && e >= 30)
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
            } else if (panel_scroller != null && panel_scroller.widgets.contains(guiEventListener)) {
                if ((d >= panelX && d <= panelX + panel_size) && e >= 30)
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
            } else  if (scroller_pages != null && scroller_pages.widgets.contains(guiEventListener)) {
                if ((d >= x && d <= x + size) && e <= 30)
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
        int size = Math.min(maxSize, width - 15 - panelSize);
        int x = (width - size - panelSize) / 2 + panelSize;

        int maxY = 35;
        if(scroller != null) for(AbstractWidget widget : scroller.widgets) maxY = widget.getY()+widget.getHeight()+5;
        guiGraphics.fill(x - 5, 5, x + size + 5, Math.min(height-5, maxY), Colors.BLACK_ALPHA); // Затемнение

        int maxPanelY = 35;
        if(panel_scroller != null) for(AbstractWidget widget : panel_scroller.widgets) maxPanelY = widget.getY()+widget.getHeight()+5;
        guiGraphics.fill(5, 5, 5+panelSize-10, Math.min(height-5, maxPanelY), Colors.BLACK_ALPHA); // Затемнение

        if(pages.length == 0) guiGraphics.fill(x, 10, x + size, 30, Colors.BLACK_ALPHA);
        guiGraphics.fill(10, 10, 5 + panelSize - 35, 30, Colors.BLACK_ALPHA);
    }

    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && panel_scroller != null && mouseX < panelSize) scr = panel_scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        else if(!scr && scroller_pages != null && mouseY < 30) scr = scroller_pages.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        else if (!scr && scroller != null) scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return scr;
    }

    @Override
    public void tick() {
        if (scroller != null) scroller.onScroll.accept(scroller);
        if (panel_scroller != null) panel_scroller.onScroll.accept(panel_scroller);
        if (scroller_pages != null) scroller_pages.onScroll.accept(scroller_pages);
        super.tick();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == GLFW.GLFW_KEY_ESCAPE) {
            if (getFocused() != null && getFocused().isFocused()) {
                getFocused().setFocused(false);
                return true;
            }
        } else if (i == GLFW.GLFW_KEY_F5) {
            rebuildWidgets();
        }
        return super.keyPressed(i, j, k);
    }
}
