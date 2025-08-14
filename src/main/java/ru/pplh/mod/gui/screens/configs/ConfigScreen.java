package ru.pplh.mod.gui.screens.configs;

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
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.gui.screens.message.ErrorScreen;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;
import static ru.kelcuprum.alinlib.gui.Icons.*;

public class ConfigScreen {
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

        builder.addWidget(new TextBuilder(Component.translatable("pplhelper.configs")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.menu.lobby"), true).setConfig(PepeLandHelper.config,"MENU.LOBBY"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.menu.lobby.alinlib"), false).setConfig(PepeLandHelper.config,"MENU.LOBBY.ALINLIB"))
                .addWidget(new SliderBuilder(Component.translatable("pplhelper.configs.selected_project.auto_hide")).setDefaultValue(5).setMin(1).setMax(32).setConfig(PepeLandHelper.config, "SELECTED_PROJECT.AUTO_HIDE"));
        if(PepeLandHelper.isTestSubject()) builder.addPanelWidget(new ButtonBuilder(Component.literal("Crash me!")).setIcon(DONT).setOnPress((s) -> {
            try{
                throw new RuntimeException("Эта карусель крутится и никогда не остановится!");
            } catch (Exception ex){
                AlinLib.MINECRAFT.setScreen(new ErrorScreen(ex, AlinLib.MINECRAFT.screen));
            }
        }));
        if(PepeLandHelper.isTestSubject()) builder.addPanelWidget(new ButtonBuilder(Component.literal("Crash me! (без exception)")).setIcon(DONT).setOnPress((s) -> {
            try{
                throw new RuntimeException("Эта карусель крутится и никогда не остановится!");
            } catch (Exception ex){
                AlinLib.MINECRAFT.setScreen(new ErrorScreen(AlinLib.MINECRAFT.screen));
            }
        }));
        if(PepeLandHelper.isInstalledABI) {
            builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.abi.title")));
            if(FabricLoader.getInstance().getModContainer("actionbarinfo").get().getMetadata().getVersion().getFriendlyString().startsWith("1."))
                builder.addWidget(new TextBuilder(Component.translatable("pplhelper.configs.abi.legacy")).setType(TextBuilder.TYPE.BLOCKQUOTE).setColor(GROUPIE));
            builder.addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.abi"), false).setConfig(PepeLandHelper.config, "ABI"))
                    .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.abi.info")).setValue(PepeLandHelper.config.getString("INFO.PPLHELPER", ActionBarInfo.localization.getLocalization("info.pplhelper", false, false, false))).setConfig(PepeLandHelper.config, "INFO.PPLHELPER"))
                    .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.sproject.abi"), true).setConfig(PepeLandHelper.config, "SPROJECT.ABI"));
        }
        if(PepeLandHelper.isInstalledSailStatus) {
            builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.sailstatus")))
                    .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.sailstatus.art")).setValue("https://wf.kelcu.ru/icons/mc_brush.png").setConfig(PepeLandHelper.config, "SAILSTATUS.ASSETS.WORLD_ART"))
                    .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.sailstatus.art.old")).setValue("https://wf.kelcu.ru/icons/mc_brush.png").setConfig(PepeLandHelper.config, "SAILSTATUS.ASSETS.WORLD_ART.OLD"));
        }
                builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.project.schematic")))
                        .addWidget(new SliderBuilder(Component.translatable("pplhelper.project.schematic.total_blocks")).setMin(25).setMax(1000).setDefaultValue(50).setConfig(PepeLandHelper.config, "SCHEMATIC.MAX_SIZE"));

        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.timer")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.timer.restart"), true).setConfig(PepeLandHelper.config,"TIMER.RESTART"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.timer.join"), true).setConfig(PepeLandHelper.config,"TIMER.JOIN"));

//        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.updater")))
//                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.updater.notice"), true).setConfig(PepeLandHelper.config,"PPLH.NOTICE"))
//                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.updater.auto_update"), false).setConfig(PepeLandHelper.config,"PPLH.AUTO_UPDATE"))
//                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.q.two_dot_zero_update"), true).setConfig(PepeLandHelper.config,"UPDATER.FOLLOW_TWO_DOT_ZERO"));
        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.api")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.configs.offline_mode"), false).setConfig(PepeLandHelper.config,"OFFLINE_MODE") )
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.api_url")).setValue("https://a-api.pplh.ru/").setConfig(PepeLandHelper.config, "API_URL"))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.auth_url")).setValue("https://auth.pplh.ru/").setConfig(PepeLandHelper.config, "oauth.url"))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("pplhelper.configs.urls")))
                .addWidget(new EditBoxBuilder(Component.translatable("pplhelper.configs.modrinth_url")).setValue("https://modrinth.com/").setConfig(PepeLandHelper.config, "MODRINTH_URL"));
        return builder.build();
    }
}
