package ru.kelcuprum.pplhelper.utils;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.gui.components.ScaledTextBox;
import ru.kelcuprum.pplhelper.gui.components.WebImageWidget;

import java.util.ArrayList;
import java.util.List;

public class MarkdownParser {
    public static List<AbstractWidget> parse(String content, int x, int width, String idForImage, Screen screen) {
        List<AbstractWidget> widgets = new ArrayList<>();
        String[] strings = parse(content, false).split("\n");
        boolean lastIsPlain = false;
        boolean lastIsBlockQuote = false;
        String plain = "";
        String blockquote = "";
        for (String string : strings) {
            if (string.matches("!\\[(.+?)]\\((.+?)\\)")) {
                if (lastIsPlain) {
                    lastIsPlain = false;
                    plain = parse(plain, true);
                    widgets.add(new TextBuilder(Component.literal(plain.substring(0, plain.length() - (plain.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
                    plain = "";
                }
                if (lastIsBlockQuote) {
                    lastIsBlockQuote = false;
                    blockquote = parse(blockquote, true);
                    widgets.add(new TextBuilder(Component.literal(blockquote.substring(0, blockquote.length() - (blockquote.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
                    blockquote = "";
                }
                int iWidth = width - 230;
                int iHeight = 20;
                widgets.add(new WebImageWidget(x, -Integer.MAX_VALUE, iWidth, iHeight, unparse(string.replaceAll("!\\[(.+?)]\\((.+?)\\)", "$2")), unparse(string.replaceAll("!\\[(.+?)]\\((.+?)\\)", "$1")), true, Component.empty()));
            } else if (string.startsWith("<hr") && string.endsWith(">")) {
                if (lastIsPlain) {
                    lastIsPlain = false;
                    plain = parse(plain, true);
                    widgets.add(new TextBuilder(Component.literal(plain.substring(0, plain.length() - (plain.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
                    plain = "";
                }
                if (lastIsBlockQuote) {
                    lastIsBlockQuote = false;
                    blockquote = parse(blockquote, true);
                    widgets.add(new TextBuilder(Component.literal(blockquote.substring(0, blockquote.length() - (blockquote.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
                    blockquote = "";
                }
                widgets.add(new HorizontalRuleBuilder().setPosition(x, -1).build());
            } else if (string.startsWith("#")) {
                if (lastIsPlain) {
                    lastIsPlain = false;
                    plain = parse(plain, true);
                    widgets.add(new TextBuilder(Component.literal(plain.substring(0, plain.length() - (plain.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
                    plain = "";
                }
                if (lastIsBlockQuote) {
                    lastIsBlockQuote = false;
                    blockquote = parse(blockquote, true);
                    widgets.add(new TextBuilder(Component.literal(blockquote.substring(0, blockquote.length() - (blockquote.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
                    blockquote = "";
                }
                int j = 0;
                for (int i = 0; i < string.length() && string.split("")[i].equals("#"); i++) j = i;
                string = parse(string, true);
                widgets.add(new ScaledTextBox(x, -40, width - 230, AlinLib.MINECRAFT.font.lineHeight + 5, Component.literal(string.substring(j + (string.contains("# ") ? 2 : 0))), false, 1.5F - ((float) j / 6)));
            } else if (string.startsWith(">")) {
                if (lastIsPlain) {
                    lastIsPlain = false;
                    plain = parse(plain, true);
                    widgets.add(new TextBuilder(Component.literal(plain.substring(0, plain.length() - (plain.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
                    plain = "";
                }
                if (!lastIsBlockQuote) lastIsBlockQuote = true;
                string = string.substring(string.contains("> ") ? 2 : 1);
                blockquote += string += "\n";
            } else {
                if (lastIsBlockQuote) {
                    lastIsBlockQuote = false;
                    blockquote = parse(blockquote, true);
                    widgets.add(new TextBuilder(Component.literal(blockquote.substring(0, blockquote.length() - (blockquote.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
                    blockquote = "";
                }
                if (!lastIsPlain) lastIsPlain = true;
                if (!string.isBlank()) plain += string += "\n";
            }
        }
        if (lastIsPlain)
            widgets.add(new TextBuilder(Component.literal(plain.substring(0, plain.length() - (plain.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
        if (lastIsBlockQuote)
            widgets.add(new TextBuilder(Component.literal(blockquote.substring(0, blockquote.length() - (blockquote.endsWith("\n") ? 1 : 0)))).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
        return widgets;
    }

    public static String parse(String string, boolean isLine) {
        String ret = string.replaceAll("\\*\\*(.+?)\\*\\*(?!\\*)", "§l$1§r")
                .replaceAll("\\*(.+?)\\*(?!\\*)", "§o$1§r")
                .replaceAll("__(.+?)__(?!_)", "§n$1§r")
                .replaceAll("_(.+?)_(?!_)", "§o$1§r")
                .replaceAll("~~(.+?)~~(?!~)", "§m$1§r")
                .replaceAll("\\|\\|(.+?)\\|\\|(?!\\|)", "§k$1§r")
                .replace("<br>\n", "\n")
                .replace("\r", "");
        if (isLine) {
            ret = ret.replace("<br>", "\n");
        }
        return ret;
    }

    public static String unparse(String string) {
        return string.replaceAll("§l(.+?)§r", "**$1**")
                .replaceAll("§o(.+?)§r", "_$1_")
                .replaceAll("§n(.+?)§r", "__$1__")
                .replaceAll("§m(.+?)§r", "~~$1~~")
                .replaceAll("§k(.+?)§r", "||$1||");
    }
}
