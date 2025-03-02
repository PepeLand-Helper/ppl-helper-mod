package ru.kelcuprum.pplhelper.gui.screens.pages;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.components.BannerWidget;
import ru.kelcuprum.pplhelper.gui.components.ScaledTextBox;
import ru.kelcuprum.pplhelper.gui.components.UserCard;
import ru.kelcuprum.pplhelper.utils.MarkdownParser;

import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.WEB;

public class ProjectScreen extends Screen {
    public final Project project;
    public final Screen parent;

    public ProjectScreen(Screen screen, Project project) {
        super(Component.translatable("pplhelper.project", project.title));
        this.project = project;
        this.parent = screen;
    }

    @Override
    protected void init() {
        initContent();
    }

    private ConfigureScrolWidget scroller;
    private List<AbstractWidget> widgets = new ArrayList<>();
    private final int maxSize = 400;

    public void initContent() {
        widgets = new ArrayList<>();
        int size = Math.min(maxSize, width - 10);
        double scale = (double) size / maxSize;
        int x = (width - size) / 2;
        int y = 30;
        addRenderableWidget(new ButtonBuilder(Component.literal("x"), (s) -> onClose()).setPosition(x + size - 20, 5).setWidth(20).build()); //, 20, 20,
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.project.web"), (s) -> PepelandHelper.confirmLinkNow(this, String.format("https://pplh.ru/projects/%s", project.id))).setSprite(WEB).setPosition(x, 5).setWidth(20).build()); //, 20, 20,
        addRenderableWidget(new TextBuilder(title).setPosition(x + 25, 5).setSize(size - 50, 20).build());
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(x + size + 1, y, 4, this.height - y, Component.empty(), scroller -> {
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
        if (project.banner != null && !project.banner.isEmpty())
            widgets.add(new BannerWidget(x, -160, size, (int) (160 * scale), project.banner, String.format("project_banner_%s", project.id), Component.empty()));
        widgets.add(new ScaledTextBox(x, -40, size, 12, Component.literal(project.title), true, 1.5f));
        widgets.add(new TextBuilder(Component.literal(project.description)).setType(TextBuilder.TYPE.MESSAGE).setAlign(TextBuilder.ALIGN.CENTER).setPosition(x, -40).setSize(size, 20).build());
        widgets.add(new TextBuilder(Component.translatable("pplhelper.project.creators", project.creators)).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(size, 20).build());
        MutableComponent coord = Component.empty().append(Component.translatable("pplhelper.project.coordinates", project.world));
        if (project.coordinates$overworld != null && !project.coordinates$overworld.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.overworld")).append(": ").append(project.coordinates$overworld);
        if (project.coordinates$nether != null && !project.coordinates$nether.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.nether")).append(": ").append(project.coordinates$nether);
        if (project.coordinates$end != null && !project.coordinates$end.isEmpty())
            coord.append("\n").append(Component.translatable("pplhelper.project.coordinates.end")).append(": ").append(project.coordinates$end);
        TextBox msg = (TextBox) new TextBuilder(coord).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, y).setSize(size, 20).build();
        widgets.add(msg);
        widgets.add(new ButtonBuilder(Component.translatable((PepelandHelper.selectedProject == null || PepelandHelper.selectedProject.id != project.id) ? "pplhelper.project.follow" : "pplhelper.project.unfollow"), (s) -> {
            PepelandHelper.selectedProject = (PepelandHelper.selectedProject == null || PepelandHelper.selectedProject.id != project.id) ? project : null;
            s.builder.setTitle(Component.translatable(PepelandHelper.selectedProject == null ? "pplhelper.project.follow" : "pplhelper.project.unfollow"));
        }).setPosition(x, y).setWidth(size).build());
        widgets.add(new HorizontalRuleBuilder().setPosition(x, y).build());
        widgets.addAll(MarkdownParser.parse(project.getContent(), x, size, String.format("project_%s_", project.id) + "%s", this));
        widgets.add(new HorizontalRuleBuilder().setTitle(Component.translatable("pplhelper.project.author")).build());
        widgets.add(new UserCard(x, y, size, project.getAuthor(), true));
        addWidgetsToScroller(widgets);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget);
    }

    int yC = 30;

    public void addWidgetsToScroller(AbstractWidget widget) {
        widget.setY(yC);
        yC += 5 + widget.getHeight();
        this.scroller.addWidget(widget);
        this.addWidget(widget);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        int size = Math.min(maxSize, width - 10);
        int x = (width - size) / 2;
        guiGraphics.enableScissor(0, 30, width, this.height);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        int size = Math.min(maxSize, width - 10);
        int x = (width - size) / 2;
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
        int size = Math.min(maxSize, width - 10);
        int x = (width - size) / 2;
        guiGraphics.fill(x - 5, 0, x + size + 5, height, Colors.BLACK_ALPHA); // Затемнение

        guiGraphics.fill(x + 25, 5, x + size - 25, 25, Colors.BLACK_ALPHA);
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
    public void tick() {
        if (scroller != null) scroller.onScroll.accept(scroller);
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
