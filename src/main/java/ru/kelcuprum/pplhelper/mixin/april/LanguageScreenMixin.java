package ru.kelcuprum.pplhelper.mixin.april;

import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.pplhelper.PepeLandHelper;

import static ru.kelcuprum.alinlib.gui.Icons.THANKS;

@Mixin(value = LanguageSelectScreen.class, priority = -1)
public class LanguageScreenMixin extends OptionsSubScreen {
    protected LanguageScreenMixin(Screen screen, Options options) {
        super(screen, options, Component.empty());
    }

    @Inject(method = "onDone", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo ci) {
        if(!PepeLandHelper.isAprilFool() || !PepeLandHelper.isPWGood()) return;
        new ToastBuilder().setTitle(Component.literal("aetenae")).setMessage(Component.literal("ого \uE701 пошел нахуй")).setDisplayTime(7500).setType(ToastBuilder.Type.WARN).setIcon(THANKS).buildAndShow();
        this.minecraft.setScreen(this.lastScreen);
        ci.cancel();
    }

    @Shadow @Override
    protected void addOptions() {

    }
}
