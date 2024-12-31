package ru.kelcuprum.pplhelper.gui.configs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.slider.SliderBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.sliders.Slider;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.gui.screens.*;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.*;

public class ConfigScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.configs"))
                .addPanelWidgets(PepelandHelper.getPanelWidgets(parent, parent))

                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.enable_toast"), true).setConfig(PepelandHelper.config,"ENABLE.TOAST"))
                .addWidget(new SliderBuilder(Component.translatable("pplhelper.configs.selected_project.auto_hide")).setDefaultValue(15).setMin(1).setMax(32).setConfig(PepelandHelper.config, "SELECTED_PROJECT.AUTO_HIDE"))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.api")))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.api_url")).setValue("https://api-h.pplmods.ru/").setConfig(PepelandHelper.config, "API_URL"))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.urls")))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.modrinth_url")).setValue("https://modrinth.com/").setConfig(PepelandHelper.config, "MODRINTH_URL"));

        return builder.build();
    }
}
