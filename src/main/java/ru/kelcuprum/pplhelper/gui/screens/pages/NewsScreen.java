package ru.kelcuprum.pplhelper.gui.screens.pages;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.api.components.News;
import ru.kelcuprum.pplhelper.gui.components.BannerWidget;
import ru.kelcuprum.pplhelper.gui.components.UserCard;
import ru.kelcuprum.pplhelper.gui.components.VerticalConfigureScrolWidget;
import ru.kelcuprum.pplhelper.utils.MarkdownParser;

import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.alinlib.gui.Icons.EXIT;

public class NewsScreen extends Screen {
    public final News news;
    public final Screen parent;
    public String content = "";
    public NewsScreen(Screen screen, News news) {
        super(Component.translatable("pplhelper.news.page", news.title));
        this.news = news;
        this.parent = screen;
        this.content = news.getContent();
    }

    @Override
    protected void init() {
        initContent();
        initSidebar();
    }

    private ConfigureScrolWidget scroller;
    private List<AbstractWidget> widgets = new ArrayList<>();
    // -=-=-=- Информация
    private ConfigureScrolWidget panel_scroller;
    private List<AbstractWidget> panel_widgets = new ArrayList<>();
    private final int maxSize = 550;
    private final int panelSize = 200;

    public void initContent(){
        widgets = new ArrayList<>();
        int size = Math.min(maxSize, width - 15 - panelSize);
        int x = (width - size - panelSize) / 2 + panelSize;
        int y = 6;
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(x + size + 1, y, 3, this.height - y - 6, Component.empty(), scroller -> {
            scroller.innerHeight = 4;
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

        widgets.addAll(MarkdownParser.parse(content, x, size, String.format("news_%s_",news.id)+"%s", this));
        addWidgetsToScroller(widgets);
    }


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
        if (news.banner != null && !news.banner.isEmpty())
            panel_widgets.add(new BannerWidget(x, -160, size, (int) (160 * scale), news.banner, String.format("news_banner_%s", news.id), Component.empty()));
        panel_widgets.add(new TextBuilder(Component.empty().append(Component.literal(news.title).setStyle(Style.EMPTY.withBold(true))).append(news.description.isEmpty() ? "" : "\n").append(news.description)).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(size, 20).build());
        panel_widgets.add(new HorizontalRuleBuilder().setTitle(Component.translatable("pplhelper.project.author")).build());
        panel_widgets.add(new UserCard(x, y, size, news.getAuthor(), true));
        addWidgetsToScroller(panel_widgets, panel_scroller);
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
        guiGraphics.enableScissor(0, 35, width, this.height-5);
        if (panel_scroller != null) for (AbstractWidget widget : panel_scroller.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();

        guiGraphics.enableScissor(0, 5, width, this.height-5);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, i, j, f);
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
                if ((d >= x && d <= x + size) && e >= 5)
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

//        guiGraphics.fill(x, 10, x + size, 30, Colors.BLACK_ALPHA);
        guiGraphics.fill(10, 10, 5 + panelSize - 35, 30, Colors.BLACK_ALPHA);
    }

    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && panel_scroller != null && mouseX < panelSize) scr = panel_scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        else if (!scr && scroller != null) scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return scr;
    }

    @Override
    public void tick() {
        if (scroller != null) scroller.onScroll.accept(scroller);
        if (panel_scroller != null) panel_scroller.onScroll.accept(panel_scroller);
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
