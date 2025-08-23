package ru.pplh.mod.test;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.api.events.client.ClientTickEvents;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.interactive.Action;
import ru.pplh.mod.interactive.InteractiveManager;
import ru.pplh.mod.utils.LocatorBarRender;
import ru.pplh.mod.utils.TabHelper;
import ru.pplh.mod.utils.TradeManager;

import static ru.kelcuprum.alinlib.gui.Colors.GROUPIE;
import static ru.kelcuprum.alinlib.gui.Colors.SEADRIVE;

public class LevelTick implements ClientTickEvents.StartTick{
    @Override
    public void onStartTick(Minecraft minecraft){
        if(InteractiveManager.currentInteractive != null && PepeLandHelper.config.getBoolean("IM_A_TEST_SUBJECT.PARTICLES.INTERACTIVE", false)){
            for(Action action : InteractiveManager.currentInteractive.actions){
                int[] pos1 = action.area.get(0);
                int[] pos2 = action.area.get(1);
                sendCuboidParticles(new Vec3(pos1[0], pos1[1], pos1[2]), new Vec3(pos2[0], pos2[1], pos2[2]), action.isExecuted ? SEADRIVE : GROUPIE);
            }
        }
        if(TabHelper.getWorld() == TabHelper.Worlds.TRADE && PepeLandHelper.config.getBoolean("LOCATOR_BAR.TRADE", true) && PepeLandHelper.config.getBoolean("IM_A_TEST_SUBJECT.PARTICLES.TRADE", false)){
            for(TradeManager.Category category : TradeManager.categories){
                Vec3 pos = category.center();
                for(double y = category.pos1().y; y<category.pos2().y;y++) sendParticle(pos.x, y, pos.z, 0xFFFFFF);
                if(minecraft.player != null) sendCuboidParticles(category.pos1(), category.pos2(), LocatorBarRender.playerInArea(minecraft.player.position(), category.pos1(), category.pos2()) ? SEADRIVE : category.color());
            }
        }
    }
    public static void sendCuboidParticles(Vec3 pos1, Vec3 pos2, int color){
        double x1 = pos1.x;
        double x2 = pos2.x;
        double y1 = pos1.y;
        double y2 = pos2.y;
        double z1 = pos1.z;
        double z2 = pos2.z;
        sendParticle(x1, y1, z1, color);
        sendParticle(x2, y1, z1, color);
        sendParticle(x2, y1, z2, color);
        sendParticle(x1, y1, z2, color);
        for(double x = x1; x<x2; x+=0.5){
            sendParticle(x, y1, z1, color);
            sendParticle(x, y1, z2, color);
            sendParticle(x, y2, z1, color);
            sendParticle(x, y2, z2, color);
        }

        for(double z = z1; z<z2; z+=0.5){
            sendParticle(x1, y1, z, color);
            sendParticle(x2, y1, z, color);
            sendParticle(x1, y2, z, color);
            sendParticle(x2, y2, z, color);
        }

        for(double y = y1; y<y2; y+=0.5){
            sendParticle(x1, y, z1, color);
            sendParticle(x2, y, z2, color);
            sendParticle(x1, y, z2, color);
            sendParticle(x2, y, z1, color);
        }

        sendParticle(x1, y2, z1, color);
        sendParticle(x2, y2, z1, color);
        sendParticle(x2, y2, z2, color);
        sendParticle(x1, y2, z2, color);
    }
    public static void sendParticle(double x, double y, double z, int color){
        AlinLib.MINECRAFT.levelRenderer.addParticle(
                new DustParticleOptions(color, 1.0f),
                true, true, x+0.5, y+0.5, z+0.5, 0, 0, 0
        );
    }
}
