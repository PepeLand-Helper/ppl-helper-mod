package ru.pplh.mod.mixin.camera;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.pplh.mod.utils.CameraManager;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At(value = "RETURN", ordinal = 1), method = "getFov", cancellable = true)
    private void getFov(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir){
        if(CameraManager.isCameraMode) cir.setReturnValue(CameraManager.fov);
    }
}
