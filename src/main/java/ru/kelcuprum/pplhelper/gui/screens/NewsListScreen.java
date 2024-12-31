package ru.kelcuprum.pplhelper.gui.screens;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.News;
import ru.kelcuprum.pplhelper.gui.components.NewsButton;
import ru.kelcuprum.pplhelper.gui.screens.builder.AbstractPPLScreen;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

import java.util.List;

import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;
import static ru.kelcuprum.alinlib.gui.Icons.SEARCH;

public class NewsListScreen extends AbstractPPLScreen {
    public NewsListScreen(Screen screen) {
        super(new ScreenBuilder(screen, Component.translatable("pplhelper.news")).addPanelWidgets(PepelandHelper.getPanelWidgets(screen, screen)));
        contentY = 60;
    }
    private static String query = "";
    private static List<News> lastNews;
    @Override
    public void initContent() {
        int x = getX();
        final int[] y = {contentY - 50};
        addRenderableWidget(new TextBuilder(builder.title).setPosition(x, y[0]).setSize(getFactWidth()-25, 20).build());
        ButtonBuilder exit = new ButtonBuilder(Component.literal("x")).setOnPress((s) -> onClose()).setPosition(x+getFactWidth()-20, y[0]).setWidth(20);
        addRenderableWidget(exit.build());
        y[0] +=25;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.news.search"), (s) -> query = s).setValue(query).setPosition(x, y[0]).setWidth(getFactWidth()-25).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.news.find"), (s) -> search()).setSprite(SEARCH).setPosition(x+getFactWidth()-20, y[0]).setWidth(20).build());
        y[0] +=25;
        new Thread(() -> {
            List<News> projects = lastNews == null ? PepeLandHelperAPI.getNews(query) : lastNews;
            lastNews = projects;
            builder.widgets.clear();
            if(projects.isEmpty()) {
                builder.widgets.add(new TextBuilder(Component.translatable("pplhelper.news.not_found")).setType(TextBuilder.TYPE.MESSAGE).setAlign(TextBuilder.ALIGN.CENTER).setPosition(x, 55).setSize(getFactWidth(), 20).build());
                builder.widgets.add(new ImageWidget(x, 55, getFactWidth(), 20, GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/ozon.png"), 640,360, true, Component.empty()));
            } else for(News project : projects)
                builder.widgets.add(new NewsButton(0, -40, DEFAULT_WIDTH(), project, this));
            for (AbstractWidget widget : builder.widgets) {
                widget.setWidth(getFactWidth());
                widget.setPosition(x, y[0]);
                y[0] +=(widget.getHeight()+5);
            }
            addRenderableWidgets$scroller(scroller, builder.widgets);
        }).start();
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(getX()+getFactWidth()+1, contentY, 4, Math.min(y[0] -contentY, height-5-contentY), Component.empty(), scroller -> {
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

    private void search(){
        lastNews = null;
        builder.widgets.clear();
        rebuildWidgets();
    }
}
