package ru.kelcuprum.pplhelper.gui.components;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.pplhelper.gui.TextureHelper;

import static ru.kelcuprum.alinlib.gui.Colors.BLACK;
import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.PACK_INFO;
import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.WHITE_PEPE;
import static ru.kelcuprum.pplhelper.gui.screens.message.DialogScreen.replaceAlpha;

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
    private boolean loadFailed = false;
    @Override
    public int getHeight() {
        if(nativeImage == null) return super.getHeight();
        double scale = (double) nativeImage.getWidth() / width;
        return (int) (nativeImage.getHeight()/scale);
    }
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation image = TextureHelper.getBanner(url, id);
        if(image == PACK_INFO && !loadFailed) {
            loadFailed = true;
        }
        if(image != WHITE_PEPE) nativeImage = TextureHelper.urlsImages.get(url);
        if(nativeImage == null) {
            long time = System.currentTimeMillis();
            double alpha = 0.1 + (0.3 * (time % 4000 < 2000 ? (double) (time % 2000) / 2000 : 1.0 - ((double) (time % 2000) / 2000)));
            guiGraphics.fill(getX(), getY(), getRight(), getBottom(), replaceAlpha(BLACK, (int) (255 * alpha)));
            if(loadFailed) {
                guiGraphics.enableScissor(getX(), getY(), getRight(), getBottom());
                int fireSize = (int) (height*0.75);
                int startX = (int) -(fireSize*0.5);
                for(int l = 0; true; l++){
                    if(fireSize*l+startX > width) break;
                    guiGraphics.blitSprite(RenderType::guiTextured, GuiUtils.getResourceLocation("pplhelper", "error/fire_0"), fireSize*l+startX, getBottom()-fireSize, fireSize, fireSize);
                }
                guiGraphics.disableScissor();
                guiGraphics.fillGradient(getX(), getY(), getRight(), getBottom(), 0x7f245965, 0x7F9f1b46);
                guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, "Он пропал. НЕТ! У НАС ЕГО УКРАЛИ!", getX()+(getWidth()/2), getY()+(getHeight()/2 - AlinLib.MINECRAFT.font.lineHeight / 2), -1);
            };
        }
        else guiGraphics.blit(RenderType::guiTextured, image, getX(), getY(), 0.0F, 0.0F, getWidth(), getHeight(), getWidth(), getHeight());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
