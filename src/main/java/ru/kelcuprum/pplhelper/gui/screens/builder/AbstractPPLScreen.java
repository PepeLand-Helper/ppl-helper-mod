package ru.kelcuprum.pplhelper.gui.screens.builder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.screens.ThanksScreen;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.gui.configs.TestConfigScreen;

import java.util.List;

import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.WHITE_PEPE;

public class AbstractPPLScreen extends Screen {
    public ConfigureScrolWidget scroller;
    public ConfigureScrolWidget scroller_panel;
    public final ScreenBuilder builder;
    public AbstractPPLScreen(ScreenBuilder builder){
        super(builder.getTitle());
        this.builder = builder;
    }

    public void init(){
        initPanel();
        initContent();
    }

    public int panelY = 45;
    public int maxPanelY = panelY;
    public int contentY = 35;
    public int maxContentY = contentY;

    public void initPanel(){
        int heigthScroller = 45;
        int x = getPanelX();
        int y = panelY;
        for (AbstractWidget widget : builder.panelWidgets) {
            widget.setPosition(getPanelX(), y);
            heigthScroller+=(widget.getHeight()+5);
            y+=(widget.getHeight()+5);
        }
        this.scroller_panel = addRenderableWidget(new ConfigureScrolWidget(x+getPanelWidth()+1, panelY, 4, Math.min(y-panelY, height-5-panelY), Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            for (AbstractWidget widget : builder.panelWidgets) {
                if (widget.visible) {
                    widget.setPosition(x,panelY+(int) (scroller.innerHeight - scroller.scrollAmount()));
                    scroller.innerHeight += (widget.getHeight() + 5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight-=8;
        }));
        maxPanelY = Math.min(heigthScroller, height-5);
        addRenderableWidgets$scroller(scroller_panel, builder.panelWidgets);
    }
    public void initContent(){
        int x = getX();
        int y = contentY-25;
        addRenderableWidget(new TextBuilder(builder.title).setPosition(x, y).setSize(getFactWidth()-25, 20).build());
        ButtonBuilder exit = new ButtonBuilder(Component.literal("x")).setOnPress((s) -> onClose()).setPosition(x+getFactWidth()-20, y).setWidth(20);
        addRenderableWidget(exit.build());
        y+=25;
        contentY = y;
        for (AbstractWidget widget : builder.widgets) {
            widget.setWidth(getFactWidth());
            widget.setPosition(x, y);
            y+=(widget.getHeight()+5);
        }
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(getX()+getFactWidth()+1, contentY, 4, Math.min(y-contentY, height-5-contentY), Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            CategoryBox lastCategory = null;
            for (AbstractWidget widget : builder.widgets) {
                if (widget.visible) {
                    if (widget instanceof CategoryBox) {
                        if (lastCategory != widget && ((CategoryBox) widget).getState())
                            lastCategory = (CategoryBox) widget;
                    }
                    if (lastCategory != null && !(widget instanceof CategoryBox)) {
                        if (!lastCategory.values.contains(widget)) {
                            scroller.innerHeight += 6;
                            lastCategory.setRenderLine(true);
                            lastCategory = null;
                        }
                    }
                    widget.setY(contentY+(int) (scroller.innerHeight - scroller.scrollAmount()));
                    scroller.innerHeight += (widget.getHeight() + 5);
                } else widget.setY(-widget.getHeight());
            }
            maxContentY = Math.min(scroller.innerHeight+contentY, height-5);
            scroller.innerHeight -= 8;
        }));
        addRenderableWidgets$scroller(scroller, builder.widgets);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        // --- Content
        guiGraphics.fill(getX()-5, 5, getX()+getFactWidth()+5, maxContentY, Colors.BLACK_ALPHA);
        guiGraphics.fill(getX(), 10, getX()+getFactWidth()-25, 30, Colors.BLACK_ALPHA);

        // --- Panel
        guiGraphics.fill(getPanelX()-5, 5, getPanelX()+getPanelWidth()+5, 35, Colors.BLACK_ALPHA);
        guiGraphics.blit(RenderType::guiTextured, WHITE_PEPE, getPanelX(), 10, 0f, 0f, 20, 20, 20, 20);
        guiGraphics.fill(getPanelX()-5, 40, getPanelX()+getPanelWidth()+5, maxPanelY, Colors.BLACK_ALPHA);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if(maxPanelY == height-5) guiGraphics.enableScissor(0, panelY, width, maxPanelY-5);
        if (scroller_panel != null) for (AbstractWidget widget : scroller_panel.widgets) widget.render(guiGraphics, mouseX, mouseY, partialTicks);
        if(maxPanelY == height-5) guiGraphics.disableScissor();

        if(maxContentY == height-5) guiGraphics.enableScissor(0, contentY, width, maxContentY-5);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, mouseX, mouseY, partialTicks);
        if(maxContentY == height-5) guiGraphics.disableScissor();
    }

    // --- Клики

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller_panel != null && scroller_panel.widgets.contains(guiEventListener)) {
                if (e >= panelY && e <= maxPanelY) {
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
                }
            } else if (scroller != null && scroller.widgets.contains(guiEventListener)) {
                if (e >= contentY && e <= maxContentY) {
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
                }
            } else if (guiEventListener.mouseClicked(d, e, i)) {
                st = false;
                selected = guiEventListener;
                break;
            }
        }

        this.setFocused(selected);
        if (i == 0) {
            this.setDragging(true);
        }

        return st;
    }

    // --- Скролл

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (mouseX <= getPanelX()+getPanelWidth()) {
            if (!scr && scroller_panel != null) {
                scr = scroller_panel.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        } else {
            if (!scr && scroller != null) {
                scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        }
        return scr;
    }

    // --- Добавление элементов

    protected void addRenderableWidgets(@NotNull List<AbstractWidget> widgets){
        for(AbstractWidget widget : widgets)
            addRenderableWidget(widget);
    }

    protected void addRenderableWidgets$scroller(ConfigureScrolWidget scroller, @NotNull List<AbstractWidget> widgets){
        scroller.addWidgets(widgets);
        for(AbstractWidget widget : widgets) addWidget(widget);
    }
    protected void addRenderableWidgets$scroller(@NotNull List<AbstractWidget> widget){
        addRenderableWidgets$scroller(scroller, widget);
    }

    protected void addRenderableWidgets$scroller(@NotNull AbstractWidget widget){
        addRenderableWidgets$scroller(scroller, widget);
    }
    protected void addRenderableWidgets$scroller(ConfigureScrolWidget scroller, @NotNull AbstractWidget widget){
        scroller.addWidget(widget);
        addWidget(widget);
    }

    // --- Base спижено с AlinLib
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE){
            if(getFocused() != null && getFocused().isFocused()) {
                getFocused().setFocused(false);
                return true;
            }
        }
        if(keyCode == GLFW.GLFW_KEY_D && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0 && !(getFocused() instanceof EditBox)) {
            AlinLib.MINECRAFT.setScreen(new ThanksScreen(this));
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_T && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0){
            PepelandHelper.config.setBoolean("IM_A_TEST_SUBJECT", !PepelandHelper.config.getBoolean("IM_A_TEST_SUBJECT", false));
            if(PepelandHelper.config.getBoolean("IM_A_TEST_SUBJECT", false)) AlinLib.MINECRAFT.setScreen(new TestConfigScreen().build(builder.parent));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Рендер, скролл, прослушивание кей-биндов
    @Override
    public void tick(){
        if(scroller != null) scroller.onScroll.accept(scroller);
        if(scroller_panel != null) scroller_panel.onScroll.accept(scroller_panel);
        if(builder.onTick != null) builder.onTick.onTick(builder);
        if(builder.onTickScreen != null) builder.onTickScreen.onTick(builder, this);
    }

    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(builder.parent);
    }

    // --- Разметка компонентов

    public int getWidth(){
        return Math.min(400, width-10);
    }
    public int getFactWidth(){
        return getWidth() - 45;
    }
    public int getX(){
        return getPanelX()+35;
    }
    public int getPanelWidth(){
        return 20;
    }
    public int getPanelX(){
        return (width / 2) - (getWidth() / 2)+5;
    }
}
