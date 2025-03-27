package ru.kelcuprum.pplhelper.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ru.kelcuprum.pplhelper.PepeLandHelper;

import java.util.concurrent.CompletableFuture;

public class EmotesArgumentType implements ArgumentType<String> {
    public static String getWorld(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead()) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    @Override
    public String toString() {
        return "string()";
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            for(String world : PepeLandHelper.getEmotesPath().keySet())
                if(!world.contains("black.png")) builder.suggest(String.format("%s", PepeLandHelper.getEmotesPath().get(world)));
        } catch (Exception e) {

        }
//        builder.suggest("\"Когда нибудь потом...\"");
        return builder.buildFuture();
    }
}
