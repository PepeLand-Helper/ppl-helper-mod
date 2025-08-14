package ru.pplh.mod.gui.screens;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.api.PepeLandHelperAPI;
import ru.pplh.mod.api.components.News;
import ru.pplh.mod.api.components.project.Project;
import ru.pplh.mod.api.components.user.User;
import ru.pplh.mod.gui.components.NewsButton;
import ru.pplh.mod.gui.components.ProjectButton;
import ru.pplh.mod.gui.components.ScaledTextBox;
import ru.pplh.mod.gui.components.UserCard;
import ru.pplh.mod.gui.screens.builder.AbstractPPLScreen;
import ru.pplh.mod.gui.screens.builder.ScreenBuilder;
import ru.pplh.mod.gui.screens.message.ErrorScreen;

import java.util.List;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;
import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;
import static ru.kelcuprum.alinlib.gui.Icons.ADD;
import static ru.kelcuprum.alinlib.gui.Icons.EXIT;
import static ru.pplh.mod.PepeLandHelper.Icons.WEB;
import static ru.pplh.mod.api.PepeLandAPI.uriEncode;

public class ProfileScreen extends AbstractPPLScreen {
    public static String getURI(String url, boolean uriEncode) {
        String api = PepeLandHelper.config.getString("SITE_URL", "https://pplh.ru/");
        if (!api.endsWith("/")) api += "/";
        return String.format("%1$s%2$s", api, uriEncode ? uriEncode(url) : url);
    }

    public final User user;

    public ProfileScreen(Screen screen, User user) {
        super(new ScreenBuilder(screen, Component.translatable("pplhelper.oauth.profile"))
                .addPanelWidget(new UserCard(0, 0, 20, user))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.oauth.open_browser"))
                        .setIcon(WEB).setCentered(false).setOnPress((s) ->
                                PepeLandHelper.confirmLinkNow(AlinLib.MINECRAFT.screen, getURI("me", false))))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.oauth.leave"))
                        .setIcon(EXIT).setCentered(false).setOnPress((s) -> {
                            PepeLandHelper.user = null;
                            PepeLandHelper.config.setString("oauth.access_token", "");
                            AlinLib.MINECRAFT.setScreen(screen);
                        })));
        this.user = user;
    }

    boolean apiAvailable = PepeLandHelperAPI.apiAvailable();
    boolean loaded = false;
    public Thread loadInfo = null;

    @Override
    public void initCategory() {
        final int[] y = {builder.contentY - 25};
        if (apiAvailable) {
            if (!loaded) {
                builder.widgets.clear();
                try {
                    loadInfo = new Thread(() -> {
                        builder.addWidget(new ScaledTextBox(Component.translatable("pplhelper.oauth.news"), false, 1.2f));
                        if (user.role.CREATE_NEWS)
                            builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.oauth.news.create")).setIcon(ADD).setOnPress((s) ->
                                    PepeLandHelper.confirmLinkNow(AlinLib.MINECRAFT.screen, getURI("news/create", false))));
                        List<News> news = user.getNews();
                        if (news.isEmpty()) {
                            builder.addWidget(new TextBuilder(Component.translatable("pplhelper.oauth.news.empty")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(GROUPIE).setPosition(getX(), 55).setSize(getContentWidth(), 20).build());
                        } else for (News project : news)
                            builder.addWidget(new NewsButton(getX(), -40, DEFAULT_WIDTH(), project, this));
                        // -=-=-=
                        builder.addWidget(new ScaledTextBox(Component.translatable("pplhelper.oauth.projects"), false, 1.2f));
                        if (user.role.CREATE_PROJECTS)
                            builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.oauth.projects.create")).setIcon(ADD).setOnPress((s) ->
                                    PepeLandHelper.confirmLinkNow(AlinLib.MINECRAFT.screen, getURI("projects/create", false))));
                        List<Project> projects = user.getProjects();
                        if (projects.isEmpty()) {
                            builder.addWidget(new TextBuilder(Component.translatable("pplhelper.oauth.projects.empty")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(GROUPIE).setPosition(getX(), 55).setSize(getContentWidth(), 20).build());
                        } else for (Project project : projects)
                            builder.addWidget(new ProjectButton(getX(), -40, DEFAULT_WIDTH(), project, null, this));
                        // -=-=-=

                        int heigthScroller = builder.contentY;
                        final List<AbstractWidget> widgets = builder.widgets;
                        for (AbstractWidget widget : widgets) {
                            heigthScroller += (widget.getHeight() + 5);
                            widget.setWidth(getContentWidth());
                            widget.setPosition(getX(), y[0]);
                            y[0] += (widget.getHeight() + 5);
                        }
                        yc = Math.min(height - 5, heigthScroller);
                        addRenderableWidgets$scroller(scroller, widgets);
                    });
                    loaded = true;
                    loadInfo.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AlinLib.MINECRAFT.setScreen(new ErrorScreen(ex, builder.parent));
                }
            }
        } else {
            builder.addWidget(new TextBuilder(Component.translatable("pplhelper.api.unavailable")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(GROUPIE).setWidth(getContentWidth()));
        }
        super.initCategory();
    }
}
