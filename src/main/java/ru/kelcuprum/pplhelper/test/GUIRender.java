package ru.kelcuprum.pplhelper.test;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.api.events.client.GuiRenderEvents;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.pplhelper.interactive.Action;
import ru.kelcuprum.pplhelper.interactive.Interactive;
import ru.kelcuprum.pplhelper.interactive.InteractiveManager;

import static ru.kelcuprum.pplhelper.PepeLandHelper.playerInPPL;

public class GUIRender implements GuiRenderEvents {
    @Override
    public void onRender(GuiGraphics guiGraphics, float partialTick) {
        int y = 5;
        int x = 5;
        if(InteractiveManager.currentInteractive != null && playerInPPL()){
            Interactive inter = InteractiveManager.currentInteractive;
            guiGraphics.drawString(AlinLib.MINECRAFT.font, String.format("Интерактив: %s", inter.id), x, y, -1);
            y+=AlinLib.MINECRAFT.font.lineHeight+3;
            guiGraphics.drawString(AlinLib.MINECRAFT.font, String.format("Статус интерактива: %s", inter.status), x, y, -1);
            y+=AlinLib.MINECRAFT.font.lineHeight+3;
            guiGraphics.drawString(AlinLib.MINECRAFT.font, "Действия:", x, y, -1);
            y+=AlinLib.MINECRAFT.font.lineHeight+3;
            for(Action action : inter.actions){
                String msg = String.format("%s (%s) [%s | %s] %s ", (action.isExecuted ? "✅" : "❎"), action.type, getCoordinate(action.area.get(0)), getCoordinate(action.area.get(1)), action.content);
                for(FormattedCharSequence text : AlinLib.MINECRAFT.font.split(Component.literal(msg), (int) (guiGraphics.guiWidth()*0.33))) {
                    guiGraphics.drawString(AlinLib.MINECRAFT.font, text, x, y, -1);
                    y+=AlinLib.MINECRAFT.font.lineHeight+3;
                }
            }
        }
    }

    public static String getCoordinate(int[] coordinates){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i : coordinates){
            if(!stringBuilder.isEmpty()) stringBuilder.append(" ");
            stringBuilder.append(i);
        }
        return stringBuilder.toString();
    }
}
