package ru.pplh.mod.mixin.camera;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.pplh.mod.utils.CameraManager;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    @Inject(method = "render", at=@At("HEAD"), cancellable = true)
    public void render(BlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci){
        if(CameraManager.isCameraMode && CameraManager.currentCamera != null){
            if(blockEntity == CameraManager.currentCamera.blockEntity()) ci.cancel();
        }
    }
}
