package ru.kelcuprum.pplhelper.gui.screens.builder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.Description;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.Resetable;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.components.text.DescriptionBox;
import ru.kelcuprum.alinlib.gui.screens.ConfirmScreen;
import ru.kelcuprum.alinlib.gui.screens.ThanksScreen;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.gui.screens.configs.ConfigScreen;
import ru.kelcuprum.pplhelper.gui.screens.configs.TestConfigScreen;

import java.util.List;

import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.WHITE_PEPE;

public class AbstractPPLScreen extends Screen {
    public ConfigureScrolWidget scroller;
    public ConfigureScrolWidget scroller_panel;
    public final ScreenBuilder builder;
    public DescriptionBox descriptionBox;
    public boolean lastCheck = false;
    public AbstractPPLScreen(ScreenBuilder builder){
        super(builder.getTitle());
        this.builder = builder;
    }

    @Override
    protected void init() {
        initPanelButtons();
        initCategory();
        assert this.minecraft != null;
        if (this.minecraft.getLastInputType().isKeyboard()) {
            if (!this.builder.panelWidgets.isEmpty()) this.setFocused(getFirstActiveWidget(this.builder.panelWidgets));
            else if (!this.builder.widgets.isEmpty()) this.setFocused(getFirstActiveWidget(this.builder.widgets));
            else this.setFocused(back);
        }
    }
    public AbstractWidget getFirstActiveWidget(List<AbstractWidget> widgets){
        AbstractWidget widget = widgets.getFirst();
        for(AbstractWidget abstractWidget : widgets){
            if(abstractWidget.isActive()) {
                widget = abstractWidget;
                break;
            }
        }
        return widget;
    }
    public AbstractWidget titleW;
    public AbstractWidget back;
    public AbstractWidget reset;
    public AbstractWidget options;

    public void removeWidgetFromBuilder(){
        for (AbstractWidget widget : this.builder.widgets)
            removeWidget(widget);
        this.builder.widgets.clear();
    }
    public void removePanelWidgetFromBuilder(){
        for (AbstractWidget widget : this.builder.panelWidgets)
            removeWidget(widget);
        this.builder.panelWidgets.clear();
    }

