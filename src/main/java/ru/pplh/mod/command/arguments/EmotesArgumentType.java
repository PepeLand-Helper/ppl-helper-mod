package ru.pplh.mod.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ru.pplh.mod.PepeLandHelper;

import java.util.concurrent.CompletableFuture;

public class EmotesArgumentType implements ArgumentType<String> {
    public final String nameArgument;

    public EmotesArgumentType(String nameArgument) {
        this.nameArgument = nameArgument;
    }

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
            String arg = "";
            try {
                arg = context.getArgument(nameArgument, String.class);
            } catch (Exception ignored){}
            for (String world : PepeLandHelper.getEmotesPath().keySet()) {
                String[] war = world.split("/");
                String name = war[war.length - 1].split("\\.")[0];
                if (name.startsWith(arg) && !world.contains("black.png"))
                    builder.suggest(String.format("%s", PepeLandHelper.getEmotesPath().get(world)));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    }
}
