package ru.kelcuprum.pplhelper.mixin.locatorbar;
import net.minecraft.client.gui.Gui;
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
import ru.kelcuprum.pplhelper.utils.LocatorBarRender;

@Mixin(LocatorBarRenderer.class)
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
