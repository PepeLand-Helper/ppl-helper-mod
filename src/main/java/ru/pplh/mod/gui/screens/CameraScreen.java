package ru.pplh.mod.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.pplh.mod.gui.components.VerticalConfigureScrolWidget;
import ru.pplh.mod.utils.CameraManager;

import java.util.ArrayList;
import java.util.List;

public class CameraScreen extends Screen {
    public final ArrayList<CameraManager.Camera> cameras;
    public final Screen parent;
    public CameraScreen(Screen parent, ArrayList<CameraManager.Camera> cameras) {
        super(Component.translatable("pplhelper.camera"));
        this.parent = parent;
        this.cameras = cameras;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        if(isBreakCam){
            TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            TextureSetup textureSetup = TextureSetup.doubleTexture(textureManager.getTexture(TheEndPortalRenderer.END_SKY_LOCATION).getTextureView(), textureManager.getTexture(TheEndPortalRenderer.END_PORTAL_LOCATION).getTextureView());
            guiGraphics.fill(RenderPipelines.END_PORTAL, textureSetup, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight());
            guiGraphics.drawCenteredString(font, Component.translatable("pplhelper.camera.break"), width/2, (height/2)-(font.lineHeight/2), -1);
        }
    }
    public boolean isInited = false;

    private VerticalConfigureScrolWidget scroller_pages;
    private List<AbstractWidget> widgets_pages = new ArrayList<>();
    @Override
    public void init() {
        if(!isInited) {
            isInited = true;
            CameraManager.setCamera(cameras.getFirst());
        }
        widgets_pages.clear();
        int x = 5;
        int y = 5;
        int finalX = x;
        this.scroller_pages = addRenderableWidget(new VerticalConfigureScrolWidget(x, 27, width-10, 3, Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            for (AbstractWidget widget : widgets_pages) {
                if (widget.visible) {
                    widget.setPosition((finalX + (int) (scroller.innerHeight - scroller.scrollAmount())), y);
                    scroller.innerHeight += (widget.getWidth() + 5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight -= 13;
        }));
        int i = 0;
        for(CameraManager.Camera camera : cameras){
            Component component = Component.translatable("pplhelper.camera.button", i);
            ButtonBuilder builder = new ButtonBuilder(component);
            builder.setWidth(12+font.width(component));
            builder.setOnPress((s) -> {
                CameraManager.setCamera(camera);
            });
            builder.setPosition(x, y);
            x+=12+5+font.width(component);
            widgets_pages.add(builder.build());
            i++;
        }
        addWidgetsToScroller(widgets_pages, scroller_pages);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets) {
        addWidgetsToScroller(widgets, this.scroller_pages);
    }


    public void addWidgetsToScroller(AbstractWidget widget) {
        addWidgetsToScroller(widget, this.scroller_pages);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets, ConfigureScrolWidget scroller) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget, scroller);
    }
    public void addWidgetsToScroller(AbstractWidget widget, ConfigureScrolWidget scroller) {
        widget.setY(-100);
        scroller.addWidget(widget);
        this.addWidget(widget);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets, VerticalConfigureScrolWidget scroller) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget, scroller);
    }
    public void addWidgetsToScroller(AbstractWidget widget, VerticalConfigureScrolWidget scroller) {
        widget.setY(-100);
        scroller.addWidget(widget);
        this.addWidget(widget);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void left(boolean isMouse){
        CameraManager.xRot = Math.clamp(CameraManager.xRot-(isMouse ? 0.25f : 1.0f), CameraManager.currentCamera.rotation()-60, CameraManager.currentCamera.rotation()+60);
    }
    public void right(boolean isMouse){
        CameraManager.xRot = Math.clamp(CameraManager.xRot+(isMouse ? 0.25f : 1.0f), CameraManager.currentCamera.rotation()-60, CameraManager.currentCamera.rotation()+60);
    }
    public void top(boolean isMouse){
        CameraManager.yRot = Math.clamp(CameraManager.yRot-(isMouse ? 0.25f : 1.0f), -30f, 60f);
    }
    public void down(boolean isMouse){
        CameraManager.yRot = Math.clamp(CameraManager.yRot+(isMouse ? 0.25f : 1.0f), -30f, 60f);
    }

    @Override
    public void onClose() {
        CameraManager.setCamera(null);
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    //
    public boolean isF1 = false;
    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        if(isF1) return;
        guiGraphics.enableScissor(5, 5, width-5, 30);
        if (scroller_pages != null) for (AbstractWidget widget : scroller_pages.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if(isF1) return false;
        int size = width-10;
        int x = 5;
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller_pages != null && scroller_pages.widgets.contains(guiEventListener)) {
                if ((d >= x && d <= x + size) && e <= 30)
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
            } else if (guiEventListener.mouseClicked(d, e, i)) {
                st = false;
                selected = guiEventListener;
                break;
            }
        }

        this.setFocused(selected);
        if (i == 0)
            this.setDragging(true);

        return st;
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if(!scr && scroller_pages != null && mouseY < 30 && !isF1) scr = scroller_pages.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        else {
            if(scrollY > 0) CameraManager.fov = Math.clamp(CameraManager.fov-1f, 10f, 110f);
            else CameraManager.fov = Math.clamp(CameraManager.fov+1f, 10f, 110f);
        }
        return scr;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if(e>30) {
            if (f < 0) left(true);
            else if (f > 0) right(true);
            if (g < 0) top(true);
            else if (g > 0) down(true);
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(i == GLFW.GLFW_KEY_F1) isF1 = !isF1;
        if(i == GLFW.GLFW_KEY_RIGHT) {
            right(false);
            return true;
        }
        if(i == GLFW.GLFW_KEY_LEFT) {
            left(false);
            return true;
        }
        if(i == GLFW.GLFW_KEY_UP) {
            top(false);
            return true;
        }
        if(i == GLFW.GLFW_KEY_DOWN) {
            down(false);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    public boolean isBreakCam = false;

    @Override
    public void tick() {
        if (scroller_pages != null) scroller_pages.onScroll.accept(scroller_pages);
        BlockState blockState = minecraft.level.getBlockState(CameraManager.currentCamera.position());
        isBreakCam = !(blockState.is(Blocks.PLAYER_HEAD) || blockState.is(Blocks.PLAYER_WALL_HEAD));
        if(blockState.hasBlockEntity()){
            if(minecraft.level.getBlockEntity(CameraManager.currentCamera.position()) != CameraManager.currentCamera.blockEntity()) isBreakCam = true;
        }
        super.tick();
    }
}
