package ru.pplh.mod.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;
import ru.pplh.mod.PepeLandHelper;

import java.util.concurrent.CompletableFuture;

public class WorldArgumentType implements ArgumentType<String> {
    public static String getWorld(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public String toString() {
        return "string()";
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for(String world : PepeLandHelper.worlds)
            if(!world.contains(Component.translatable("pplhelper.project.world.all").getString())) builder.suggest(String.format("\"%s\"", world));
        return builder.buildFuture();
    }
}
