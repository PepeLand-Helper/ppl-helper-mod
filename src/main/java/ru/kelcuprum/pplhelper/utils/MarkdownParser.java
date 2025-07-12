package ru.kelcuprum.pplhelper.utils;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.pplhelper.gui.components.ScaledTextBox;
import ru.kelcuprum.pplhelper.gui.components.WebImageWidget;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static ru.kelcuprum.alinlib.gui.Colors.CPM_BLUE;

public class MarkdownParser {
    public static List<AbstractWidget> parse(String content, int x, int width, String idForImage, Screen screen) {
        List<AbstractWidget> widgets = new ArrayList<>();
        String[] strings = content.replaceAll("\r\n", "\n").split("\n");
        boolean lastIsPlain = false;
        boolean lastIsBlockQuote = false;
        String plain = "";
        String blockquote = "";
        for (String string : strings) {
            if (string.matches("!\\[(.+?)]\\((.+?)\\)")) {
                if (lastIsPlain) {
                    lastIsPlain = false;
                    Component component = parse(plain);
                    widgets.add(new TextBuilder(component).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
                    plain = "";
                }
                if (lastIsBlockQuote) {
                    lastIsBlockQuote = false;
                    Component component = parse(blockquote);
                    widgets.add(new TextBuilder(component).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
                    blockquote = "";
                }
                int iWidth = width - 230;
                int iHeight = 20;
                widgets.add(new WebImageWidget(x, -Integer.MAX_VALUE, iWidth, iHeight, unparse(string.replaceAll("!\\[(.+?)]\\((.+?)\\)", "$2")), unparse(string.replaceAll("!\\[(.+?)]\\((.+?)\\)", "$1")), true, Component.empty()));
            } else if (string.startsWith("<hr") && string.endsWith(">")) {
                if (lastIsPlain) {
                    lastIsPlain = false;
                    Component component = parse(plain);
                    widgets.add(new TextBuilder(component).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
                    plain = "";
                }
                if (lastIsBlockQuote) {
                    lastIsBlockQuote = false;
                    Component component = parse(blockquote);
                    widgets.add(new TextBuilder(component).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
                    blockquote = "";
                }
                widgets.add(new HorizontalRuleBuilder().setPosition(x, -1).build());
            } else if (string.startsWith("#")) {
                if (lastIsPlain) {
                    lastIsPlain = false;
                    Component component = parse(plain);
                    widgets.add(new TextBuilder(component).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
                    plain = "";
                }
                if (lastIsBlockQuote) {
                    lastIsBlockQuote = false;
                    Component component = parse(blockquote);
                    widgets.add(new TextBuilder(component).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
                    blockquote = "";
                }
                int j = 0;
                int scale = 0;
                for (int i = 0; i < string.length() && string.split("")[i].equals("#"); i++){
                    j = i + ((string.split("")[i]+string.split("")[i+1]).equals("# ") ? 2 : 1);
                    scale = i;
                }
                Component component = parse(string.substring(j));
                widgets.add(new ScaledTextBox(x, -40, width - 230, AlinLib.MINECRAFT.font.lineHeight + 5, component, false, 1.5F - ((float) scale / 6)));
            } else if (string.startsWith(">")) {
                if (lastIsPlain) {
                    lastIsPlain = false;
                    Component component = parse(plain);
                    widgets.add(new TextBuilder(component).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
                    plain = "";
                }
                if (!lastIsBlockQuote) lastIsBlockQuote = true;
                string = string.substring(string.contains("> ") ? 2 : 1);
                blockquote += string += "\n";
            } else {
                if (lastIsBlockQuote) {
                    lastIsBlockQuote = false;
                    Component component = parse(blockquote);
                    widgets.add(new TextBuilder(component).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
                    blockquote = "";
                }
                if (!lastIsPlain) lastIsPlain = true;
                if (!string.isBlank()) plain += string += "\n";
            }
        }
        if (lastIsPlain)
            widgets.add(new TextBuilder(parse(plain)).setType(TextBuilder.TYPE.MESSAGE).setPosition(x, -40).setSize(width - 230, 20).build());
        if (lastIsBlockQuote)
            widgets.add(new TextBuilder(parse(blockquote)).setType(TextBuilder.TYPE.BLOCKQUOTE).setPosition(x, -40).setSize(width - 230, 20).build());
        return widgets;
    }

    public static Component parse(String string) {
        String ret = getString(string);
        MutableComponent component = Component.empty();
        String[] links = hehSplit(ret,"\\[(.+?)]\\((.+?)\\)");
        if(links.length >= 1) {
            String sp = ret;
            for(String link : links) sp = sp.replace(link, "[\uE699]");
            int u = 0;
            for(String text : sp.split("\\[\uE699]")){
                component.append(Component.literal(text));
                if(u < links.length) {
                    MutableComponent cLink = Component.empty().withStyle(Style.EMPTY.withUnderlined(true).withColor(CPM_BLUE).withClickEvent(
                            //#if MC >= 12105
                            new ClickEvent.OpenUrl(URI.create(links[u].replaceAll("\\[(.+?)]\\((.+?)\\)", "$2")))
                            //#else
                            //$$ new ClickEvent(ClickEvent.Action.OPEN_URL, links[u].replaceAll("\\[(.+?)]\\((.+?)\\)", "$2"))
                            //#endif
                    )).append(links[u].replaceAll("\\[(.+?)]\\((.+?)\\)", "$1"));
                    component.append(cLink);
                }
                u++;
            }
        } else component.append(Component.literal(ret));
        return component;
    }

    private static @NotNull String getString(String string) {
        String ret = string.replaceAll("\\*\\*(.+?)\\*\\*(?!\\*)", "§l$1§r")
                .replaceAll("\\*(.+?)\\*(?!\\*)", "§o$1§r")
                .replaceAll("__(.+?)__(?!_)", "§n$1§r")
                .replaceAll("_(.+?)_(?!_)", "§o$1§r")
                .replaceAll("~~(.+?)~~(?!~)", "§m$1§r")
                .replaceAll("\\|\\|(.+?)\\|\\|(?!\\|)", "§k$1§r")
                .replace("<br>\n", "\n")
                .replace("\r", "")
                .replace("<br>", "\n");
        if(ret.endsWith("\n")) ret = ret.substring(0, ret.length()-1);
        return ret;
    }

    public static String[] hehSplit(String content, String regex){
        String[] shits = content.split(regex);
        boolean start = true;
        for(String shit : shits) {
            if(!shit.isEmpty() && content.contains(shit)) content = content.replace(shit, start ? "" : "[\uE699]");
            if(start) start = false;
        }
        if(content.endsWith("[\uE699]")) content = content.substring(0, content.length()-3);
        return content.isEmpty() ? new String[0] : content.split("\\[\uE699]");
    }

    public static String unparse(String string) {
        return string.replaceAll("§l(.+?)§r", "**$1**")
                .replaceAll("§o(.+?)§r", "_$1_")
                .replaceAll("§n(.+?)§r", "__$1__")
                .replaceAll("§m(.+?)§r", "~~$1~~")
                .replaceAll("§k(.+?)§r", "||$1||");
    }
}
