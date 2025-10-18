package ru.pplh.mod.mixin.mods;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.utils.TabHelper;

import java.util.ArrayList;
import java.util.HashMap;

@Mixin(DesktopScreen.class)
public class MinedowsMixin {
    @Inject(method = "getActionsMainMenu", at=@At("RETURN"),cancellable = true, remap = false)
    public void getActions(CallbackInfoReturnable<HashMap<String, ArrayList<Action>>> cir){
        if(AlinLib.MINECRAFT.hasSingleplayerServer() || AlinLib.MINECRAFT.isLocalServer() || AlinLib.MINECRAFT.getCurrentServer() == null || !PepeLandHelper.config.getBoolean("MENU.LOBBY", true)) return;
        if(PepeLandHelper.playerInPPL() && TabHelper.getWorld() != TabHelper.Worlds.LOBBY) {
            HashMap<String, ArrayList<Action>> links = cir.getReturnValue();
            ArrayList<Action> system = links.get("0");
            system.add(new Action(() -> PepeLandHelper.executeCommand(AlinLib.MINECRAFT.player, "/lobby"), Component.literal("Lobby"), PepeLandHelper.Icons.PEPE));
            links.put("0", system);
            cir.setReturnValue(links);
        }
    }

}
