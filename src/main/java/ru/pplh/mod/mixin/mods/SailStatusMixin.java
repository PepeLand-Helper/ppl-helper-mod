package ru.pplh.mod.mixin.mods;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcuprum.alinlib.info.World;
import ru.pplh.mod.PepeLandHelper;
import ru.kelcuprum.sailstatus.info.PresenceWorld;

@Mixin(PresenceWorld.class)
public class SailStatusMixin {

    @Inject(method="getAssets", at=@At("HEAD"), cancellable = true, remap = false)
    private static void getAssets(CallbackInfoReturnable<String> cir){
        switch (World.getCodeName()){
            case "minecraft:world_art" -> cir.setReturnValue(PepeLandHelper.config.getString("SAILSTATUS.ASSETS.WORLD_ART", "https://wf.kelcu.ru/icons/mc_brush.png"));
            case "minecraft:world_art_old" -> cir.setReturnValue(PepeLandHelper.config.getString("SAILSTATUS.ASSETS.WORLD_ART.OLD", "https://wf.kelcu.ru/icons/mc_brush.png"));
        }
    }
}
