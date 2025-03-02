package ru.kelcuprum.pplhelper.mixin.april;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.gui.components.oneshot.OneShotButton;

@Mixin(value = TitleScreen.class, priority = -1)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("RETURN"))
    void init(CallbackInfo cl) {
        if(!PepelandHelper.isAprilFool() || !PepelandHelper.isPWGood()) return;
        clearWidgets();
        int bHeight = font.lineHeight + 4;
        int bHeight2 = (bHeight + 3);
        int size = 6;
        int y = height - (bHeight2 * size);
        int bWidth = font.width("...");
        Component[] texts = {
                Component.translatable("pwshot.single"),
                Component.translatable("pwshot.start"),
                Component.translatable("pwshot.start.alt"),
                Component.translatable("pwshot.exit"),
                Component.literal("..."),
        };
        for (Component text : texts) {
            int i = font.width(text) + 5;
            bWidth = Math.max(bWidth, i);
        }
        int x = width - bWidth - 45;

        assert this.minecraft != null;
        addRenderableWidget(new OneShotButton(x, y, bWidth, bHeight, Component.translatable("pwshot.single"), true, (OnPress) -> {
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }));
        y += bHeight2;

        addRenderableWidget(new OneShotButton(x, y, bWidth, bHeight, Component.translatable("pwshot.start"), true, (OnPress) -> {
            ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString("play.pepeland.net"), new ServerData("ppl", "play.pepeland.net", ServerData.Type.OTHER), false, null);
        }));
        y += bHeight2;

        addRenderableWidget(new OneShotButton(x, y, bWidth, bHeight, Component.translatable("pwshot.start.alt"), true, (OnPress) -> {
            ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString("alt.play.pepeland.net"), new ServerData("ppl", "alt.play.pepeland.net", ServerData.Type.OTHER), false, null);
        }));
        y += bHeight2;

        addRenderableWidget(new OneShotButton(x, y, bWidth, bHeight, Component.translatable("pwshot.options"), true, (OnPress) -> {
            this.minecraft.setScreen(new OptionsScreen(this, minecraft.options));
        }));
        y += bHeight2;

        addRenderableWidget(new OneShotButton(x, y, bWidth, bHeight, Component.translatable("pwshot.exit"), true, (OnPress) -> {
            this.minecraft.destroy();
        }));
        y += bHeight2;

        addRenderableWidget(new OneShotButton(x, y, bWidth, bHeight, Component.literal("..."), true, (OnPress) ->ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString("play.cubecraft.net"), new ServerData("cubecraft", "play.cubecraft.net", ServerData.Type.OTHER), false, (TransferState)null)));
    }

    @Shadow
    private long fadeInStart;
    @Shadow
    private boolean fading;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo cl) {
        if(!PepelandHelper.isAprilFool() || !PepelandHelper.isPWGood()) return;
        if (this.fadeInStart == 0L && this.fading) {
            this.fadeInStart = Util.getMillis();
        }
        guiGraphics.fill(0, 0, width, height, 0xFF220325);
        int w = 427 * minecraft.options.guiScale().get();
        int h = 242 * minecraft.options.guiScale().get();
        if(guiGraphics.guiWidth() < w){
            w = w / minecraft.options.guiScale().get();
            h = h / minecraft.options.guiScale().get();
        }
        guiGraphics.drawString(minecraft.font, "просьба не ругать за арт, я криворукий", 2, height-2-minecraft.font.lineHeight, 0x0aFFFFFF);
        guiGraphics.blitSprite(RenderType::guiTextured, ResourceLocation.fromNamespaceAndPath("pplhelper", "sky"), 0, 0, width, height);
        guiGraphics.blit(RenderType::guiTextured, ResourceLocation.fromNamespaceAndPath("pplhelper", "textures/gui/sprites/pwshot.png"), width-w, height-h, 0, 0, w, h, w, h);
        super.render(guiGraphics, i, j, f);
        cl.cancel();
    }
}
