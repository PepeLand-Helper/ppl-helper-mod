package ru.pplh.mod.mixin.camera;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.utils.CameraManager;

import static java.lang.Integer.MIN_VALUE;
import static ru.pplh.mod.PepeLandHelper.LOG;

@Mixin(value = MultiPlayerGameMode.class, priority = MIN_VALUE)
public abstract class MultiPlayerGameModeMixin {
    @Shadow
    protected abstract void startPrediction(ClientLevel clientLevel, PredictiveAction predictiveAction);

    @Unique
    private static boolean isFucking = false;
    @Unique
    private static long lastFucking = 0;
    @Inject(method = "useItemOn", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V"), cancellable = true)
    public void useItemOn(LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir){
        if(localPlayer != AlinLib.MINECRAFT.player || !localPlayer.level().isClientSide) return;
        if(localPlayer.isSpectator()) return;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = localPlayer.level().getBlockState(blockPos);
        if (blockState.is(Blocks.PLAYER_HEAD) || blockState.is(Blocks.PLAYER_WALL_HEAD)) {
            BlockEntity blockEntity = localPlayer.level().getBlockEntity(blockPos);
            assert blockEntity != null;
            BlockDataAccessor dataAccessor = new BlockDataAccessor(blockEntity, blockPos);
            Tag tag = dataAccessor.getData().get("custom_name");
            if (tag != null) {
                String name = tag.asString().get();
                if(name.startsWith("monitor")){
                    if(isFucking || System.currentTimeMillis()-lastFucking < 1500){
                        isFucking = false;
                        AlinLib.MINECRAFT.gui.getChat().addMessage(Component.literal("У вас слишком быстрые пальцы"));
                        return;
                    }
                    isFucking = true;
                    lastFucking = System.currentTimeMillis();
                    String[] args = name.split(":");
                    if(args.length == 2){
                        LOG.log("Test: [%s] %s %s %s", args[1], blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        new Thread(() -> {
                            CameraManager.openMonitor(args[1], blockPos, localPlayer.level());
                        }).start();
                        startPrediction(localPlayer.clientLevel, id -> new ServerboundUseItemOnPacket(interactionHand, blockHitResult, id));
                        cir.setReturnValue(InteractionResult.SUCCESS);
                    }
                }
            }
        }
    }
}
