package ru.pplh.mod.gui.screens.configs;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.utils.TabHelper;

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
        if(PepeLandHelper.isTestSubject())
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.test"), (s) ->AlinLib.MINECRAFT.setScreen(new TestConfigScreen().build(parent))).setIcon(CLOWNFISH));

        builder.addWidget(new TextBuilder(Component.translatable("pplhelper.configs.stealth.title")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.stealth"), false).setConfig(PepeLandHelper.config, "STEALTH"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.stealth.current_world"), true).setConfig(PepeLandHelper.config, "STEALTH.CURRENT_WORLD"));

        if(PepeLandHelper.worldsLoaded) {
            builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.stealth.worlds")));
            for(String world : PepeLandHelper.worlds){
                TabHelper.Worlds worldObject = TabHelper.getWorldByShortName(world);
                if(worldObject != null) builder.addWidget(new ButtonBooleanBuilder(worldObject.title, true).setConfig(PepeLandHelper.config, String.format("STEALTH.WORLD.%s", worldObject.shortName.toUpperCase())));
            }
            builder.addWidget(new HorizontalRuleBuilder());
        }

        return builder.build();
    }
}
