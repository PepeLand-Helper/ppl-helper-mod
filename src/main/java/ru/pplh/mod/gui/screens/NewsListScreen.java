package ru.pplh.mod.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.PageControlWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.screens.DialogScreen;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.api.PepeLandHelperAPI;
import ru.pplh.mod.api.components.News;
import ru.pplh.mod.api.components.SearchResult;
import ru.pplh.mod.gui.components.NewsButton;
import ru.pplh.mod.gui.screens.builder.AbstractPPLScreen;
import ru.pplh.mod.gui.screens.builder.ScreenBuilder;

import java.util.ArrayList;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;
import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;
import static ru.kelcuprum.alinlib.gui.Icons.SEARCH;

public class NewsListScreen extends AbstractPPLScreen {
    public NewsListScreen(Screen screen) {
        super(new ScreenBuilder(screen, Component.translatable("pplhelper.news")).addPanelWidgets(PepeLandHelper.getPanelWidgets(screen, screen)));
        builder.contentY = 85;
    }
    private static String query = "";
    private static int category = 0;
    private static SearchResult lastNews;
    public Thread loadInfo = null;
    boolean apiAvailable = PepeLandHelperAPI.apiAvailable();
    public PageControlWidget pageControlWidget;
    private static int currentPosition = 0;
    @Override
    public void initCategory() {
        builder.widgets.clear();
        super.initCategory();
        final int[] y = {builder.contentY-50};
        int searchSize = (getContentWidth() - 30) / 2;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.news.search"), (s) -> query = s).setValue(query).setPosition(getX(), y[0]).setWidth(searchSize).setActive(apiAvailable).build());
        addRenderableWidget(new SelectorBuilder(Component.translatable("pplhelper.project.category"), (s) -> {
            category = s.getPosition();
            search();
        }).setList(PepeLandHelper.nc).setValue(category).setPosition(getX() + 5 + searchSize, y[0]).setWidth(searchSize).setActive(apiAvailable).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.news.find"), (s) -> search()).setSprite(SEARCH).setPosition(getX()+getContentWidth()-20, y[0]).setWidth(20).setActive(apiAvailable).build());
        y[0] += 25;
        pageControlWidget =  addRenderableWidget(new PageControlWidget(getX(), y[0], getContentWidth(), 20, currentPosition, 8, (widget) -> {
            currentPosition = widget.position;
            search();
        }));
        y[0] += 25;
        if(apiAvailable) {
            if(!PepeLandHelper.categoriesAndTags){
                try {
                    PepeLandHelper.loadStaticInformation();
                } catch (Exception ignored){}
            }
            loadInfo = new Thread(() -> {
                SearchResult projects = lastNews == null ? PepeLandHelperAPI.getNews(query, PepeLandHelper.nct[category]) : lastNews;
                lastNews = projects;
                if (projects.arrayList().isEmpty()) {
                    builder.addWidget(new TextBuilder(Component.translatable("pplhelper.news.not_found")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(GROUPIE).setPosition(getX(), 55).setSize(getContentWidth(), 20).build());
                    builder.addWidget(new ImageWidget(getX(), 55, getContentWidth(), 20, GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/amogus.png"), 256, 32, true, Component.empty()));
                } else {
                    pageControlWidget.size = projects.pages();
                    for (News project : (ArrayList<News>) projects.arrayList())
                        builder.addWidget(new NewsButton(getX(), -40, DEFAULT_WIDTH(), project, this));
                }
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
//                Util.getPlatform().openUri("https://wfu.kelcu.ru/vpEJaZQ");
//                AlinLib.MINECRAFT.setScreen(builder.parent);
                AlinLib.MINECRAFT.setScreen(new DialogScreen(builder.parent, new String[]{
                        "[...]",
                        "[Ты долбишь как птица из этой ссылке -> i.clovi.art/vpEJaZQ]",
                        "[Пожалуйста, не кликай так быстро.]",
                        "[Иначе сервер на тебя обидится и мировая машина мигом рухнет.]",
                        "[Надеюсь ты понял(а).]"
                }, null));
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
