package ru.pplh.mod.gui.style;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
//#if MC >= 12106
import net.minecraft.client.renderer.RenderPipelines;
//#endif
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.styles.AbstractStyle;

public class VanillaLikeStyle extends AbstractStyle {
    private static final WidgetSprites SPRITES = new WidgetSprites(GuiUtils.getResourceLocation("widget/button"), GuiUtils.getResourceLocation("widget/button_disabled"), GuiUtils.getResourceLocation("widget/button_highlighted"));
    private static final ResourceLocation SLIDER_SPRITE = GuiUtils.getResourceLocation("widget/slider");
    private static final ResourceLocation HIGHLIGHTED_SPRITE = GuiUtils.getResourceLocation("widget/slider_highlighted");
    private static final ResourceLocation SLIDER_HANDLE_SPRITE = GuiUtils.getResourceLocation("widget/slider_handle");
    private static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_SPRITE = GuiUtils.getResourceLocation("widget/slider_handle_highlighted");
    public VanillaLikeStyle() {
        super("vanilla", Component.literal("Vanilla-Like (PepeLand Helper)"));
    }

    @Override
    public void renderBackground$widget(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean active, boolean isHoveredOrFocused) {
        guiGraphics.blitSprite(
                //#if MC >= 12106
                RenderPipelines.GUI_TEXTURED,
                //#elseif MC >= 12102
                //$$ RenderType::guiTextured,
                //#endif
                SPRITES.get(active, isHoveredOrFocused), x, y, width, height);
    }

    @Override
    public void renderBackground$slider(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean active, boolean isHoveredOrFocused, double v) {
        guiGraphics.blitSprite(
                //#if MC >= 12106
                RenderPipelines.GUI_TEXTURED,
                //#elseif MC >= 12102
                //$$ RenderType::guiTextured,
                //#endif
                this.getSprite(isHoveredOrFocused), x, y, width, height);
        guiGraphics.blitSprite(
                //#if MC >= 12106
                RenderPipelines.GUI_TEXTURED,
                //#elseif MC >= 12102
                //$$ RenderType::guiTextured,
                //#endif
                this.getHandleSprite(isHoveredOrFocused), x + (int)(v * (double)(width - 8)), y, 8, height);
    }
    private ResourceLocation getSprite(boolean isFocused) {
        return isFocused ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
    }

    private ResourceLocation getHandleSprite(boolean isHovered) {
        return !isHovered ? SLIDER_HANDLE_SPRITE : SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
    }
}
