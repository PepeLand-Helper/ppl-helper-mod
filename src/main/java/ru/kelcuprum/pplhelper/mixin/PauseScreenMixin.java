package ru.kelcuprum.pplhelper.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.gui.Icons;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.alinlib.info.World;
import ru.kelcuprum.pplhelper.PepelandHelper;

import java.util.List;

import static java.lang.Integer.parseInt;
import static ru.kelcuprum.alinlib.gui.Icons.DONT;
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.WHITE_PEPE;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "createPauseMenu", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout;visitWidgets(Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    void createPauseMenu(CallbackInfo ci, GridLayout gridLayout){
        if(AlinLib.MINECRAFT.hasSingleplayerServer() || AlinLib.MINECRAFT.isLocalServer() || AlinLib.MINECRAFT.getCurrentServer() == null) return;
        if(AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net") && PepelandHelper.getWorld() != PepelandHelper.Worlds.LOBBY) {
            PepelandHelper.getWorld();
            if (gridLayout != null) {
                final List<AbstractWidget> buttons = ((AccessorGridLayout) gridLayout).getChildren();
                int vanillaButtonsY = this.height / 4 + 72 - 16 + 1;
                for(AbstractWidget widget : buttons){
                    if(widget.getMessage().contains(Component.translatable("menu.feedback")) || widget.getMessage().contains(Component.translatable("menu.sendFeedback"))){
                        vanillaButtonsY = widget.getY();
                    }
                }
                buttons.add(new ButtonBuilder(Component.translatable("pplhelper.world.lobby"))
                        .setOnPress((s) -> PepelandHelper.executeCommand(AlinLib.MINECRAFT.player, "/lobby"))
                        .setSprite(Icons.CLOWNFISH)
                        .setPosition(this.width / 2 - 4 - 100 - 2 - 20, vanillaButtonsY)
                        .setSize(20, 20)
                        .build());
            }
        }
    }
}
