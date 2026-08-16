package ru.pplh.mod.mixin.locatorbar;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.gui.contextualbar.LocatorBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12106
import net.minecraft.client.DeltaTracker;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.utils.LocatorBarRender;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

@Mixin(value = LocatorBar.class, priority = MAX_VALUE)
public abstract class LocatorBarMixin implements ContextualBar {
    @Inject(method = "extractRenderState", at=@At("RETURN"))
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci){
        LocatorBarRender.renderLodestoneWaypoints(AlinLib.MINECRAFT, guiGraphics, top(AlinLib.MINECRAFT.getWindow()));
    }
}
//#else
//$$ @Mixin(Gui.class)
//$$ public class LocatorBarMixin { }
//#endif
