package ru.kelcuprum.pplhelper;

import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class TabHelper {
    public static Worlds getWorld(){
        if((AlinLib.MINECRAFT.getCurrentServer() == null || !AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net")) || AlinLib.MINECRAFT.gui.getTabList().header == null) return null;
        String[] args = AlinLib.MINECRAFT.gui.getTabList().header.getString().split("\n");
        StringBuilder world = new StringBuilder();
        for(String arg : args){
            if(arg.contains("Мир:")){
                String[] parsed = arg.replace("Мир:", "").replaceAll("[^A-Za-zА-Яа-я #0-9]", "").split(" ");
                boolean first = true;
                for(String parsed_arg : parsed) {
                    if(!parsed_arg.isBlank()) {
                        world.append(first ? "" : " ").append(parsed_arg);
                        if(first) first = false;
                    }
                }
            }
        }
        return switch (world.toString()){
            case "Лобби" -> Worlds.LOBBY;
            case "Постройки #1" -> Worlds.CONSTRUCTIONS_1;
            case "Постройки #2" -> Worlds.CONSTRUCTIONS_2;
            case "Ресурсы" -> Worlds.RESOURCE;
            case "Фермы" -> Worlds.FARM;
            case "Торговля" -> Worlds.TRADE;
            default -> null;
        };
    }
    public static double getTPS(){
        double tps = 0;
        if((AlinLib.MINECRAFT.getCurrentServer() == null || !AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net")) || AlinLib.MINECRAFT.gui.getTabList().footer == null) return tps;
        String[] args = AlinLib.MINECRAFT.gui.getTabList().footer.getString().split("\n");
        for(String arg : args){
            if(arg.contains("TPS:")){
                String parsed = arg.replace("TPS:", "").replaceAll("[^0-9.]", "");
                tps = parseDouble(parsed);
            }
        }
        return tps;
    }
    public static int getOnline(){
        int tps = 0;
        if((AlinLib.MINECRAFT.getCurrentServer() == null || !AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net")) || AlinLib.MINECRAFT.gui.getTabList().footer == null) return tps;
        String[] args = AlinLib.MINECRAFT.gui.getTabList().footer.getString().split("\n");
        for(String arg : args){
            if(arg.contains("Онлайн:")){
                String[] parsed = arg.replace("Онлайн:", "").replaceAll("[^0-9/]", "").split("/");
                tps = parseInt(parsed[0]);
            }
        }
        return tps;
    }
    public static int getMaxOnline(){
        int tps = 0;
        if((AlinLib.MINECRAFT.getCurrentServer() == null || !AlinLib.MINECRAFT.getCurrentServer().ip.contains("pepeland.net")) || AlinLib.MINECRAFT.gui.getTabList().footer == null) return tps;
        String[] args = AlinLib.MINECRAFT.gui.getTabList().footer.getString().split("\n");
        for(String arg : args){
            if(arg.contains("Онлайн:")){
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
        TRADE(Component.translatable("pplhelper.world.trade"), "ТЦ"),
        END(Component.translatable("pplhelper.world.end"), "Энд");
        public Component title;
        public String shortName;
        Worlds(Component title, String shortName){
            this.title = title;
            this.shortName = shortName;
        }
    }
}
