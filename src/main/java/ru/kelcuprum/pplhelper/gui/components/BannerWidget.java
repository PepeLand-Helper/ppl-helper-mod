package ru.kelcuprum.pplhelper.gui.components;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcuprum.pplhelper.gui.TextureHelper;

import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.PACK_INFO;

public class BannerWidget extends AbstractWidget {
    public final String url;
    public final String id;

    public BannerWidget(int x, int y, int width, int height, String url, String id, Component message) {
        super(x, y, width, height, message);
        this.active = false;
        this.url = url;
        this.id = id;
    }

    private NativeImage nativeImage;

    @Override
    public int getHeight() {
        if(nativeImage == null) return super.getHeight();
        double scale = (double) nativeImage.getWidth() / width;
        return (int) (nativeImage.getHeight()/scale);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation image = TextureHelper.getBanner(url, id);
        if(image == PACK_INFO) return;
        nativeImage = TextureHelper.urlsImages.get(url);
        guiGraphics.blit(RenderType::guiTextured, image, getX(), getY(), 0.0F, 0.0F, getWidth(), getHeight(), getWidth(), getHeight());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
