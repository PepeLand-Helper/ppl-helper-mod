package ru.kelcuprum.pplhelper.gui.screens.configs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.slider.SliderBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;

import static ru.kelcuprum.alinlib.gui.Icons.*;

public class ConfigScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent, Component.translatable("pplhelper"))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setIcon(OPTIONS));
        if(PepelandHelper.config.getBoolean("IM_A_TEST_SUBJECT", false))
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.test"), (s) ->AlinLib.MINECRAFT.setScreen(new TestConfigScreen().build(parent))).setIcon(CLOWNFISH));

        builder.addWidget(new TextBuilder(Component.translatable("pplhelper.configs")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.enable_toast"), true).setConfig(PepelandHelper.config,"ENABLE.TOAST"))
                .addWidget(new SliderBuilder(Component.translatable("pplhelper.configs.selected_project.auto_hide")).setDefaultValue(15).setMin(1).setMax(32).setConfig(PepelandHelper.config, "SELECTED_PROJECT.AUTO_HIDE"))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.chat")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.chat.global"), false).setConfig(PepelandHelper.config,"CHAT.GLOBAL"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.chat.global.toggle"), false).setConfig(PepelandHelper.config,"CHAT.GLOBAL.TOGGLE"))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.api")))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.api_url")).setValue("https://api-h.pplmods.ru/").setConfig(PepelandHelper.config, "API_URL"))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.urls")))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.modrinth_url")).setValue("https://modrinth.com/").setConfig(PepelandHelper.config, "MODRINTH_URL"));

        return builder.build();
    }
}
