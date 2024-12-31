package ru.kelcuprum.pplhelper.gui.configs;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
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
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;

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
                .addPanelWidgets(PepelandHelper.getPanelWidgets(parent, parent))

                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.notice"), true).setConfig(PepelandHelper.config, "PACK_UPDATES.NOTICE"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.auto_update"), true).setConfig(PepelandHelper.config, "PACK_UPDATES.AUTO_UPDATE"));
                if(!FabricLoader.getInstance().isModLoaded("citresewn"))
                    builder.addWidget(new TextBuilder(Component.translatable("pplhelper.configs.pack_updates.only_emote.citresewn_not_installed")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(GROUPIE));
                builder.addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.only_emote"), false).setConfig(PepelandHelper.config, "PACK_UPDATES.ONLY_EMOTE").setActive(FabricLoader.getInstance().isModLoaded("citresewn")));

        try {
            JsonObject pack = PepeLandAPI.getPackInfo(PepelandHelper.onlyEmotesCheck());
            if (packVersion.isBlank()) {
                if (PepelandHelper.getAvailablePack().isEmpty()) {
                    builder.addWidget(new TextBuilder(Component.translatable("pplhelper.pack.not_installed")));
                    builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.download"), Component.literal("v" + pack.get("version").getAsString()))
                            .setOnPress((s) ->
                                    AlinLib.MINECRAFT.setScreen(new DownloadScreen(build(parent), pack, PepelandHelper.onlyEmotesCheck()))
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
                                    AlinLib.MINECRAFT.setScreen(new DownloadScreen(build(parent), pack, PepelandHelper.onlyEmotesCheck()))
                            ).build());
                builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.installed"), Component.literal("v" + packVersion)).setActive(false));
            }
        } catch (Exception ex) {
            return new ErrorScreen(ex, parent);
        }

        return builder.build();
    }
}
