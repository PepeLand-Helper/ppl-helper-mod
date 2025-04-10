package ru.kelcuprum.pplhelper.mixin;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.pplhelper.PepeLandHelper;
import ru.kelcuprum.pplhelper.utils.ChatFilter;
import ru.kelcuprum.pplhelper.utils.TabHelper;

import static java.lang.Integer.parseInt;
import static net.minecraft.ChatFormatting.AQUA;
import static net.minecraft.ChatFormatting.BOLD;

@Mixin(value = ChatComponent.class, priority = -1)
public abstract class ChatComponentMixin {
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    public void addMessage(Component component, MessageSignature messageSignature, GuiMessageTag guiMessageTag, CallbackInfo ci) {
        if (!PepeLandHelper.playerInPPL()) return;
        String test = component.getString();

        if (test.replaceAll("\\[PPL[0-9]]: ", "").length() != test.length()) {
            String parsed = test.replaceAll("\\[PPL[0-9]]: ", "");
            if (parsed.contains("Рестарт через") && PepeLandHelper.config.getBoolean("TIMER.RESTART", true)) {
                int time = parseInt(parsed.replaceAll("[^0-9]", ""));
                PepeLandHelper.restartTime = System.currentTimeMillis() + ((long) time * (parsed.contains("минут") ? 60 : 1) * 1000);
            }
        } else if (test.contains("Вы еще не можете зайти на сервер") && TabHelper.getWorld() == TabHelper.Worlds.LOBBY && PepeLandHelper.config.getBoolean("TIMER.JOIN", true)) {
            int time = parseInt(test.replaceAll("[^0-9]", ""));
            PepeLandHelper.joinTime = System.currentTimeMillis() + ((long) time * (test.contains("минут") ? 60 : 1) * 1000);
        }
        if (PepeLandHelper.config.getBoolean("CHAT.FILTER", false)) {
                if (!ChatFilter.mention(test) && ChatFilter.isGlobalChat(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.GLOBAL", false))
                    ci.cancel();
                else if (!ChatFilter.mention(test) && ChatFilter.isWorldEnabled(test)) {
                    if (!ChatFilter.enableWorld(test)) ci.cancel();
                } else if (!ChatFilter.mention(test) && ChatFilter.isMysteryBox(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.MYSTERY_BOX", true))
                    ci.cancel();
                else if (!ChatFilter.mention(test) && ChatFilter.isContainsNWords(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.WORDS", false))
                    ci.cancel();
                else if ((ChatFilter.isLeave(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.LEAVE", false))
                        || (ChatFilter.isJoin(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.JOIN", false))) {
                    if (!ChatFilter.isFriend(test)) ci.cancel();
                }
            }
    }
}