    int yo = 35;
    public void initPanelButtons(){
        // -=-=-=-=-=-=-=-
        // this.builder.title
        addRenderableWidget(new ImageWidget(7, 5, 20, 20, WHITE_PEPE, 20, 20, true, Component.empty()));
        titleW = addRenderableWidget(new TextBuilder(Component.translatable("pplhelper")).setPosition(30, 5).setSize(this.builder.panelSize - 35, 20).build());
        // -=-=-=-=-=-=-=-
        this.descriptionBox = new DescriptionBox(10, 35, this.builder.panelSize - 20, height - 70, Component.empty());
        this.descriptionBox.visible = false;
        addRenderableWidget(this.descriptionBox);
        // -=-=-=-=-=-=-=-
        // Exit Buttons
        // 85 before reset button
        int heigthScroller = 35;
        for (AbstractWidget widget : builder.panelWidgets) heigthScroller+=(widget.getHeight()+5);
        this.scroller_panel = addRenderableWidget(new ConfigureScrolWidget(builder.panelSize-9, 30, 4, this.height - 60, Component.empty(), scroller -> {
            scroller.innerHeight = 5;
            for (AbstractWidget widget : builder.panelWidgets) {
                if (widget.visible) {
                    widget.setY(30+(int) (scroller.innerHeight - scroller.scrollAmount()));
                    scroller.innerHeight += (widget.getHeight() + 5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight-=8;
        }));
        yo = Math.min(heigthScroller, height-30);
        options = addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((OnPress) -> {
            this.minecraft.setScreen(new ConfigScreen().build(this));
        }).setSprite(OPTIONS).setSize(20, 20).setPosition(5, yo+5).build());

        back = addRenderableWidget(new ButtonBuilder(CommonComponents.GUI_BACK).setOnPress((OnPress) -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(builder.parent);
        }).setIcon(AlinLib.isAprilFool() ? EXIT : null).setPosition(30, yo+5).setSize(this.builder.panelSize - 25 - (builder.isResetable ? 35 : 10), 20).build());

        if(builder.isResetable) reset = addRenderableWidget(new ButtonBuilder(Component.translatable("alinlib.component.reset")).setOnPress((OnPress) -> {
            this.minecraft.setScreen(new ConfirmScreen(this, RESET, Component.translatable("alinlib.title.reset"), Component.translatable("alinlib.title.reset.description"), (bl) -> {
                if(bl){
                    for (AbstractWidget widget : builder.widgets)
                        if (widget instanceof Resetable) ((Resetable) widget).resetValue();
                    assert this.minecraft != null;
                    new ToastBuilder()
                            .setTitle(title)
                            .setMessage(Component.translatable("alinlib.component.reset.toast"))
                            .setIcon(RESET)
                            .buildAndShow();
                    AlinLib.LOG.log(Component.translatable("alinlib.component.reset.toast"));
                }
            }));
        }).setSprite(RESET).setSize(20, 20).setPosition(this.builder.panelSize - 25, yo+5).build());

        addRenderableWidgets$scroller(scroller_panel, builder.panelWidgets);
    }
    public int yc = 0;
    public void initCategory() {
        addRenderableWidget(new TextBuilder(builder.title).setPosition(getX(), 10).setWidth(getContentWidth()).build());
        yc = builder.contentY;
        int width = getContentWidth();
        for (AbstractWidget widget : builder.widgets) {
            widget.setWidth(width);
            widget.setX(this.builder.panelSize + 5);
        }
        int heigthScroller = builder.contentY;
        for (AbstractWidget widget : builder.widgets) if(widget.visible) heigthScroller+=(widget.getHeight()+5);
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(getX()+getContentWidth()+1, builder.contentY, 4, this.height-5-builder.contentY, Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            int heigthScroller1 = builder.contentY;
            boolean descriptionEnable = false;
            CategoryBox lastCategory = null;
            for (AbstractWidget widget : builder.widgets) {
                if (widget.visible) {
                    if (widget instanceof Description) {
                        if (widget.isHoveredOrFocused() && ((Description) widget).getDescription() != null && this.descriptionBox != null) {
                            descriptionEnable = true;
                            this.descriptionBox.setDescription(((Description) widget).getDescription());
                        }
                    }
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
                    widget.setPosition(getX(), builder.contentY + (int) (scroller.innerHeight - scroller.scrollAmount()));
                    heigthScroller1+=(widget.getHeight()+5);
                    scroller.innerHeight += (widget.getHeight() + 5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight -= 8;
            yc = Math.min(height-5, heigthScroller1);
            if (this.lastCheck != descriptionEnable) {
                lastCheck = descriptionEnable;
                for (AbstractWidget widget : builder.panelWidgets) {
                    widget.visible = !lastCheck;
                }
                this.descriptionBox.visible = lastCheck;
            }
        }));
        yc = Math.min(height-5, heigthScroller);
        addRenderableWidgets$scroller(scroller, builder.widgets);
    }

    public void rebuildPanel(){
        removeWidget(scroller_panel);
        scroller_panel = null;
        removeWidget(titleW);
        removeWidget(back);
        removeWidget(reset);
        removeWidget(options);
        removeWidget(descriptionBox);
        for (AbstractWidget widget : this.builder.panelWidgets) {
            removeWidget(widget);
        }
        initPanelButtons();
    }
    public void rebuildCategory(){
        removeWidget(scroller);
        scroller = null;
        for (AbstractWidget widget : this.builder.widgets) {
            removeWidget(widget);
        }
        initCategory();
    }

    // Добавление виджетов
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE){
            if(getFocused() != null && getFocused().isFocused()) {
                getFocused().setFocused(false);
                return true;
            }
        }
        if(keyCode == GLFW.GLFW_KEY_D && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0 && !(getFocused() instanceof EditBox))
            AlinLib.MINECRAFT.setScreen(new ThanksScreen(this));
        if(keyCode == GLFW.GLFW_KEY_T && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0 && !(getFocused() instanceof EditBox))
            AlinLib.MINECRAFT.setScreen(new TestConfigScreen().build(this));
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

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller_panel != null && scroller_panel.widgets.contains(guiEventListener)) {
                if ((d >= 10 && d <= builder.panelSize-10) && (e >= 35 && e <= height-35)) {
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (mouseX <= this.builder.panelSize) {
            if (descriptionBox.visible && (mouseX >= 5 && mouseX <= builder.panelSize - 5) && (mouseY >= 40 && mouseY <= height - 30)) {
                scr = descriptionBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            } else if (!scr && scroller_panel != null) {
                scr = scroller_panel.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        } else {
            if (!scr && scroller != null) {
                scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        }
        return scr;
    }

    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(builder.parent);
    }
    // --- render
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        assert this.minecraft != null;
        super.renderBackground(guiGraphics, i, j, f);
        // Panel
        guiGraphics.fill(5, 5, this.builder.panelSize-5, 25, Colors.BLACK_ALPHA);
        guiGraphics.fill(5, 30, this.builder.panelSize-5, yo, Colors.BLACK_ALPHA);
        // Content
        guiGraphics.fill(getX()-5, 5, getX()+getContentWidth()+5, yc, Colors.BLACK_ALPHA);
        // - Title
        guiGraphics.fill(getX(), 10, getX()+getContentWidth(), 30, Colors.BLACK_ALPHA);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        try {
            guiGraphics.enableScissor(10, 35, builder.panelSize-10, yo-5);
            if (scroller_panel != null) for (AbstractWidget widget : scroller_panel.widgets) widget.render(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.disableScissor();

            guiGraphics.enableScissor(0, builder.contentY, width, yc-5);
            if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.disableScissor();
        } catch (Exception ex) {
            PepeLandHelper.LOG.error(ex.getMessage());
        }
    }
    // ------------------------
    // --- Position + Width ---
    // ------------------------
    public int getContentWidth(){
        return Math.min(Math.max(550, (int) (width*0.5)), width-15-this.builder.panelSize);
    }
    public int getX(){
        return Math.max(this.builder.panelSize+5, this.builder.panelSize + ((width-this.builder.panelSize) / 2) - (getContentWidth() / 2));
    }
}
