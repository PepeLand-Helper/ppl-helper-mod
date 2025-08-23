package ru.pplh.mod.mixin.locatorbar;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.Gui;

//#if MC >= 12106
import net.minecraft.client.waypoints.ClientWaypointManager;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.utils.FollowManager;
import ru.pplh.mod.utils.TabHelper;
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
        if (AlinLib.MINECRAFT.player != null && ((FollowManager.getCurrentCoordinates() != null && FollowManager.playerInCurrentLevel() && FollowManager.playerInCurrentWorld() && PepeLandHelper.config.getBoolean("LOCATOR_BAR.FOLLOW", true)) || (TabHelper.getWorld() == TabHelper.Worlds.TRADE && PepeLandHelper.config.getBoolean("LOCATOR_BAR.TRADE", true)))) {
            return true;
        } else {
            return original.call(instance);
        }
    }
    //#endif
}
