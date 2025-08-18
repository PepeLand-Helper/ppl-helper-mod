package ru.pplh.mod.mixin.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.pplh.mod.utils.CameraManager;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "disconnect", at=@At("HEAD"), cancellable = true)
    public void disconnect(Screen screen, boolean bl, CallbackInfo ci){
        if(CameraManager.isCameraMode) CameraManager.setCamera(null);
    }
}
