package ru.kelcuprum.pplhelper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import java.util.List;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout;visitWidgets(Ljava/util/function/Consumer;)V"))
    void createPauseMenu(CallbackInfo ci, @Local GridLayout gridLayout){
        if(AlinLib.MINECRAFT.hasSingleplayerServer() || AlinLib.MINECRAFT.isLocalServer() || AlinLib.MINECRAFT.getCurrentServer() == null) return;
        if(AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net") && TabHelper.getWorld() != TabHelper.Worlds.LOBBY) {
            TabHelper.getWorld();
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
                        .setSprite(PepelandHelper.Icons.PEPE)
                        .setPosition(this.width / 2 - 4 - 100 - 2 - 20, vanillaButtonsY)
                        .setSize(20, 20)
                        .build());
            }
        }
    }
}
