package ru.kelcuprum.pplhelper.gui.configs;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;
import ru.kelcuprum.pplhelper.gui.message.DownloadScreen;
import ru.kelcuprum.pplhelper.gui.message.ErrorScreen;
import ru.kelcuprum.pplhelper.gui.screens.*;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.*;

public class UpdaterScreen {
    public Screen parent;
    public static boolean isEmotes = PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false);
    public static String packVersion = PepelandHelper.getInstalledPack();

    public Screen build(Screen parent) {
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.pack"))
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
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.news")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new NewsListScreen(parent))).setSprite(WIKI).setSize(20, 20))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.projects")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ProjectsScreen(build(parent)))).setSprite(PROJECTS).setSize(20, 20))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.commands")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new CommandsScreen().build(parent))).setSprite(LIST).setSize(20, 20))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.mods")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ModsScreen().build(parent))).setSprite(MODS).setSize(20, 20))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.pack")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new UpdaterScreen().build(parent))).setSprite(PepelandHelper.Icons.PACK_INFO).setSize(20, 20))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setSprite(OPTIONS).setSize(20, 20))

                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.notice"), true).setConfig(PepelandHelper.config, "PACK_UPDATES.NOTICE"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.auto_update"), true).setConfig(PepelandHelper.config, "PACK_UPDATES.AUTO_UPDATE"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.only_emote"), false).setConfig(PepelandHelper.config, "PACK_UPDATES.ONLY_EMOTE"));

        try {
            JsonObject pack = PepeLandAPI.getPackInfo(PepelandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false));
            if (packVersion.isBlank()) {
                if (PepelandHelper.getAvailablePack().isEmpty()) {
                    builder.addWidget(new TextBuilder(Component.translatable("pplhelper.pack.not_installed")));
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
