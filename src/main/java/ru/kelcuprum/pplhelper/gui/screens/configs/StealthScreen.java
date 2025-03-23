package ru.kelcuprum.pplhelper.gui.screens.configs;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.slider.SliderBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;
import static ru.kelcuprum.alinlib.gui.Icons.*;

public class StealthScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent, Component.translatable("pplhelper"))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setIcon(OPTIONS))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs.chat")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ChatConfigsScreen().build(parent))).setIcon(LIST));
        if(!FabricLoader.getInstance().getModContainer("alinlib").get().getMetadata().getVersion().getFriendlyString().startsWith("2.1.0-alpha"))
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs.stealth.title")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new StealthScreen().build(parent))).setIcon(INVISIBILITY));
        if(PepelandHelper.config.getBoolean("IM_A_TEST_SUBJECT", false))
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.test"), (s) ->AlinLib.MINECRAFT.setScreen(new TestConfigScreen().build(parent))).setIcon(CLOWNFISH));

        builder.addWidget(new TextBuilder(Component.translatable("pplhelper.configs.stealth.title")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.stealth"), false).setConfig(PepelandHelper.config, "STEALTH"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.stealth.current_world"), true).setConfig(PepelandHelper.config, "STEALTH.CURRENT_WORLD"));

        if(PepelandHelper.worldsLoaded) {
            builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.stealth.worlds")));
            for(String world : PepelandHelper.worlds){
                TabHelper.Worlds worldObject = TabHelper.getWorldByShortName(world);
                if(worldObject != null) builder.addWidget(new ButtonBooleanBuilder(worldObject.title, true).setConfig(PepelandHelper.config, String.format("STEALTH.WORLD.%s", worldObject.shortName.toUpperCase())));
            }
            builder.addWidget(new HorizontalRuleBuilder());
        }

        return builder.build();
    }
}
