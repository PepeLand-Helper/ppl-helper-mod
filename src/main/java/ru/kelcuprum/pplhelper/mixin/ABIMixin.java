package ru.kelcuprum.pplhelper.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.info.World;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.TabHelper;

import static java.lang.Integer.parseInt;
import static ru.kelcuprum.pplhelper.PepelandHelper.dist;
import static ru.kelcuprum.pplhelper.PepelandHelper.getStringSelectedProjectCoordinates;

@Mixin(ActionBarInfo.class)
public class ABIMixin {
    @Inject(method = "getMessage", at=@At("RETURN"), remap = false, cancellable = true)
    private static void getMessage(CallbackInfoReturnable<String> cir){
        if(PepelandHelper.selectedProject == null || TabHelper.getWorld() == null) return;
        if(!PepelandHelper.config.getBoolean("SPROJECT.ABI", true)) return;
        String huy = "\\n";
        String parsedCoordinates = getStringSelectedProjectCoordinates();
        if(parsedCoordinates.isEmpty()) return;
        huy += String.format("&6%s:&r %s", PepelandHelper.selectedProject.world, parsedCoordinates);
        LocalPlayer p = AlinLib.MINECRAFT.player;
        if(p != null && PepelandHelper.selectedProject.world.equalsIgnoreCase(TabHelper.getWorld().shortName)) {
            String[] args = parsedCoordinates.split(" ");
            int near = (int) dist(parseInt(args[0]), parseInt(args[args.length-1]),p.getBlockX(), p.getBlockZ());
            if(near <= PepelandHelper.config.getNumber("SELECTED_PROJECT.AUTO_HIDE", 15).intValue()){
                PepelandHelper.selectedProject = null;
                return;
            } else huy+= String.format(" &6(%s блоков от вас)&r", near);
        }
        cir.setReturnValue(cir.getReturnValue()+Localization.fixFormatCodes(huy));
    }
}
