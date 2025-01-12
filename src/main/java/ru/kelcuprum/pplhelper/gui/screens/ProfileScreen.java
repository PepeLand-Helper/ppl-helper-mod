package ru.kelcuprum.pplhelper.gui.screens;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.Mod;
import ru.kelcuprum.pplhelper.api.components.News;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.api.components.ResourcePack;
import ru.kelcuprum.pplhelper.api.components.user.User;
import ru.kelcuprum.pplhelper.gui.components.*;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;
import ru.kelcuprum.pplhelper.gui.screens.message.ErrorScreen;

import java.util.List;

import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;
import static ru.kelcuprum.alinlib.gui.Icons.ADD;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.WEB;
import static ru.kelcuprum.pplhelper.api.PepeLandAPI.uriEncode;

public class ProfileScreen {
    public String getURI(String url, boolean uriEncode){
        String api = PepelandHelper.config.getString("SITE_URL", "https://h.pplmods.ru/");
        if(!api.endsWith("/")) api+="/";
        return String.format("%1$s%2$s", api, uriEncode ? uriEncode(url) : url);
    }
    public Screen parent;
    public Screen build(Screen parent, User user){
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.oauth.profile"))
                .addPanelWidget(new UserCard(0, 0, 20, user))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.oauth.open_browser")).setIcon(WEB).setCentered(false).setOnPress((s) ->
                    PepelandHelper.confirmLinkNow(AlinLib.MINECRAFT.screen, getURI("me", false))));
        builder.addWidget(new ScaledTextBox(Component.translatable("pplhelper.oauth.news"), false, 1.2f));
        if(user.role.CREATE_NEWS) builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.oauth.news.create")).setIcon(ADD).setOnPress((s) ->
                PepelandHelper.confirmLinkNow(AlinLib.MINECRAFT.screen, getURI("news/create", false))));

        List<News> news = user.getNews();
        if (news.isEmpty()) builder.addWidget(new TextBuilder(Component.translatable("pplhelper.oauth.news.empty")).setType(TextBuilder.TYPE.BLOCKQUOTE));
        else for (News project : news)
            builder.addWidget(new NewsButton(0, -40, DEFAULT_WIDTH(), project, builder.build()));

        builder.addWidget(new HorizontalRuleBuilder());

        builder.addWidget(new ScaledTextBox(Component.translatable("pplhelper.oauth.projects"), false, 1.2f));
        if(user.role.CREATE_PROJECTS) builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.oauth.projects.create")).setIcon(ADD).setOnPress((s) ->
                PepelandHelper.confirmLinkNow(AlinLib.MINECRAFT.screen, getURI("projects/create", false))));


        List<Project> projects = user.getProjects();
        if (projects.isEmpty()) builder.addWidget(new TextBuilder(Component.translatable("pplhelper.oauth.projects.empty")).setType(TextBuilder.TYPE.BLOCKQUOTE));
            else for (Project project : projects)
            builder.addWidget(new ProjectButton(0, -40, DEFAULT_WIDTH(), project, builder.build()));

        return builder.build();
    }
}
