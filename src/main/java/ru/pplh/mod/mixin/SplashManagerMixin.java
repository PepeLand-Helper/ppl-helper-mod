package ru.pplh.mod.mixin;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcuprum.alinlib.info.Player;
import ru.pplh.mod.PepeLandHelper;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
    @Inject(method="getSplash", at=@At("HEAD"), cancellable = true)
    public void getSplash(CallbackInfoReturnable<SplashRenderer> cir){
        if(PepeLandHelper.isTestSubject()) cir.setReturnValue(new SplashRenderer("\uE699 PepeLand Helper 2.0 Test mode"));
        else if(PepeLandHelper.isPWGood()) cir.setReturnValue(new SplashRenderer(String.format("%s, не используй альфа версии 2.0!!!!!", Player.getName())));
    }
}
