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
    @Shadow public abstract void addMessage(Component component);

    @Unique boolean isSended = false;
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    public void addMessage(Component component, MessageSignature messageSignature, GuiMessageTag guiMessageTag, CallbackInfo ci) {
        if (!PepeLandHelper.playerInPPL()) return;
        if(PepeLandHelper.isAprilFool() && PepeLandHelper.isPWGood()){
            if(Math.random() < 0.005 && !isSended){ // Math.random() < 0.005 && !isSended
                isSended = true;
                MutableComponent component1 = Component.empty();
                component1.append(Component.literal("✉✉✉ [").withColor(AQUA.getColor()));
                component1.append(Component.literal("Sanhez").withStyle(BOLD).withColor(AQUA.getColor()));
                component1.append(Component.literal(" → ").withColor(AQUA.getColor()));
                component1.append(Component.literal("Вы").withStyle(BOLD).withColor(AQUA.getColor()));
                component1.append(Component.literal("]: ").withColor(AQUA.getColor()));
                component1.append("го ебаться");
                for(int i = 0; i<3; i++){
                    addMessage(Component.empty().append(component1).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Sanhez 300 блоков от вас")))));
                    AlinLib.MINECRAFT.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.CHICKEN_EGG, 1.5f));
                }
            }
        }
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
        if(!ChatFilter.mention(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER", false)){
            if(ChatFilter.isGlobalChat(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.GLOBAL", false)) ci.cancel();
            else if (ChatFilter.isWorldEnabled(test)){
                if(!ChatFilter.enableWorld(test)) ci.cancel();
            } else if(ChatFilter.isMysteryBox(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.MYSTERY_BOX", true)) ci.cancel();
            else if((ChatFilter.isLeave(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.LEAVE", false))
                    || (ChatFilter.isJoin(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.JOIN", false))){
                if(!ChatFilter.isFriend(test)) ci.cancel();
            } else if(ChatFilter.isContainsNWords(test) && PepeLandHelper.config.getBoolean("CHAT.FILTER.WORDS", false)) ci.cancel();
        }
    }
}
