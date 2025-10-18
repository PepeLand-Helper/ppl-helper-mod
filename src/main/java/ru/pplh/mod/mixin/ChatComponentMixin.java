package ru.pplh.mod.mixin;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.pplh.mod.PepeLandHelper;
import ru.pplh.mod.utils.ChatFilter;
import ru.pplh.mod.utils.TabHelper;

import static java.lang.Integer.parseInt;

@Mixin(value = ChatComponent.class, priority = -1)
public abstract class ChatComponentMixin {
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    public void addMessage(Component component, MessageSignature messageSignature, GuiMessageTag guiMessageTag, CallbackInfo ci) {
        if (!PepeLandHelper.playerInPPL()) return;
        String test = component.getString();

        if (test.replaceAll("\\[PPL[0-9]*]: ", "").length() != test.length()) {
            String parsed = test.replaceAll("\\[PPL[0-9]*]: ", "");
            if (parsed.contains("Рестарт через") && PepeLandHelper.config.getBoolean("TIMER.RESTART", true)) {
                if(parsed.split(" ").length == 6) {
                    int time = 0;
                    for (String arg : parsed.split(" ")) {
                        int lengtht = arg.length();
                        int parsedLength = arg.replaceAll("[^0-9]", "").length();
                        if (lengtht == parsedLength) time = parseInt(arg);
                    }
                    PepeLandHelper.restartTime = System.currentTimeMillis() + ((long) time * (parsed.contains("минут") ? 60 : 1) * 1000);
                }
            } else if(parsed.contains("Рестарт случился") && parsed.split(" ").length == 2) PepeLandHelper.restartTime = System.currentTimeMillis();
        } else if ((test.contains("Вы еще не можете зайти на сервер")
        || test.contains("Сервер заполнен. Вы не можете зайти!") || (test.startsWith("Unable to connect to ") && test.contains(": The server is full!")))
                && TabHelper.getWorld() == TabHelper.Worlds.LOBBY && PepeLandHelper.config.getBoolean("TIMER.JOIN", true)) {
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
                else if (!ChatFilter.isContainsGandons(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.BLACKLIST", false))
                    ci.cancel();
                else if ((ChatFilter.isLeave(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.LEAVE", false))
                        || (ChatFilter.isJoin(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.JOIN", false))) {
                    if (!ChatFilter.isFriend(test)) ci.cancel();
                }
            }
    }
}
