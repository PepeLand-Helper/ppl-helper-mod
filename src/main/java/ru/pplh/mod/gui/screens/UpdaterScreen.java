package ru.pplh.mod.gui.screens;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.api.PepeLandAPI;
import ru.pplh.mod.api.PepeLandHelperAPI;
import ru.pplh.mod.gui.screens.message.DownloadScreen;
import ru.pplh.mod.gui.screens.message.ErrorScreen;
import ru.pplh.mod.gui.screens.builder.ScreenBuilder;

import static ru.kelcuprum.alinlib.gui.Colors.CLOWNFISH;

public class UpdaterScreen {
    public Screen parent;
    public static boolean isEmotes = PepeLandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false);
    public static String packVersion = PepeLandHelper.getInstalledPackVersion();

    public Screen build(Screen parent) {
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.pack"))
                .setOnTick((s) -> {
                    if (isEmotes != PepeLandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false)) {
                        isEmotes = PepeLandHelper.config.getBoolean("PACK_UPDATES.ONLY_EMOTE", false);
                        AlinLib.MINECRAFT.setScreen(build(parent));
                    }
                    if (!packVersion.contains(PepeLandHelper.getInstalledPackVersion())) {
                        packVersion = PepeLandHelper.getInstalledPackVersion();
                        AlinLib.MINECRAFT.setScreen(build(parent));
                    }
                })
                .addPanelWidgets(PepeLandHelper.getPanelWidgets(parent, parent))

                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.notice"), true).setConfig(PepeLandHelper.config, "PACK_UPDATES.NOTICE"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.auto_update"), true).setConfig(PepeLandHelper.config, "PACK_UPDATES.AUTO_UPDATE"));
                if(!PepeLandHelperAPI.apiAvailable()) builder.addWidget(new TextBuilder(Component.translatable("pplhelper.configs.pack_updates.modrinth.api_warn")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(CLOWNFISH));
                builder.addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.modrinth"), true).setConfig(PepeLandHelper.config, "PACK.MODRINTH"));
                builder.addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.pack_updates.only_emote"), false).setConfig(PepeLandHelper.config, "PACK_UPDATES.ONLY_EMOTE")); // .setActive(FabricLoader.getInstance().isModLoaded("citresewn"))

        try {
            boolean modrinth = PepeLandHelper.config.getBoolean("PACK.MODRINTH", true);
            JsonObject pack = PepeLandAPI.getPackInfo(PepeLandHelper.onlyEmotesCheck(), modrinth);
            if (packVersion.isBlank()) {
                if (PepeLandHelper.getAvailablePack().isEmpty()) {
                    builder.addWidget(new TextBuilder(Component.translatable("pplhelper.pack.not_installed")));
                    builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.download"), Component.literal("v" + pack.get("version").getAsString()))
                            .setOnPress((s) ->
                                    AlinLib.MINECRAFT.setScreen(new DownloadScreen(build(parent), pack, PepeLandHelper.onlyEmotesCheck(), modrinth))
                            ).build());
                } else {
                    builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.not_selected")).setOnPress((s) -> {
                        AlinLib.MINECRAFT.getResourcePackRepository().addPack(PepeLandHelper.getAvailablePack());
                        AlinLib.MINECRAFT.options.updateResourcePacks(AlinLib.MINECRAFT.getResourcePackRepository());
                        AlinLib.MINECRAFT.setScreen(parent);
                    }));
                }
            } else {
                if (!pack.get("version").getAsString().equals(packVersion))
                    builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.download_update"), Component.literal("v" + pack.get("version").getAsString()))
                            .setOnPress((s) ->
                                    AlinLib.MINECRAFT.setScreen(new DownloadScreen(build(parent), pack, PepeLandHelper.onlyEmotesCheck(), modrinth))
                            ).build());
                builder.addWidget(new ButtonBuilder(Component.translatable("pplhelper.pack.installed"), Component.literal("v" + packVersion)).setActive(false));
            }
        } catch (Exception ex) {
            return new ErrorScreen(ex, parent);
        }

        return builder.build();
    }
}
