package ru.kelcuprum.pplhelper.gui.configs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.gui.configs.test.TestProject;
import ru.kelcuprum.pplhelper.gui.screens.builder.ScreenBuilder;

import static ru.kelcuprum.pplhelper.TabHelper.worlds;

public class TestConfigScreen {
    public Screen parent;
    public Screen build(Screen parent){
        this.parent = parent;
        ScreenBuilder builder = new ScreenBuilder(parent, Component.translatable("pplhelper.test"))
                .addPanelWidgets(PepelandHelper.getPanelWidgets(parent, parent))

                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.test.im_a_test_subject"), false).setConfig(PepelandHelper.config,"IM_A_TEST_SUBJECT"))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("pplhelper.test.im_a_test_subject.enable_world"), false).setConfig(PepelandHelper.config,"IM_A_TEST_SUBJECT.ENABLE_WORLD"))
                .addWidget(new SelectorBuilder(Component.translatable("pplhelper.test.im_a_test_subject.world")).setValue(worlds[0]).setList(worlds).setConfig(PepelandHelper.config,"IM_A_TEST_SUBJECT.WORLD"))
                .addWidget(new ButtonBuilder(Component.translatable("pplhelper.test.im_a_test_subject.project"), (s) -> PepelandHelper.selectedProject = PepelandHelper.selectedProject == null ? new TestProject() : null));

        return builder.build();
    }
}
