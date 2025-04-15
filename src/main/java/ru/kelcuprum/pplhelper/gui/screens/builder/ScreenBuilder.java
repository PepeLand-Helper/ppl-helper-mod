package ru.kelcuprum.pplhelper.gui.screens.builder;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.Resetable;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.styles.AbstractStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScreenBuilder {
    public Component title;
    public AbstractStyle style;
    public List<AbstractWidget> panelWidgets = new ArrayList<>();
    public List<AbstractWidget> widgets = new ArrayList<>();
    public boolean isResetable = false;
    public OnTick onTick;
    public OnTickScreen onTickScreen;
    public int panelSize = AlinLib.bariumConfig.getBoolean("CONFIG_SCREEN.SMALL_PANEL_SIZE", false) ?  130 : 190;
    public Screen parent;
    public int yL = 35;
    public int yC = 5;
    public int contentY = 60;

    public ScreenBuilder(Screen parent) {
        this(parent, Component.literal("Change me please"));
    }
    public ScreenBuilder(Screen parent, Component title) {
        this(parent, title, GuiUtils.getSelected());
    }
    public ScreenBuilder(Screen parent, Component title, AbstractStyle style){
        this.parent = parent;
        this.title = title;
        this.style = style;
    }
    //
    public ScreenBuilder setTitle(String string){
        setTitle(Component.literal(string));
        return this;
    }
    public ScreenBuilder setTitle(Component component) {
        this.title = component;
        return this;
    }
    public Component getTitle(){
        return this.title;
    }
    //
    public ScreenBuilder setType(AbstractStyle style) {
        this.style = style;
        return this;
    }
    public AbstractStyle getType() {
        return this.style;
    }
    //
    public ScreenBuilder setPanelSize(int panelSize) {
        this.panelSize = panelSize;
        return this;
    }
    public int getPanelSize() {
        return this.panelSize;
    }
    //
    public ScreenBuilder addPanelWidget(AbstractBuilder builder){
        return addPanelWidget(builder.build());
    }
    public ScreenBuilder addPanelWidget(AbstractWidget widget){
        widget.setWidth(getPanelWidth());
        widget.setPosition(getPanelX(), yL);
        yL+=widget.getHeight()+5;
        this.panelWidgets.add(widget);
        return this;
    }
    public ScreenBuilder addPanelWidgets(AbstractBuilder... widgets){
        for(AbstractBuilder widget : widgets) addPanelWidget(widget);
        return this;
    }
    public ScreenBuilder addPanelWidgets(AbstractWidget... widgets){
        for(AbstractWidget widget : widgets) addPanelWidget(widget);
        return this;
    }

    public ScreenBuilder addWidget(AbstractBuilder builder){
        return addWidget(builder.build());
    }
    CategoryBox lastCategory = null;
    public ScreenBuilder addWidget(AbstractWidget widget){
        if(widget instanceof CategoryBox){
            if(lastCategory != widget) lastCategory = (CategoryBox) widget;
            this.widgets.add(widget);
            widget.setPosition(getX(), yC);
            yC+=widget.getHeight()+5;
            for(AbstractWidget cW : ((CategoryBox) widget).getValues()){
                if(!isResetable && (cW instanceof Resetable)) isResetable = true;
                this.widgets.add(cW);
                cW.setX(getX());
                cW.setY(yC);
                yC+=cW.getHeight()+5;
            }
        } else {
            if(lastCategory != null){
                if(!lastCategory.values.contains(widget)) {
                    yC+=6;
                    lastCategory.setRenderLine(true);
                    lastCategory = null;
                }
            }
            this.widgets.add(widget);
            widget.setPosition(yC, getX());
            yC+=widget.getHeight()+5;
        }
        if(!isResetable && (widget instanceof Resetable)) isResetable = true;
        return this;
    }
    public ScreenBuilder addWidgets(AbstractBuilder... widgets){
        for(AbstractBuilder widget : widgets) addWidget(widget);
        return this;
    }
    public ScreenBuilder addWidgets(AbstractWidget... widgets){
        for(AbstractWidget widget : widgets) addWidget(widget);
        return this;
    }
    //
    public ScreenBuilder setResetable(boolean isResetable){
        this.isResetable = isResetable;
        return this;
    }
    public boolean getResetable(){
        return this.isResetable;
    }
    //
    public ScreenBuilder setOnTick(OnTick onTick){
        this.onTick = onTick;
        return this;
    }
    public ScreenBuilder setOnTickScreen(OnTickScreen onTickScreen){
        this.onTickScreen = onTickScreen;
        return this;
    }
    public OnTick getOnTick(){
        return this.onTick;
    }
    //
    public ScreenBuilder setParent(Screen parent){
        this.parent = parent;
        return this;
    }

    public AbstractPPLScreen build() {
        Objects.requireNonNull(this.title, "title == null");
        return new AbstractPPLScreen(this);
    }

    public interface OnTick {
        void onTick(ScreenBuilder builder);
    }
    public interface OnTickScreen {
        void onTick(ScreenBuilder builder, AbstractPPLScreen screen);
    }

    // --- Аналог для адаптиции
    public int getWidth(){
        return Math.min(400, AlinLib.MINECRAFT.getWindow().getGuiScaledWidth()-10);
    }
    public int getFactWidth(){
        return getWidth() - (AlinLib.MINECRAFT.getWindow().getGuiScaledWidth() > 450 ? 0 : 40);
    }
    public int getX(){
        return ((AlinLib.MINECRAFT.getWindow().getGuiScaledWidth()-getWidth()) / 2) + (AlinLib.MINECRAFT.getWindow().getGuiScaledWidth() > 500 ? 0 : 35);
    }
    public int getPanelWidth(){
//        return Math.min(Math.max(20, ((width-getWidth()) / 2)-25), 120);
        return this.panelSize-(AlinLib.bariumConfig.getBoolean("MODERN", true) ? 20 : 10);
    }
    public int getPanelX(){
        return AlinLib.bariumConfig.getBoolean("MODERN", true) ? 10 : 5;
    }
}
