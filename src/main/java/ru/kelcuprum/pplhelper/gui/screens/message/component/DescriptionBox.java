package ru.kelcuprum.pplhelper.gui.screens.message.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import java.util.List;
import java.util.Objects;

public class DescriptionBox extends AbstractWidget {
    protected Component description;
    int textHeight;
    int lastTextHeight;
    int textSize;
    double scrollRate;
    double scrollAmount;

    public DescriptionBox(int x, int y, Component label) {
        this(x, y, GuiUtils.DEFAULT_WIDTH(), 20, label, (DescriptionBox.OnPress)null);
    }

    public DescriptionBox(int x, int y, Component label, DescriptionBox.OnPress onPress) {
        this(x, y, GuiUtils.DEFAULT_WIDTH(), 20, label, onPress);
    }

    public DescriptionBox(int x, int y, int width, int height, Component label) {
        this(x, y, width, height, label, (DescriptionBox.OnPress)null);
    }

    public DescriptionBox(int x, int y, int width, int height, Component label, DescriptionBox.OnPress onPress) {
        super(x, y, width, height, label);
        this.textHeight = 0;
        this.lastTextHeight = 0;
        Objects.requireNonNull(AlinLib.MINECRAFT.font);
        this.textSize = 9 + 3;
        this.scrollRate = (double)9.0F;
        this.scrollAmount = (double)0.0F;
        this.setActive(false);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setX(int x) {
        super.setX(x);
    }

    public void setY(int y) {
        super.setY(y);
    }

    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }

    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        List<FormattedCharSequence> list = AlinLib.MINECRAFT.font.split(this.description, this.width - 12);
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 1962934272);
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
        int y = this.getY() + 6;
        this.textHeight = (list.size() + 1) * this.textSize + 12;
        if (this.lastTextHeight != this.textHeight) {
            this.scrollAmount = Mth.clamp(this.scrollAmount, (double)0.0F, (double)(this.textHeight - this.height - this.textSize));
            this.lastTextHeight -= this.lastTextHeight - this.textHeight;
            if (this.height > this.textHeight) {
                this.scrollAmount = (double)0.0F;
            }
        }

        for(FormattedCharSequence text : list) {
            guiGraphics.drawString(AlinLib.MINECRAFT.font, text, this.getX() + 6, (int)((double)y - this.scrollAmount), -1);
            y += this.textSize;
        }

        guiGraphics.disableScissor();
    }

    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollY * this.scrollRate, (double)0.0F, (double)(this.lastTextHeight - this.height - this.textSize));
        return true;
    }

    public DescriptionBox setDescription(Component description) {
        this.description = description;
        return this;
    }

    public interface OnPress {
        void onPress(DescriptionBox var1);
    }
}
