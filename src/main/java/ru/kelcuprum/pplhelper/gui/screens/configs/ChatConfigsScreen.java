package ru.kelcuprum.pplhelper.gui.screens.configs;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import static ru.kelcuprum.alinlib.gui.Icons.*;

public class ChatConfigsScreen {
    public Screen parent;

    public Screen build(Screen parent) {
        this.parent = parent;
        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent, Component.translatable("pplhelper"))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setIcon(OPTIONS))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs.chat")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ChatConfigsScreen().build(parent))).setIcon(LIST));
        if (!FabricLoader.getInstance().getModContainer("alinlib").get().getMetadata().getVersion().getFriendlyString().startsWith("2.1.0-alpha"))
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs.stealth.title")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new StealthScreen().build(parent))).setIcon(INVISIBILITY));
        if (PepeLandHelper.isTestSubject())
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.test"), (s) -> AlinLib.MINECRAFT.setScreen(new TestConfigScreen().build(parent))).setIcon(CLOWNFISH));

        builder.addWidget(new TextBuilder(Component.translatable("pplhelper.configs.chat")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.chat.emote_convert"), true).setConfig(PepeLandHelper.config, "CHAT.EMOTE_CONVERT"))
                .addWidget(new HorizontalRuleBuilder())
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.chat.global"), false).setConfig(PepeLandHelper.config, "CHAT.GLOBAL"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.chat.global.toggle"), true).setConfig(PepeLandHelper.config, "CHAT.GLOBAL.TOGGLE"))
                .addWidget(new TextBuilder(Component.translatable("pplhelper.configs.chat.global.description")).setType(TextBuilder.TYPE.BLOCKQUOTE))
                // Фильтры чата
                .addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.chat_filters")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.chat_filters"), false).setConfig(PepeLandHelper.config, "CHAT.FILTER"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.chat_filters.global"), false).setConfig(PepeLandHelper.config, "CHAT.FILTER.GLOBAL"));

        if (PepeLandHelper.worldsLoaded) {
            builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.chat_filters.worlds")));
            for (String world : PepeLandHelper.worlds) {
                TabHelper.Worlds worldObject = TabHelper.getWorldByShortName(world);
                if (worldObject != null)
                    builder.addWidget(new ButtonBooleanBuilder(worldObject.title, false).setConfig(PepeLandHelper.config, String.format("CHAT.FILTER.WORLD.%s", worldObject.shortName.toUpperCase())));
            }
            builder.addWidget(new HorizontalRuleBuilder());
        }
        builder.addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.chat_filters.mystery_box"), true).setConfig(PepeLandHelper.config, "CHAT.FILTER.MYSTERY_BOX"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.chat_filters.leave"), false).setConfig(PepeLandHelper.config, "CHAT.FILTER.LEAVE"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.chat_filters.join"), false).setConfig(PepeLandHelper.config, "CHAT.FILTER.JOIN"))
                .addWidget(new TextBuilder(Component.literal("Пример: Kel_Caffeine, GRUI_72, fELuGOz, PWGoood")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(Colors.CLOWNFISH))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.chat_filters.friends")).setValue("PWGoood, Gwinsen, Pooshka").setConfig(PepeLandHelper.config, "CHAT.FILTER.FRIENDS"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.chat_filters.words"), false).setConfig(PepeLandHelper.config, "CHAT.FILTER.WORDS"))
                .addWidget(new TextBuilder(Component.literal("Пример: матч, хоккей, куплю, продам, продаст, купить, пипохуй")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(Colors.CLOWNFISH))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.chat_filters.nwords")).setValue("хоккей, хоккейный, матч").setConfig(PepeLandHelper.config, "CHAT.FILTER.NWORDS"))

                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.chat_filters.blacklist"), false).setConfig(PepeLandHelper.config, "CHAT.FILTER.BLACKLIST"))
                .addWidget(new TextBuilder(Component.literal("Пример: LeoBarn, Anton_Gandon")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(Colors.CLOWNFISH))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.chat_filters.gandons")).setValue("GaszovayaPlita").setConfig(PepeLandHelper.config, "CHAT.FILTER.GANDONS"));

        return builder.build();
    }
}
