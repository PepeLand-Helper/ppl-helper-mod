package ru.pplh.mod.mixin.locatorbar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12106
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.utils.LocatorBarRender;

import static java.lang.Integer.MIN_VALUE;

@Mixin(value = LocatorBarRenderer.class, priority = MIN_VALUE)
public abstract class LocatorBarMixin implements ContextualBarRenderer {
    @Inject(method = "render", at=@At("RETURN"))
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci){
        LocatorBarRender.renderLodestoneWaypoints(AlinLib.MINECRAFT, guiGraphics, top(AlinLib.MINECRAFT.getWindow()));
    }
}
//#else
//$$ @Mixin(Gui.class)
//$$ public class LocatorBarMixin { }
//#endif
