package ru.kelcuprum.pplhelper.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
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
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.components.ProjectButton;
import ru.kelcuprum.pplhelper.gui.screens.builder.AbstractPPLScreen;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

import java.util.List;

import static ru.kelcuprum.alinlib.gui.Icons.SEARCH;

public class ProjectsScreen extends AbstractPPLScreen {
    public ProjectsScreen(Screen screen) {
        super(new ScreenBuilder(screen, Component.translatable("pplhelper.projects")).addPanelWidgets(PepelandHelper.getPanelWidgets(screen, screen)));
        builder.contentY = 60;
    }
    private static String query = "";
    private static int world = 0;
    private static List<Project> lastProjects;
    @Override
    public void initCategory() {
        builder.widgets.clear();
        super.initCategory();
        final int[] y = {builder.contentY - 25};
        int searchSize = (getContentWidth() - 30) / 2;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.news.search"), (s) -> query = s).setValue(query).setPosition(getX(), y[0]).setWidth(searchSize).build());

        addRenderableWidget(new SelectorBuilder(Component.translatable("pplhelper.project.world"), (s) -> {
            world = s.getPosition();
            search();
        }).setList(PepelandHelper.worlds).setValue(world).setPosition(getX() + 5 + searchSize, y[0]).setWidth(searchSize).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.news.find"), (s) -> search()).setSprite(SEARCH).setPosition(getX() + getContentWidth() - 20, y[0]).setWidth(20).build());
        y[0] +=25;
        new Thread(() -> {
            List<Project> projects = lastProjects == null ? PepeLandHelperAPI.getProjects(query, PepelandHelper.worlds[world]) : lastProjects;
            lastProjects = projects;
            if (projects.isEmpty()) {
                builder.addWidget(new TextBuilder(Component.translatable("pplhelper.news.not_found")).setType(TextBuilder.TYPE.MESSAGE).setAlign(TextBuilder.ALIGN.CENTER).setPosition(getX(), 55).setSize(getContentWidth(), 20).build());
                builder.addWidget(new ImageWidget(getX(), 55, getContentWidth(), 20, GuiUtils.getResourceLocation("pplhelper", "textures/gui/sprites/ozon.png"), 640,360, true, Component.empty()));
            } else for (Project project : projects)
                builder.addWidget(new ProjectButton(getX(), -40, getContentWidth(), project, this));
            int heigthScroller = builder.contentY;
            for (AbstractWidget widget : builder.widgets) {
                heigthScroller+=(widget.getHeight()+5);
                widget.setWidth(getContentWidth());
                widget.setPosition(getX(), y[0]);
                y[0] +=(widget.getHeight()+5);
            }
            yc = Math.min(height-5, heigthScroller);
            addRenderableWidgets$scroller(scroller, builder.widgets);
        }).start();
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

    private void search(){
        lastProjects = null;
        builder.widgets.clear();
        rebuildWidgets();
    }
}
