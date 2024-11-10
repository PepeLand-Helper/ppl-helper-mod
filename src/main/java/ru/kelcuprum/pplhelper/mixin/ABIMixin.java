package ru.kelcuprum.pplhelper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.alinlib.info.World;
import ru.kelcuprum.pplhelper.PepelandHelper;

@Mixin(ActionBarInfo.class)
public class ABIMixin {
    @Inject(method = "getMessage", at=@At("RETURN"), remap = false, cancellable = true)
    private static void getMessage(CallbackInfoReturnable<String> cir){
        if(PepelandHelper.selectedProject == null) return;
        String huy = "\\n";
        if(World.getCodeName().equals("minecraft:overworld") && PepelandHelper.selectedProject.coordinates$overworld != null && !PepelandHelper.selectedProject.coordinates$overworld.isEmpty())
            huy += String.format("PPL Helper: %s (%s)", PepelandHelper.selectedProject.coordinates$overworld, PepelandHelper.selectedProject.world);
        else if(World.getCodeName().equals("minecraft:the_nether") && PepelandHelper.selectedProject.coordinates$nether != null && !PepelandHelper.selectedProject.coordinates$nether.isEmpty())
            huy += String.format("PPL Helper: %s (%s)", PepelandHelper.selectedProject.coordinates$nether, PepelandHelper.selectedProject.world);
        else if(World.getCodeName().equals("minecraft:the_end") && PepelandHelper.selectedProject.coordinates$end != null && !PepelandHelper.selectedProject.coordinates$end.isEmpty())
            huy += String.format("PPL Helper: %s (%s)", PepelandHelper.selectedProject.coordinates$end, PepelandHelper.selectedProject.world);
        cir.setReturnValue(cir.getReturnValue()+huy);
    }
}
