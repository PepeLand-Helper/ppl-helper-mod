package ru.kelcuprum.pplhelper.interactive;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;

public class InteractiveManager {
    public static Interactive currentInteractive = null;
    public static String[] knowActions = {
            "dialog",
            "chat",
            "actionbar",
            "sound",
            "stop_sound"
    };

    public static void checkPlayerPosition(LocalPlayer player){
        if(currentInteractive != null){
            int executed = 0;
            for(Action action : currentInteractive.actions){
                if(action.type.equals("particle_circle")) {
                    action.execute();
                } else if(!action.isExecuted && !action.area.isEmpty()){
                    int[] pos1 = action.area.get(0);
                    int[] pos2 = new int[]{ (int)player.position().x, (int)player.position().y, (int)player.position().z };
                    int[] pos3 = action.area.get(1);
                    boolean playerInArea = ((pos1[0] <= pos2[0] && pos2[0] <= pos3[0]) &&
                            (pos1[1] <= pos2[1] && pos2[1] <= pos3[1]) &&
                            (pos1[2] <= pos2[2] && pos2[2] <= pos3[2]));
                    if(playerInArea) action.execute();
                } else executed++;
            }
            if(executed == currentInteractive.actions.size()){
                currentInteractive = null;
                AlinLib.MINECRAFT.getChatListener().handleSystemMessage(Component.literal("[PPL Helper] Интерактив завершен!"), false);
            }
        }
    }
}
