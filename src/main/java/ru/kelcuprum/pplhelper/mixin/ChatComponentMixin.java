package ru.kelcuprum.pplhelper.mixin;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import static java.lang.Integer.parseInt;

@Mixin(value = ChatComponent.class, priority = -1)
public class ChatComponentMixin {
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"))
    public void addMessage(Component component, MessageSignature messageSignature, GuiMessageTag guiMessageTag, CallbackInfo ci) {
        if (!PepelandHelper.playerInPPL()) return;
        String test = component.getString();
        if (test.replaceAll("\\[PPL[0-9]]: ", "").length() != test.length()) {
            String parsed = test.replaceAll("\\[PPL[0-9]]: ", "");
            if (parsed.contains("Рестарт через") && PepelandHelper.config.getBoolean("TIMER.RESTART", true)) {
                int time = parseInt(parsed.replaceAll("[^0-9]", ""));
                PepelandHelper.restartTime = System.currentTimeMillis() + ((long) time * (parsed.contains("минут") ? 60 : 1) * 1000);
            }
        } else if (test.contains("Вы еще не можете зайти на сервер") && TabHelper.getWorld() == TabHelper.Worlds.LOBBY && PepelandHelper.config.getBoolean("TIMER.JOIN", true)) {
            int time = parseInt(test.replaceAll("[^0-9]", ""));
            PepelandHelper.joinTime = System.currentTimeMillis() + ((long) time * (test.contains("минут") ? 60 : 1) * 1000);
        }
    }
}
