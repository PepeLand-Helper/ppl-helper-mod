package ru.kelcuprum.pplhelper.mixin.locatorbar;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.Gui;

//#if MC >= 12106
import net.minecraft.client.waypoints.ClientWaypointManager;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.pplhelper.utils.FollowManager;
//#endif

@Mixin(Gui.class)
public class GUIMixin {

    //#if MC >= 12106
    @WrapOperation(
            method = "nextContextualInfoState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/waypoints/ClientWaypointManager;hasWaypoints()Z"
            )
    )
    private boolean getCurrentBarType(ClientWaypointManager instance, Operation<Boolean> original) {
        if (AlinLib.MINECRAFT.player != null && FollowManager.getCurrentCoordinates() != null && FollowManager.playerInCurrentLevel() && FollowManager.playerInCurrentWorld()) {
            return true;
        } else {
            return original.call(instance);
        }
    }
    //#endif
}
