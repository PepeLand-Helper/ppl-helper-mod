package ru.kelcuprum.pplhelper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.config.Localization;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PPLHelperCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("pplhelper").executes(s -> {
            sendFeedback(s, Component.literal(String.format("Привет, %s!", s.getSource().getPlayer().getDisplayName().getString())));
            sendFeedback(s, Component.literal("Данная команда находится на стадии разработки!"));
            return 0;
        }));
    }

    public static void sendFeedback(CommandContext<FabricClientCommandSource> s, Component component){
        s.getSource().sendFeedback(Component.empty().append(Localization.fixFormatCodes("&a&l[PPL Helper]:&r ")).append(component));
    }
}
