package ru.kelcuprum.pplhelper.gui.components.oneshot;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import ru.kelcuprum.alinlib.AlinLib;

public class OneShotTitle extends AbstractWidget {
    public OneShotTitle(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.active = false;
    }


    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(2.5F, 2.5F, 2.5F);
        guiGraphics.drawString(AlinLib.MINECRAFT.font, getMessage(), (int) (getX()/2.5F), (int) (getY()/2.5F), 16777215 | Mth.ceil(this.getValuePos() * 255.0F) << 24);
        guiGraphics.pose().popPose();
    }

    private final long timeFocused = System.currentTimeMillis();
    private double getValuePos(){
        double value = (double) (System.currentTimeMillis() - timeFocused) / 200;
        if(value >= 1.0) value = 1.0;
        return value;
    }
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
