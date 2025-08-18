package ru.pplh.mod.mixin.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.utils.CameraManager;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(
            at = @At("HEAD"),
            method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z",
            cancellable = true
    )
    private void viewOwnLabel(LivingEntity ent, double d, CallbackInfoReturnable<Boolean> ci) {
        if (ent == AlinLib.MINECRAFT.cameraEntity && CameraManager.isCameraMode) ci.setReturnValue(Minecraft.renderNames());
    }
}
