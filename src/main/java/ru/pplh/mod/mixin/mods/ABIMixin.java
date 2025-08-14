package ru.pplh.mod.mixin.mods;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.utils.FollowManager;
import ru.pplh.mod.utils.TabHelper;

import static java.lang.Integer.parseInt;

@Deprecated
@Mixin(ActionBarInfo.class)
public class ABIMixin {
    @Inject(method = "getMessage", at = @At("RETURN"), remap = false, cancellable = true)
    private static void getMessage(CallbackInfoReturnable<String> cir) {
        if (!PepeLandHelper.playerInPPL()) return;
        String msg = cir.getReturnValue();
        if (PepeLandHelper.config.getBoolean("ABI", false)) {
            msg += "\\n" + AlinLib.localization.getParsedText(Localization.fixFormatCodes(PepeLandHelper.config.getString("INFO.PPLHELPER", ActionBarInfo.localization.getLocalization("info.pplhelper", false, false, false))));
        }
        FollowManager.Coordinates coordinates = FollowManager.getCurrentCoordinates();
        if (coordinates != null && TabHelper.getWorld() != null && PepeLandHelper.config.getBoolean("SPROJECT.ABI", true)) {
            String huy = "\\n";
            String gameWorld = " "+(FollowManager.playerInCurrentLevel() ? "" : FollowManager.getLevelName(coordinates.level()));
            huy += String.format("&6%s:&r %s%s", coordinates.world().shortName, coordinates.getStringCoordinates(), gameWorld);
            LocalPlayer p = AlinLib.MINECRAFT.player;
            if (FollowManager.playerInCurrentWorld() && gameWorld.isBlank()) {
                long near = (long) FollowManager.dist(coordinates.coordinates()[0], coordinates.coordinates()[coordinates.coordinates().length-1], p.getBlockX(), p.getBlockZ());
                if (near <= PepeLandHelper.config.getNumber("SELECTED_PROJECT.AUTO_HIDE", 5).intValue()) {
                    FollowManager.resetCoordinates();
                } else huy += String.format(" &6(%s блоков от вас)&r", near);
            }


            msg += Localization.fixFormatCodes(huy);
        }
        cir.setReturnValue(msg);
    }
}
