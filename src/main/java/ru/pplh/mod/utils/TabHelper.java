package ru.pplh.mod.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.PepeLandHelper;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class TabHelper {
    public static String[] worlds = new String[]{
            "Лобби",
            "Постройки #1",
            "Постройки #2",
            "Ресурсы",
            "Фермы",
            "Торговая Зона",
            "Энд",
            "PepeLand Awards"
    };

    public static Worlds getWorld() {
        if (!PepeLandHelper.playerInPPL()) return null;
        StringBuilder world = new StringBuilder();
        if (AlinLib.MINECRAFT.gui.getTabList().header == null) {
            if (PepeLandHelper.isTestSubject() && PepeLandHelper.config.getBoolean("IM_A_TEST_SUBJECT.ENABLE_WORLD", false))
                world = new StringBuilder(PepeLandHelper.config.getString("IM_A_TEST_SUBJECT.WORLD", worlds[0]));
        } else {
            String[] args = AlinLib.MINECRAFT.gui.getTabList().header.getString().split("\n");
            for (String arg : args) {
                if (arg.contains("Мир:")) {
                    String[] parsed = arg.replace("Мир:", "").replaceAll("[^A-Za-zА-Яа-я #0-9]", "").split(" ");
                    boolean first = true;
                    for (String parsed_arg : parsed) {
                        if (!parsed_arg.isBlank()) {
                            world.append(first ? "" : " ").append(parsed_arg);
                            if (first) first = false;
                        }
                    }
                }
            }
        }

        return getWorldByName(world.toString());
    }

    public static Worlds getWorldByName(String name){
        return switch (name) {
            case "Лобби" -> Worlds.LOBBY;
            case "Постройки #1" -> Worlds.CONSTRUCTIONS_1;
            case "Постройки #2" -> Worlds.CONSTRUCTIONS_2;
            case "Ресурсы" -> Worlds.RESOURCE;
            case "Фермы" -> Worlds.FARM;
            case "Торговая Зона" -> Worlds.TRADE;
            case "Энд" -> Worlds.END;
            case "PepeLand Awards" -> Worlds.AWARDS;
            default -> null;
        };
    }
    public static Worlds getWorldByShortName(String name){
        return switch (name.toLowerCase()) {
            case "лобби" -> Worlds.LOBBY;
            case "мп1" -> Worlds.CONSTRUCTIONS_1;
            case "мп2" -> Worlds.CONSTRUCTIONS_2;
            case "мр" -> Worlds.RESOURCE;
            case "мф" -> Worlds.FARM;
            case "тз" -> Worlds.TRADE;
            case "знд" -> Worlds.END;
            case "awards" -> Worlds.AWARDS;
            default -> null;
        };
    }

    public static double getTPS() {
        double tps = 0;
        if (!PepeLandHelper.playerInPPL() || AlinLib.MINECRAFT.gui.getTabList().footer == null) return tps;
        String[] args = AlinLib.MINECRAFT.gui.getTabList().footer.getString().split("\n");
        for (String arg : args) {
            if (arg.contains("TPS:")) {
                String parsed = arg.replace("TPS:", "").replaceAll("[^0-9.]", "");
                tps = parseDouble(parsed);
            }
        }
        return tps;
    }

    public static int getOnline() {
        int tps = 0;
        if (!PepeLandHelper.playerInPPL() || AlinLib.MINECRAFT.gui.getTabList().footer == null) return tps;
        String[] args = AlinLib.MINECRAFT.gui.getTabList().footer.getString().split("\n");
        for (String arg : args) {
            if (arg.contains("Онлайн:")) {
                String[] parsed = arg.replace("Онлайн:", "").replaceAll("[^0-9/]", "").split("/");
                tps = parseInt(parsed[0]);
            }
        }
        return tps;
    }

    public static int getMaxOnline() {
        int tps = 0;
        if (!PepeLandHelper.playerInPPL() || AlinLib.MINECRAFT.gui.getTabList().footer == null)
            return tps;
        String[] args = AlinLib.MINECRAFT.gui.getTabList().footer.getString().split("\n");
        for (String arg : args) {
            if (arg.contains("Онлайн:")) {
                String[] parsed = arg.replace("Онлайн:", "").replaceAll("[^0-9/]", "").split("/");
                tps = parseInt(parsed[1]);
            }
        }
        return tps;
    }

    public enum Worlds {
        LOBBY(Component.translatable("pplhelper.world.lobby"), "Лобби"),
        RESOURCE(Component.translatable("pplhelper.world.resource"), "МР"),
        CONSTRUCTIONS_1(Component.translatable("pplhelper.world.constructions.1"), "МП1"),
        CONSTRUCTIONS_2(Component.translatable("pplhelper.world.constructions.2"), "МП2"),
        FARM(Component.translatable("pplhelper.world.farm"), "МФ"),
        TRADE(Component.translatable("pplhelper.world.trade"), "ТЗ"),
        END(Component.translatable("pplhelper.world.end"), "Энд"),
        AWARDS(Component.literal("PepeLand Awards"), "Awards");
        public final Component title;
        public final String shortName;

        Worlds(Component title, String shortName) {
            this.title = title;
            this.shortName = shortName;
        }
    }

    public static Component getGradient(String component, int color1, int color2){
        return getGradient(Component.literal(component), color1, color2);
    }
    public static Component getGradient(Component component, int color1, int color2) {
        String test = component.getString();
        String[] testArray = test.split("");
        MutableComponent respComponent = Component.empty();
        for (int i = 0; i < testArray.length; i++)
            respComponent.append(Component.empty().append(testArray[i]).withColor(TabHelper.interpolate(color1, color2, (float) i / (testArray.length-1))));
        return respComponent;
    }

    // https://habr.com/ru/articles/180839/
    public static int interpolate(int color1, int color2, float progress) {
        //Разделяем оба цвета на составляющие
        int a1 = (color1 & 0xff000000) >>> 24;
        int r1 = (color1 & 0x00ff0000) >>> 16;
        int g1 = (color1 & 0x0000ff00) >>> 8;
        int b1 = color1 & 0x000000ff;

        int a2 = (color2 & 0xff000000) >>> 24;
        int r2 = (color2 & 0x00ff0000) >>> 16;
        int g2 = (color2 & 0x0000ff00) >>> 8;
        int b2 = color2 & 0x000000ff;

        //И рассчитываем новые
        float progress2 = (1 - progress);
        int newA = clip((int) (a1 * progress2 + a2 * progress));
        int newR = clip((int) (r1 * progress2 + r2 * progress));
        int newG = clip((int) (g1 * progress2 + g2 * progress));
        int newB = clip((int) (b1 * progress2 + b2 * progress));

        //Собираем и возвращаем полученный цвет
        return (newA << 24) + (newR << 16) + (newG << 8) + newB;
    }

    private static int clip(int num) {
        return num <= 0 ? 0 : Math.min(num, 255);
    }
}
