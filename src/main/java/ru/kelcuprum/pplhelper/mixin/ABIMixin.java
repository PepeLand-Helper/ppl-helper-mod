package ru.kelcuprum.pplhelper.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import static java.lang.Integer.parseInt;
import static ru.kelcuprum.pplhelper.PepelandHelper.*;

@Mixin(ActionBarInfo.class)
public class ABIMixin {
    @Inject(method = "getMessage", at = @At("RETURN"), remap = false, cancellable = true)
    private static void getMessage(CallbackInfoReturnable<String> cir) {
        if (!PepelandHelper.playerInPPL()) return;
        String msg = cir.getReturnValue();
        if (PepelandHelper.config.getBoolean("ABI", false)) {
            msg += AlinLib.localization.getParsedText(Localization.fixFormatCodes(PepelandHelper.config.getString("INFO.PPLHELPER", ActionBarInfo.localization.getLocalization("info.pplhelper", false, false, false))));
        }
        if (PepelandHelper.selectedProject != null && TabHelper.getWorld() != null && PepelandHelper.config.getBoolean("SPROJECT.ABI", true)) {
            String huy = "\\n";
            String parsedCoordinates = getStringSelectedProjectCoordinates();
            String gameWorld = "";
            if (parsedCoordinates.isEmpty()) {
                parsedCoordinates = getStringSelectedProjectCoordinatesButWithRofls();
                gameWorld = String.format(" (%s)", getWorldStringSelectedProjectCoordinatesButWithRofls());
            }
            huy += String.format("&6%s:&r %s%s", PepelandHelper.selectedProject.world, parsedCoordinates, gameWorld);
            LocalPlayer p = AlinLib.MINECRAFT.player;
            if (gameWorld.isEmpty() && p != null && PepelandHelper.selectedProject.world.equalsIgnoreCase(TabHelper.getWorld().shortName)) {
                String[] args = parsedCoordinates.split(" ");
                int near = (int) dist(parseInt(args[0]), parseInt(args[args.length - 1]), p.getBlockX(), p.getBlockZ());
                if (near <= PepelandHelper.config.getNumber("SELECTED_PROJECT.AUTO_HIDE", 5).intValue()) {
                    PepelandHelper.selectedProject = null;
                } else huy += String.format(" &6(%s блоков от вас)&r", near);
            }


            msg += Localization.fixFormatCodes(huy);
        }
        cir.setReturnValue(msg);
    }
}
