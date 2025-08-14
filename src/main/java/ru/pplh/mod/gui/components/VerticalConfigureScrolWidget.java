//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.pplh.mod.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.Colors;

public class VerticalConfigureScrolWidget extends AbstractWidget {
    private double scrollAmount;
    private boolean scrolling;
    public final Consumer<VerticalConfigureScrolWidget> onScroll;
    public int innerHeight;
    public List<AbstractWidget> widgets = new ArrayList();
    private double animationTimer = (double)0.0F;
    private double scrollStartVelocity = (double)0.0F;
    public static double scrollSpeed = (double)0.5F;
    public static double scrollbarDrag = 0.025;
    public static double animationDuration = (double)1.0F;
    public static double pushBackStrength = (double)1.0F;

    public VerticalConfigureScrolWidget(int x, int y, int width, int height, Component message, Consumer<VerticalConfigureScrolWidget> onScroll) {
        super(x, y, width, height, message);
        this.onScroll = onScroll;
    }

    protected int getInnerHeight() {
        return this.innerHeight;
    }

    protected double scrollRate() {
        return (double)9.0F;
    }

    public double scrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = Mth.clamp(amount, (double)0.0F, (double)this.getMaxScrollAmount());
        this.onScroll.accept(this);
    }

    protected void renderBackground(GuiGraphics guiGraphics) {
        if (this.scrollbarVisible()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.getHeight(), 1962934272);
        }

    }

    private int getContentHeight() {
        return this.getInnerHeight() + 4;
    }

    private int getScrollBarHeight() {
        return Mth.clamp((int)((float)(this.width * this.width) / (float)this.getContentHeight()), 16, this.width);
    }

    protected void renderDecorations(GuiGraphics guiGraphics) {
        if (this.scrollbarVisible()) {
            int i = this.getScrollBarHeight();
            int k = Math.max(this.getX(), (int)this.scrollAmount() * (this.width - i) / this.getMaxScrollAmount() + this.getX());
            guiGraphics.fill(k, this.getY(), k + i, this.getY() + this.getHeight(), Colors.getScrollerColor());
        }

    }

    public void resetWidgets() {
        this.widgets.clear();
        this.setScrollAmount((double)0.0F);
    }

    public void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    public void addWidgets(List<AbstractWidget> widgets) {
        for(AbstractWidget widget : widgets) {
            this.addWidget(widget);
        }

    }

    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }

    public boolean mouseScrolled(double d, double e, double f, double g) {
        if (!this.visible) {
            return false;
        } else {
            double amount = this.scrollAmount - g * this.scrollRate();
            if (AlinLib.bariumConfig.getBoolean("SCROLLER.SMOOTH", false) && this.scrollbarVisible()) {
                double diff = amount - this.scrollAmount;
                diff = Math.signum(diff) * Math.min(Math.abs(diff), (double)10.0F);
                diff *= scrollSpeed;
                if (Math.signum(diff) != Math.signum(this.scrollStartVelocity)) {
                    diff *= (double)2.5F;
                }

                this.animationTimer *= (double)0.5F;
                this.scrollStartVelocity = scrollbarVelocity(this.animationTimer, this.scrollStartVelocity) + diff;
                this.animationTimer = (double)0.0F;
            } else {
                this.setScrollAmount(amount);
            }

            return true;
        }
    }

    public boolean mouseClicked(double d, double e, int i) {
        if (!this.visible) {
            return false;
        } else {
            boolean bl = this.withinContentAreaPoint(d, e);
            boolean bl2 = this.scrollbarVisible() && d >= (double)this.getX() && d <= (double)(this.getX() + this.width) && e >= (double)this.getY() && e < (double)(this.getY() + this.height);
            if (bl2 && i == 0) {
                this.scrolling = true;
                return true;
            } else {
                return bl || bl2;
            }
        }
    }

    public boolean mouseReleased(double d, double e, int i) {
        if (i == 0) {
            this.scrolling = false;
        }

        return super.mouseReleased(d, e, i);
    }

    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.visible && this.isFocused() && this.scrolling) {
            if (d < (double)this.getX()) {
                this.setScrollAmount((double)0.0F);
            } else if (d > (double)(this.getX() + this.width)) {
                this.setScrollAmount((double)this.getMaxScrollAmount());
            } else {
                int j = this.getScrollBarHeight();
                double h = (double)Math.max(1, this.getMaxScrollAmount() / (this.width - j));
                this.setScrollAmount(this.scrollAmount + f * h);
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean withinContentAreaPoint(double d, double e) {
        return d >= (double)this.getX() && d < (double)(this.getX() + this.width) && e >= (double)this.getY() && e < (double)(this.getY() + this.height);
    }

    public boolean keyPressed(int i, int j, int k) {
        boolean bl = i == 265;
        boolean bl2 = i == 264;
        if (bl || bl2) {
            double d = this.scrollAmount;
            this.setScrollAmount(this.scrollAmount + (double)(bl ? -1 : 1) * this.scrollRate());
            if (d != this.scrollAmount) {
                return true;
            }
        }

        return super.keyPressed(i, j, k);
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (AlinLib.bariumConfig.getBoolean("SCROLLER.SMOOTH", false)) {
            this.checkOutOfBounds(delta);
            if (Math.abs(scrollbarVelocity(this.animationTimer, this.scrollStartVelocity)) > (double)1.0F) {
                this.applyMotion(delta);
            }
        }

        this.renderBackground(guiGraphics);
        this.renderDecorations(guiGraphics);
    }

    private void applyMotion(float delta) {
        this.scrollAmount += scrollbarVelocity(this.animationTimer, this.scrollStartVelocity) * (double)delta;
        this.animationTimer += (double)(delta * 10.0F) / animationDuration;
        this.onScroll.accept(this);
    }

    private void checkOutOfBounds(float delta) {
        if (this.scrollAmount < (double)0.0F) {
            this.scrollAmount += pushBackStrength(Math.abs(this.scrollAmount), delta);
            if (this.scrollAmount > -0.2) {
                this.scrollAmount = (double)0.0F;
            }
        }

        if (this.scrollAmount > (double)this.getMaxScrollAmount()) {
            this.scrollAmount -= pushBackStrength(this.scrollAmount - (double)this.getMaxScrollAmount(), delta);
            if (this.scrollAmount < (double)this.getMaxScrollAmount() + 0.2) {
                this.scrollAmount = (double)this.getMaxScrollAmount();
            }
        }

    }

    protected boolean scrollbarVisible() {
        return this.getInnerHeight() > this.getWidth();
    }

    protected int getMaxScrollAmount() {
        return Math.max(0, this.getContentHeight() - (this.width - 4));
    }

    public static double scrollbarVelocity(double timer, double factor) {
        return Math.pow((double)1.0F - scrollbarDrag, timer) * factor;
    }

    public static double pushBackStrength(double distance, float delta) {
        return (distance + (double)4.0F) * (double)delta / 0.3 / (3.2 / pushBackStrength);
    }
}
