package ru.pplh.mod.mixin.camera;

import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ru.pplh.mod.utils.CameraManager;

@Mixin(Camera.class)
public class CameraMixin {
    @ModifyArgs(method = "setup", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    public void setRotation(Args args){
        if(CameraManager.isCameraMode && CameraManager.currentCamera != null){
            args.set(0, CameraManager.xRot);
            args.set(1, CameraManager.yRot);
        }
    }

    @ModifyArgs(method = "setup", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(Lnet/minecraft/world/phys/Vec3;)V"))
    public void setPositionVec3(Args args){
        if(CameraManager.isCameraMode && CameraManager.currentCamera != null){
            BlockPos b = CameraManager.currentCamera.position();
            args.set(0, new Vec3(getCord(b.getX()), getCord(b.getY()+1), getCord(b.getZ())));
        }
    }
    @ModifyArgs(method = "setup", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    public void setPosition(Args args){
        if(CameraManager.isCameraMode && CameraManager.currentCamera != null){
            BlockPos b = CameraManager.currentCamera.position();
            args.set(0, getCord(b.getX()));
            args.set(1, b.getY()+0.5);
            args.set(2, getCord(b.getZ()));
        }
    }

    @Unique
    public double getCord(double coordinate){
        return coordinate + (0.5);
    }
}
