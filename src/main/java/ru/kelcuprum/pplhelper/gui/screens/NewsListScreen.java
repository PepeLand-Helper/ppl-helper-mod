package ru.kelcuprum.pplhelper.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.News;
import ru.kelcuprum.pplhelper.gui.components.NewsButton;
import ru.kelcuprum.pplhelper.gui.screens.builder.AbstractPPLScreen;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

import java.util.List;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;
import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;
import static ru.kelcuprum.alinlib.gui.Icons.SEARCH;

public class NewsListScreen extends AbstractPPLScreen {
    public NewsListScreen(Screen screen) {
        super(new ScreenBuilder(screen, Component.translatable("pplhelper.news")).addPanelWidgets(PepelandHelper.getPanelWidgets(screen, screen)));
        builder.contentY = 60;
    }
    private static String query = "";
    private static int category = 0;
    private static List<News> lastNews;
    public Thread loadInfo = null;
    boolean apiAvailable = PepeLandHelperAPI.apiAvailable();
    @Override
    public void initCategory() {
        builder.widgets.clear();
        super.initCategory();
        final int[] y = {builder.contentY-25};
        int searchSize = (getContentWidth() - 30) / 2;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.news.search"), (s) -> query = s).setValue(query).setPosition(getX(), y[0]).setWidth(searchSize).setActive(apiAvailable).build());
        addRenderableWidget(new SelectorBuilder(Component.translatable("pplhelper.project.category"), (s) -> {
            category = s.getPosition();
            search();
        }).setList(PepelandHelper.nc).setValue(category).setPosition(getX() + 5 + searchSize, y[0]).setWidth(searchSize).setActive(apiAvailable).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.news.find"), (s) -> search()).setSprite(SEARCH).setPosition(getX()+getContentWidth()-20, y[0]).setWidth(20).setActive(apiAvailable).build());
        y[0] += 25;
        if(apiAvailable) {
            if(!PepelandHelper.categoriesAndTags){
                try {
                    PepelandHelper.pc = PepeLandHelperAPI.getProjectCategories();
                    PepelandHelper.pct = PepeLandHelperAPI.getProjectCategoriesTags();
                    PepelandHelper.nc = PepeLandHelperAPI.getNewsCategories();
                    PepelandHelper.nct = PepeLandHelperAPI.getNewsCategoriesTags();
                    PepelandHelper.categoriesAndTags = true;
                } catch (Exception ignored){}
            }
            loadInfo = new Thread(() -> {
                List<News> projects = lastNews == null ? PepeLandHelperAPI.getNews(query, PepelandHelper.nct[category]) : lastNews;
                lastNews = projects;
                if (projects.isEmpty()) {
                    builder.addWidget(new TextBuilder(Component.translatable("pplhelper.news.not_found")).setType(TextBuilder.TYPE.MESSAGE).setAlign(TextBuilder.ALIGN.CENTER).setPosition(getX(), 55).setSize(getContentWidth(), 20).build());
                    builder.addWidget(new ImageWidget(getX(), 55, getContentWidth(), 20, GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/ozon.png"), 640, 360, true, Component.empty()));
                } else for (News project : projects)
                    builder.addWidget(new NewsButton(getX(), -40, DEFAULT_WIDTH(), project, this));
                int heigthScroller = builder.contentY;
                for (AbstractWidget widget : builder.widgets) {
                    heigthScroller += (widget.getHeight() + 5);
                    widget.setWidth(getContentWidth());
                    widget.setPosition(getX(), y[0]);
                    y[0] += (widget.getHeight() + 5);
                }
                yc = Math.min(height - 5, heigthScroller);
                addRenderableWidgets$scroller(scroller, builder.widgets);
            });
            loadInfo.start();
        } else {
            builder.addWidget(new TextBuilder(Component.translatable("pplhelper.api.unavailable")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(GROUPIE).setWidth(getContentWidth()));
            addRenderableWidgets$scroller(scroller, builder.widgets);
        }

    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(i == GLFW.GLFW_KEY_ENTER){
            if (getFocused() != null && getFocused().isFocused() && getFocused() instanceof EditBox) {
                search();
                return true;
            }
        }
        return super.keyPressed(i, j, k);
    }
    private long lastSearch = System.currentTimeMillis();
    private int count = 0;

    private void search() {
        long cur = System.currentTimeMillis();
        long limit = 750;
        if(cur - lastSearch <= limit){
            if(count > 4){
                Util.getPlatform().openUri("https://wfu.kelcu.ru/vpEJaZQ");
                AlinLib.MINECRAFT.setScreen(builder.parent);
            } else count++;
        } else if(cur - lastSearch > limit) {
            lastSearch = cur;
            count = 0;
        }
        count++;
        lastNews = null;
        if(loadInfo != null){
            loadInfo.interrupt();
            loadInfo = null;
        }
        builder.widgets.clear();
        rebuildWidgets();
    }
}
