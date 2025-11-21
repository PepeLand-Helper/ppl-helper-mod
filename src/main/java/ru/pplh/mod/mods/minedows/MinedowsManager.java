package ru.pplh.mod.mods.minedows;

import net.minecraft.network.chat.Component;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.utils.ModManager;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.utils.TabHelper;

import java.util.ArrayList;

public class MinedowsManager {
    public static void register(){
        ModManager.registerModActions(() -> {
            ArrayList<Action> actions = new ArrayList<>();
            if(PepeLandHelper.playerInPPL() && TabHelper.getWorld() != TabHelper.Worlds.LOBBY){
                actions.add(new Action(() -> PepeLandHelper.executeCommand(AlinLib.MINECRAFT.player, "/lobby"), Component.literal("Lobby"), PepeLandHelper.Icons.PEPE));
            }
            return actions;
        }, true);
    }
}
