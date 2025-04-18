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
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.components.ProjectButton;
import ru.kelcuprum.pplhelper.gui.screens.builder.AbstractPPLScreen;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;
import ru.kelcuprum.pplhelper.utils.FollowManager;

import java.util.List;

import static ru.kelcuprum.alinlib.gui.Colors.*;
import static ru.kelcuprum.alinlib.gui.Icons.SEARCH;

public class ProjectsScreen extends AbstractPPLScreen {
    public ProjectsScreen(Screen screen) {
        super(new ScreenBuilder(screen, Component.translatable("pplhelper.projects")).addPanelWidgets(PepeLandHelper.getPanelWidgets(screen, screen)));
        builder.contentY = 85;
    }

    private static String query = "";
    private static int world = 0;
    private static int category = 0;
    private static List<Project> lastProjects;
    public Thread loadInfo = null;
    boolean apiAvailable = PepeLandHelperAPI.apiAvailable();

    @Override
    public void initCategory() {
        builder.widgets.clear();
        super.initCategory();
        final int[] y = {builder.contentY - 50};
        int searchSize = (getContentWidth() - 5) / 2;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.news.search"), (s) -> query = s).setValue(query).setPosition(getX(), y[0]).setWidth(getContentWidth() - 25).setActive(apiAvailable).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.news.find"), (s) -> search()).setSprite(SEARCH).setPosition(getX() + getContentWidth() - 20, y[0]).setWidth(20).setActive(apiAvailable).build());
        y[0] += 25;
        addRenderableWidget(new SelectorBuilder(Component.translatable("pplhelper.project.world"), (s) -> {
            world = s.getPosition();
            search();
        }).setList(PepeLandHelper.worlds).setValue(world).setPosition(getX(), y[0]).setWidth(searchSize).setActive(apiAvailable).build());
        addRenderableWidget(new SelectorBuilder(Component.translatable("pplhelper.project.category"), (s) -> {
            category = s.getPosition();
            search();
        }).setList(PepeLandHelper.pc).setValue(category).setPosition(getX() + 5 + searchSize, y[0]).setWidth(searchSize).setActive(apiAvailable).build());
        y[0] += 25;
        if(FollowManager.project != null){
            builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.project.selected")));
            builder.addWidget(new ProjectButton(getX(), -40, searchSize, FollowManager.project, this));
            builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.project.unfollow_project"), (s) -> {
                FollowManager.resetCoordinates();
                rebuildWidgets();
            }).setPosition(getX(), -20).setWidth(searchSize).build());
            builder.addWidget(new HorizontalRuleBuilder());
        }
        if (apiAvailable) {
            if(!PepeLandHelper.worldsLoaded){
                try {
                    PepeLandHelper.worlds = PepeLandHelperAPI.getWorlds();
                    PepeLandHelper.worldsLoaded = true;
                } catch (Exception ignored){}
            }
            if(!PepeLandHelper.categoriesAndTags){
                try {
                    PepeLandHelper.pc = PepeLandHelperAPI.getProjectCategories();
                    PepeLandHelper.pct = PepeLandHelperAPI.getProjectCategoriesTags();
                    PepeLandHelper.nc = PepeLandHelperAPI.getNewsCategories();
                    PepeLandHelper.nct = PepeLandHelperAPI.getNewsCategoriesTags();
                    PepeLandHelper.categoriesAndTags = true;
                } catch (Exception ignored){}
            }
            loadInfo = new Thread(() -> {
                List<Project> projects = lastProjects == null ? PepeLandHelperAPI.getProjects(query, PepeLandHelper.worlds[world], PepeLandHelper.pct[category]) : lastProjects;
                lastProjects = projects;
                if (projects.isEmpty()) {
                    builder.addWidget(new TextBuilder(Component.translatable("pplhelper.news.not_found")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(GROUPIE).setPosition(getX(), 55).setSize(getContentWidth(), 20).build());
                    builder.addWidget(new ImageWidget(getX(), 55, getContentWidth(), 20, GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/amogus.png"), 256, 32, true, Component.empty()));
                } else for (Project project : projects)
                    builder.addWidget(new ProjectButton(getX(), -40, getContentWidth(), project, this));
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
        if (i == GLFW.GLFW_KEY_ENTER) {
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
        lastProjects = null;
        if (loadInfo != null) {
            loadInfo.interrupt();
            loadInfo = null;
        }
        builder.widgets.clear();
        rebuildWidgets();
    }
}
