package ru.kelcuprum.pplhelper.gui.configs;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;
import ru.kelcuprum.pplhelper.gui.message.DownloadScreen;
import ru.kelcuprum.pplhelper.gui.message.ErrorScreen;
import ru.kelcuprum.pplhelper.gui.screens.CommandsScreen;
import ru.kelcuprum.pplhelper.gui.screens.ModsScreen;
import ru.kelcuprum.pplhelper.gui.screens.NewsListScreen;
import ru.kelcuprum.pplhelper.gui.screens.ProjectsScreen;

import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.*;

public class UpdaterScreen {
    public Screen parent;
    public static boolean isEmotes = PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false);
    public static String packVersion = PepelandHelper.getInstalledPack();

    public Screen build(Screen parent) {
        this.parent = parent;
        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent, Component.translatable("pplhelper"))
                .setOnTick((s) -> {
                    if (isEmotes != PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false)) {
                        isEmotes = PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false);
                        AlinLib.MINECRAFT.setScreen(build(parent));
                    }
                    if (!packVersion.contains(PepelandHelper.getInstalledPack())) {
                        packVersion = PepelandHelper.getInstalledPack();
                        AlinLib.MINECRAFT.setScreen(build(parent));
                    }
                })
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.news")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new NewsListScreen().build(parent))).setIcon(WIKI))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.projects")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ProjectsScreen().build(parent))).setIcon(PROJECTS))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.commands")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new CommandsScreen().build(parent))).setIcon(LIST))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.mods")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ModsScreen().build(parent))).setIcon(MODS))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.pack")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new UpdaterScreen().build(parent))).setIcon(PepelandHelper.Icons.PACK_INFO))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setIcon(OPTIONS))

                .addWidget(new TextBox(Component.translatable("pplhelper.pack")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.notice"), true).setConfig(PepelandHelper.config, "PACK_UPDATES.NOTICE"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.auto_update"), true).setConfig(PepelandHelper.config, "PACK_UPDATES.AUTO_UPDATE"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.only_emote"), false).setConfig(PepelandHelper.config, "PACK_UPDATES.ONLY_EMOTE"));

        try {
            JsonObject pack = PepeLandAPI.getPackInfo(PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false));
            if (packVersion.isBlank()) {
                if (PepelandHelper.getAvailablePack().isEmpty()) {
                    builder.addWidget(new TextBox(Component.translatable("pplhelper.pack.not_installed")));
                    builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.download"), Component.literal("v" + pack.get("version").getAsString()))
                            .setOnPress((s) ->
                                    AlinLib.MINECRAFT.setScreen(new DownloadScreen(build(parent), pack, PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false)))
                            ).build());
                } else {
                    builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.not_selected")).setOnPress((s) -> {
                        AlinLib.MINECRAFT.getResourcePackRepository().addPack(PepelandHelper.getAvailablePack());
                        AlinLib.MINECRAFT.options.updateResourcePacks(AlinLib.MINECRAFT.getResourcePackRepository());
                        AlinLib.MINECRAFT.setScreen(parent);
                    }));
                }
            } else {
                if (!pack.get("version").getAsString().contains(packVersion))
                    builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.download_update"), Component.literal("v" + pack.get("version").getAsString()))
                            .setOnPress((s) ->
                                    AlinLib.MINECRAFT.setScreen(new DownloadScreen(build(parent), pack, PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false)))
                            ).build());
                builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.installed"), Component.literal("v" + packVersion)).setActive(false));
            }
        } catch (Exception ex) {
            return new ErrorScreen(ex, parent);
        }

        return builder.build();
    }
}
