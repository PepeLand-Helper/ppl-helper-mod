package ru.kelcuprum.pplhelper.gui.components.oneshot.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;

public class DialogOverlay extends Screen {
    public final Screen screen;
    public final String[] dialog;
    public final Runnable runnable;
    private final int timeShow;
    private long startTime = System.currentTimeMillis();
    private long startTimeText;

    public DialogOverlay(Screen screen, String[] dialog, Runnable runnable) {
        this(screen, dialog, runnable, 1000);
    }
    public DialogOverlay(Screen screen, String[] dialog, Runnable runnable, int timeShot) {
        super(Component.empty());
        this.screen = screen;
        this.dialog = dialog.length == 0 ? new String[]{"[ГОЙДА]", "[ZOV]"} : dialog;
        this.runnable = runnable;
        this.timeShow = timeShot;
        startTimeText = startTime + timeShow;
    }

    int pos = 0;
    boolean isClose = false;
    boolean isRevertText = false;


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        if(screen != null) screen.renderBackground(guiGraphics, i, j, f);
        else renderBlurredBackground();
        long cur = System.currentTimeMillis();
        int back = (int) (127.5F * (Math.clamp((double) (cur - startTime) / timeShow, 0.0, 1.0))) << 24;
        if (isClose) back = (int) (127.5F - (127.5F * (Math.clamp((double) (cur - startTime) / timeShow, 0.0, 1.0)))) << 24;
        guiGraphics.fillGradient(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), back, back);
        if (startTimeText+100 < cur && !isRevertText ) {
            int backT = replaceAlpha(0xFFFFFFFF, (int) (255 * Math.clamp((double) (cur - startTimeText) / timeShow, 0.0, 1.0)));
            guiGraphics.drawCenteredString(minecraft.font, dialog[pos], width / 2, height / 2 - minecraft.font.lineHeight / 2, backT);
        }
    }

    static int replaceAlpha(int i, int j) {
        return i & 16777215 | j << 24;
    }


    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(i == GLFW.GLFW_KEY_SPACE || i == GLFW.GLFW_KEY_Z){
            changePosition();
            return false;
        } else return super.keyPressed(i, j, k);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        changePosition();
        return false;
    }

    public void changePosition(){
        if (startTime + 1000 < System.currentTimeMillis()) {
            if (pos + 1 == dialog.length) {
                isClose = isRevertText = true;
                startTimeText = startTime = System.currentTimeMillis();
            } else pos++;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (System.currentTimeMillis() - startTime >= timeShow && isClose) onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
    }

    @Override
    public void onClose() {
        if (isClose) {
            if (runnable != null) runnable.run();
            else AlinLib.MINECRAFT.setScreen(screen);
        }
    }
}
