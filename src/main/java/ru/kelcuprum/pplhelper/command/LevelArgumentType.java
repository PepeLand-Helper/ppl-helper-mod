package ru.kelcuprum.pplhelper.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class LevelArgumentType implements ArgumentType<String> {
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
        for(String world : new String[]{
                "minecraft:overworld",
                "minecraft:the_nether",
                "minecraft:the_end",
                "minecraft:world_art"
        })
            builder.suggest(String.format("\"%s\"", world));
        return builder.buildFuture();
    }
}
