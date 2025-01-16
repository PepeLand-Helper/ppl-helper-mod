package ru.kelcuprum.pplhelper.gui.screens.configs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.projects.TestProject;

import static ru.kelcuprum.alinlib.gui.Icons.CLOWNFISH;
import static ru.kelcuprum.alinlib.gui.Icons.OPTIONS;
import static ru.kelcuprum.pplhelper.utils.TabHelper.worlds;

public class TestConfigScreen {
    public Screen parent;
    public Screen build(Screen parent){
        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent, Component.translatable("pplhelper"))
                .addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.configs")).setOnPress((s) -> AlinLib.MINECRAFT.setScreen(new ConfigScreen().build(parent))).setIcon(OPTIONS));
        if(PepelandHelper.config.getBoolean("IM_A_TEST_SUBJECT", false))
            builder.addPanelWidget(new ButtonBuilder(Component.translatable("pplhelper.test"), (s) ->AlinLib.MINECRAFT.setScreen(new TestConfigScreen().build(parent))).setIcon(CLOWNFISH));

        builder.addWidget(new TextBuilder(Component.translatable("pplhelper.test")))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.test.im_a_test_subject"), false).setConfig(PepelandHelper.config,"IM_A_TEST_SUBJECT"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.test.im_a_test_subject.enable_world"), false).setConfig(PepelandHelper.config,"IM_A_TEST_SUBJECT.ENABLE_WORLD"))
                .addWidget(new SelectorBuilder(Component.translatable("pplhelper.test.im_a_test_subject.world")).setValue(worlds[0]).setList(worlds).setConfig(PepelandHelper.config,"IM_A_TEST_SUBJECT.WORLD"))
                .addWidget(new ButtonBuilder(Component.translatable("pplhelper.test.im_a_test_subject.project"), (s) -> PepelandHelper.selectedProject = PepelandHelper.selectedProject == null ? new TestProject() : null));

        return builder.build();
    }
}
