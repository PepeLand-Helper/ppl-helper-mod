package ru.pplh.mod.test;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.api.events.client.ClientTickEvents;
import ru.pplh.mod.interactive.Action;
import ru.pplh.mod.interactive.InteractiveManager;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;
import static ru.kelcuprum.alinlib.gui.Colors.SEADRIVE;

public class LevelTick implements ClientTickEvents.StartTick{
    @Override
    public void onStartTick(Minecraft minecraft){
        if(InteractiveManager.currentInteractive != null){
            for(Action action : InteractiveManager.currentInteractive.actions){
                int[] pos1 = action.area.get(0);
                int[] pos2 = action.area.get(1);
                int x1 = pos1[0];
                int x2 = pos2[0];
                int y1 = pos1[1];
                int y2 = pos2[1];
                int z1 = pos1[2];
                int z2 = pos2[2];
                sendParticle(x1, y1, z1, action);
                sendParticle(x2, y1, z1, action);
                sendParticle(x2, y1, z2, action);
                sendParticle(x1, y1, z2, action);
                for(double x = x1; x<x2; x+=0.5){
                    sendParticle(x, y1, z1, action);
                    sendParticle(x, y1, z2, action);
                    sendParticle(x, y2, z1, action);
                    sendParticle(x, y2, z2, action);
                }

                for(double z = z1; z<z2; z+=0.5){
                    sendParticle(x1, y1, z, action);
                    sendParticle(x2, y1, z, action);
                    sendParticle(x1, y2, z, action);
                    sendParticle(x2, y2, z, action);
                }

                for(double y = y1; y<y2; y+=0.5){
                    sendParticle(x1, y, z1, action);
                    sendParticle(x2, y, z2, action);
                    sendParticle(x1, y, z2, action);
                    sendParticle(x2, y, z1, action);
                }

                sendParticle(x1, y2, z1, action);
                sendParticle(x2, y2, z1, action);
                sendParticle(x2, y2, z2, action);
                sendParticle(x1, y2, z2, action);
            }
        }
    }
    public static void sendParticle(double x, double y, double z, Action action){
        AlinLib.MINECRAFT.levelRenderer.addParticle(
                new DustParticleOptions(action.isExecuted ? SEADRIVE : GROUPIE, 1.0f),
                true, true, x+0.5, y+0.5, z+0.5, 0, 0, 0
        );
    }
}
