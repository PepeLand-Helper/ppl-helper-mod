package ru.pplh.mod.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//import net.minecraft.client.gui.navigation.;
//#if MC >= 12106
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
//#endif
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.Description;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.gui.TextureHelper;

import static ru.kelcuprum.alinlib.gui.Colors.*;
import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_HEIGHT;
import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;
import static ru.pplh.mod.PepeLandHelper.Icons.PACK_INFO;
import static ru.pplh.mod.PepeLandHelper.Icons.WHITE_PEPE;

public class WebImageWidget extends AbstractButton implements Description {
    public Identifier image;
    public final OnPress onPress;
    public final boolean isScale;
    public Component description;
    public boolean loadFailed = false;

    public final String url;
    public final String id;

    public WebImageWidget(int x, int y, String url, String id, Component message) {
        this(x, y, DEFAULT_WIDTH(), DEFAULT_HEIGHT, url, id, message);
    }
    ///
    public WebImageWidget(int x, int y, int width, int height, String url, String id, Component message) {
        this(x, y, width, height, url, id, false, message);
    }
    public WebImageWidget(int x, int y, int width, int height, String url, String id, boolean isScale, Component message) {
        super(x, y, width, height, message);
        this.isScale = isScale;
        this.image = null;
        this.url = url;
        this.id = id;
        this.onPress = (s) -> PepeLandHelper.confirmLinkNow(AlinLib.MINECRAFT.gui.screen(), url);
    }

    public Identifier getImage(){
        return TextureHelper.getTexture(url, id);
    }

    public int getImageWidth(){
        if(TextureHelper.urlsImages.containsKey(url)){
            return TextureHelper.urlsImages.get(url).getWidth();
        } else return width;
    }
    public int getImageHeight(){
        if(TextureHelper.urlsImages.containsKey(url)){
            return TextureHelper.urlsImages.get(url).getHeight();
        } else return height;
    }

    public int getHeight() {
        if(image == null || loadFailed) return 20;
        double scale = (double)this.width / (double)this.getImageWidth();
        int imHeight = (int)((double)this.getImageHeight() * scale);
        return this.isScale ? imHeight : super.getHeight();
    }

    public int getWidth() {
        if(image == null || loadFailed) return super.getWidth();
        double scale = (double)this.width / (double)this.getImageWidth();
        int imWidth = (int)((double)this.getImageWidth() * scale);
        return this.isScale ? imWidth : super.getWidth();
    }

    // -=-=-=-=-=-

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if(!loadFailed) {
            Identifier img = TextureHelper.getTexture(url, id);
            image = img == WHITE_PEPE ? null : img;
            loadFailed = image == PACK_INFO;
        }
        if(loadFailed || image == null) renderButton(guiGraphics, i, j, f);
        else renderImage(guiGraphics);
    }

    @Override
    public @NotNull Component getMessage() {
        return Component.empty().append(url).append(" ").append(id);
    }

    public void renderButton(GuiGraphicsExtractor guiGraphics, int i, int j, float f){
        GuiUtils.getSelected().renderBackground$widget(guiGraphics, getX(), getY(), getWidth(), getHeight(), this.active, this.isHoveredOrFocused());
        if (GuiUtils.isDoesNotFit(getMessage(), getWidth(), getHeight()))
            this.extractScrollingStringOverContents(guiGraphics.textRenderer(), getMessage(), 2);
         else {
            guiGraphics.text(AlinLib.MINECRAFT.font, url, getX() + (getHeight() - 8) / 2, getY() + (getHeight() - 8) / 2, 0xFFFFFFFF, true);
            guiGraphics.text(AlinLib.MINECRAFT.font, id, getX() + getWidth() - AlinLib.MINECRAFT.font.width(id) - ((getHeight() - 8) / 2), getY() + (getHeight() - 8) / 2, 0xFFFFFFFF);
        }
         guiGraphics.fill(getX(), getBottom()-1, getRight(), getBottom(), loadFailed ? GROUPIE : CONVICT);
    }

    public void renderImage(GuiGraphicsExtractor guiGraphics){
        if (this.isScale) {
            double scale = (double)this.width / (double)getImageWidth();
            int imWidth = (int)((double)getImageWidth() * scale);
            int imHeight = (int)((double)getImageHeight() * scale);
            guiGraphics.blit(
                    //#if MC >= 12106
                    RenderPipelines.GUI_TEXTURED,
                    //#elseif MC >= 12102
                    //$$ RenderType::guiTextured,
                    //#endif
                    this.image, this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), imWidth, imHeight);
        } else {
            guiGraphics.blit(
                    //#if MC >= 12106
                    RenderPipelines.GUI_TEXTURED,
                    //#elseif MC >= 12102
                    //$$ RenderType::guiTextured,
                    //#endif
                    this.image, this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), getImageWidth(), getImageHeight());
        }
    }

    // -=-=-=-=-=-

    @Override
    public void onPress(InputWithModifiers i) {
        if(this.onPress != null) this.onPress.onPress(this);
    }
    @Override
    public void onClick(MouseButtonEvent event, boolean b) {
        this.onPress(event);
    }
    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (this.active && this.visible) {
            if (keyEvent.key() == GLFW.GLFW_KEY_ENTER) {
                this.playDownSound(AlinLib.MINECRAFT.getSoundManager());
                this.onPress(keyEvent);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public Component getDescription() {
        return description;
    }

    public interface OnPress {
        void onPress(WebImageWidget button);
    }
}
