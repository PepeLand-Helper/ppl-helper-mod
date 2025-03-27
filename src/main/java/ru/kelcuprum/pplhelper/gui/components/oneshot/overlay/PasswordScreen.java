package ru.kelcuprum.pplhelper.gui.components.oneshot.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.editbox.EditBox;
import ru.kelcuprum.alinlib.gui.styles.WMStyle;
import ru.kelcuprum.pplhelper.PepeLandHelper;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;
import static ru.kelcuprum.alinlib.gui.Colors.SEADRIVE;

public class PasswordScreen extends Screen {
    public final Screen screen;
    public Component message;
    public PasswordScreen(Screen screen) {
        super(Component.literal("Введите Пароль:"));
        message = getTitle();
        this.screen = screen;
    }

    EditBox box;
    String value = "";
    @Override
    protected void init() {
        box = (EditBox) new EditBoxBuilder(Component.empty()).setResponder((s) -> {
            value = s;
        }).setStyle(new WMStyle()).setWidth(10 + minecraft.font.width("0000000")).setPosition(10, 10+ font.lineHeight+10).build();
        addRenderableWidget(box);
    }
    int pos = 0;
    boolean follow = false;

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(i == GLFW.GLFW_KEY_ESCAPE) {
            pos = 2;
            onClose();
            return false;
        }
        if(i == GLFW.GLFW_KEY_SPACE || i == GLFW.GLFW_KEY_Z || i == GLFW.GLFW_KEY_ENTER){
            if(pos == 0){
                if(i != GLFW.GLFW_KEY_ENTER) return super.keyPressed(i, j, k);
                else {
                    follow = String.valueOf(PepeLandHelper.code).equals(value);
                    message = Component.literal(String.format("%s ...", value));
                    pos++;
                    removeWidget(box);
                    return true;
                }
            } else if(pos == 1){
                message = Component.empty().append(follow ? "Доступ Разрешён!" : "Доступ Запрещён!").withColor(follow ? SEADRIVE : GROUPIE);
                pos++;
                return true;
            } else if(pos == 2) {
                onClose();
                return true;
            } return true;
        } else return super.keyPressed(i, j, k);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if(pos != 0){
            if(pos == 1){
                message = Component.empty().append(follow ? "Доступ Разрешён!" : "Доступ Запрещён!").withColor(follow ? SEADRIVE : GROUPIE);
                pos++;
                return true;
            } else if(pos == 2) {
                onClose();
                return true;
            }
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fill(0, 0, width, height, 0xFA000000);
        guiGraphics.drawString(minecraft.font, message, 10, 10, -1);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
    }

    @Override
    public void onClose() {
        if(pos == 2){
            if(follow) PepeLandHelper.config.setBoolean("april.fool.lobby_enable", true);
            minecraft.setScreen(screen);
        }
    }
}
