package ru.kelcuprum.pplhelper.mixin.april;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.gui.components.oneshot.OneShotButton;
import ru.kelcuprum.pplhelper.gui.components.oneshot.OneShotPauseButton;
import ru.kelcuprum.pplhelper.gui.components.oneshot.screen.OtherScreen;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import static ru.kelcuprum.pplhelper.utils.TabHelper.getWorld;

@Mixin(value = PauseScreen.class, priority = -1)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component component) {
        super(component);
    }

    @Shadow
    @Nullable
    private net.minecraft.client.gui.components.Button disconnectButton;

    @Inject(method = "init", at = @At("RETURN"))
    void init(CallbackInfo cl) {
        if(!PepelandHelper.isAprilFool() || !PepelandHelper.isPWGood()) return;
        clearWidgets();
        // 86
        int size = (width - 24 - 10) / 3;
        AbstractWidget helloControlify = addRenderableWidget(new ButtonBuilder(Component.translatable("menu.returnToGame")).setPosition(-20, -20).setSize(20, 20).build());
        helloControlify.visible = helloControlify.active = false;
        //12
        // width / 2 - size / 2
        // width - 12 - size
        if(PepelandHelper.playerInPPL() && getWorld() != TabHelper.Worlds.LOBBY) addRenderableWidget(new OneShotPauseButton(12, 12, size, 24, Component.translatable("pwshot.lobby"), (s) -> PepelandHelper.executeCommand(AlinLib.MINECRAFT.player, "/lobby")));
        else addRenderableWidget(new OneShotPauseButton(12, 12, size, 24, Component.translatable("pwshot.lobby"), (s) -> PepelandHelper.executeCommand(AlinLib.MINECRAFT.player, "/lobby")));
        addRenderableWidget(new OneShotPauseButton(width - 12 - size, 12, size, 24, Component.translatable("pwshot.other"), (s) -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new OtherScreen(this));
        }));

        assert this.minecraft != null;
        Component component = this.minecraft.isLocalServer() ? Component.translatable("menu.returnToMenu") : CommonComponents.GUI_DISCONNECT;
        this.disconnectButton = net.minecraft.client.gui.components.Button.builder(component, (s) -> {
        }).build();

        addRenderableWidget(new OneShotPauseButton(width / 2 - size / 2, 12, size, 24, component, (s) -> {
            onDisconnect();
        }));
    }

    @Shadow
    @Final
    protected abstract void onDisconnect();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo cl) {
        if(!PepelandHelper.isAprilFool() || !PepelandHelper.isPWGood()) return;
        guiGraphics.blitSprite(RenderType::guiTextured, ResourceLocation.fromNamespaceAndPath("pplhelper", "pause/oneshot_pause_panel"), 5, 5, width - 10, 38);
        super.render(guiGraphics, i, j, f);
        cl.cancel();
    }
}
