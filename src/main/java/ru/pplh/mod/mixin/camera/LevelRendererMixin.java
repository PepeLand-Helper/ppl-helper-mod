package ru.pplh.mod.mixin.camera;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.pplh.mod.utils.CameraManager;

import java.util.List;

@Mixin(value = LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    protected abstract void renderEntity(Entity entity, double d, double e, double f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource);

    @Inject(method = "getTransparencyChain", at= @At("HEAD"), cancellable = true)
    public void render(CallbackInfoReturnable<PostChain> cir) {
        if (CameraManager.isCameraMode && CameraManager.currentCamera != null) {
            ResourceLocation postEffect = getPostEffect(CameraManager.currentCamera.cameraType());
            if(postEffect != null) {
                PostChain astralShader = Minecraft.getInstance().getShaderManager().getPostChain(postEffect, LevelTargetBundle.SORTING_TARGETS);
                if (astralShader != null) cir.setReturnValue(astralShader);
            }
        }
    }

    @Unique
    public ResourceLocation getPostEffect(CameraManager.CameraType type){
        return switch (type){
            case RGB -> null;
            case CREEPER -> ResourceLocation.withDefaultNamespace("creeper");
            default -> ResourceLocation.fromNamespaceAndPath("pplhelper","camera_mode");
        };
    }

    @Inject(method = "renderEntities", at = @At(value = "HEAD"))
    private void renderEntities(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Camera camera,
                                DeltaTracker deltaTracker, List<Entity> list, CallbackInfo ci) {
        PoseStack matrices = new PoseStack();
        //#endif
        if (camera.isDetached() || !CameraManager.isCameraMode) {
            return;
        }
        Vec3 vec3d = camera.getPosition();
        MultiBufferSource.BufferSource immediate = renderBuffers.bufferSource();
        renderEntity(camera.getEntity(), vec3d.x(), vec3d.y(), vec3d.z(),
                deltaTracker.getGameTimeDeltaPartialTick(false), matrices, immediate);
    }
}
