package ru.kelcuprum.pplhelper.mixin.april;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.gui.components.oneshot.overlay.DialogOverlay;

@Mixin(ClientPacketListener.class)
public class PlayerInventoryMixin {

    @Unique
    boolean hell = false;

    @Inject(method = "handleTakeItemEntity", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
            shift = At.Shift.AFTER))
    void p(ClientboundTakeItemEntityPacket clientboundTakeItemEntityPacket, CallbackInfo ci) {
        if(!PepeLandHelper.isAprilFool() || !PepeLandHelper.isPWGood()) return;
        assert AlinLib.MINECRAFT.player != null;
        if (AlinLib.MINECRAFT.player.getId() != clientboundTakeItemEntityPacket.getPlayerId()) return;
        Entity e = AlinLib.MINECRAFT.player.level().getEntity(clientboundTakeItemEntityPacket.getItemId());
        if (e instanceof ItemEntity) {
            if (!hell) {
                hell = true;
                ItemEntity i = (ItemEntity) e;
                ItemStack s = i.getItem().copy();
                AlinLib.MINECRAFT.execute(() -> {
                    AlinLib.MINECRAFT.setScreen(new DialogOverlay(AlinLib.MINECRAFT.screen, new String[]{
                            "[Здесь лежит " + s.getItemName().getString() + ".]",
                            "[Гнег его поднимает.]"
                    }, null, 500));
                });
            }
        }
    }
}
