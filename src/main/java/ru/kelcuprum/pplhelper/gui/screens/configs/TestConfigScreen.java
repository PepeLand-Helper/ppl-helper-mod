package ru.kelcuprum.pplhelper.gui.screens.configs;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.api.components.projects.TestProject;
import ru.kelcuprum.pplhelper.utils.FollowManager;

import static ru.kelcuprum.alinlib.gui.Icons.*;
import static ru.kelcuprum.pplhelper.utils.TabHelper.worlds;

public class TestConfigScreen {
    public Screen parent;
    public Screen build(Screen parent){
        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent, Component.translatable("pplhelper"))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setIcon(OPTIONS))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs.chat")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ChatConfigsScreen().build(parent))).setIcon(LIST));
        if(!FabricLoader.getInstance().getModContainer("alinlib").get().getMetadata().getVersion().getFriendlyString().startsWith("2.1.0-alpha"))
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs.stealth.title")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new StealthScreen().build(parent))).setIcon(INVISIBILITY));
        if(PepeLandHelper.isTestSubject())
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.test"), (s) ->AlinLib.MINECRAFT.setScreen(new TestConfigScreen().build(parent))).setIcon(CLOWNFISH));

        builder.addWidget(new TextBuilder(Component.translatable("pplhelper.test")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.test.im_a_test_subject"), false).setConfig(PepeLandHelper.config,"IM_A_TEST_SUBJECT"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.test.im_a_test_subject.enable_world"), false).setConfig(PepeLandHelper.config,"IM_A_TEST_SUBJECT.ENABLE_WORLD"))
                .addWidget(new SelectorBuilder(Component.translatable("pplhelper.test.im_a_test_subject.world")).setValue(worlds[0]).setList(worlds).setConfig(PepeLandHelper.config,"IM_A_TEST_SUBJECT.WORLD"))
                .addWidget(new ButtonBuilder(Component.translatable("pplhelper.test.im_a_test_subject.project"), (s) -> {
                    if(FollowManager.project == null) FollowManager.setCoordinates(new TestProject());
                    else FollowManager.resetCoordinates();
                }))
                .addWidget(new HorizontalRuleBuilder(Component.literal("Первое апреля")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.test.im_a_test_subject.april"), false).setConfig(PepeLandHelper.config,"IM_A_TEST_SUBJECT.APRIL"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.test.im_a_test_subject.april.pwgood"), false).setConfig(PepeLandHelper.config,"IM_A_TEST_SUBJECT.PWGOOD"));


        return builder.build();
    }
}
