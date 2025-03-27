package ru.kelcuprum.pplhelper.utils;

import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.info.Player;
import ru.kelcuprum.pplhelper.PepeLandHelper;

public class ChatFilter {

    public static boolean enableWorld(String message){
        if(isWorldEnabled(message)){
            String world = message.replaceAll("\\[Мир: (.+?)].+", "$1");
            TabHelper.Worlds worlds = TabHelper.getWorldByName(world);
            if(worlds == null) {
                AlinLib.LOG.log(world);
                return true;
            }
            return PepeLandHelper.config.getBoolean(String.format("CHAT.FILTER.WORLD.%s", worlds.shortName.toUpperCase()), true);
        }
        return true;
    }

    public static boolean isWorldEnabled(String message){
        return message.replaceAll("\\[Мир: (.+?)]", "").length() < message.length();
    }
    public static boolean isMysteryBox(String message){
        return message.contains("Mystery Box");
    }

    public static boolean isJoin(String message){
        return message.startsWith("[+] ");
    }
    public static boolean isLeave(String message){
        return message.startsWith("[-] ");
    }


    public static boolean isGlobalChat(String message){
        return message.startsWith("[G] ") || message.replaceAll("\\[Мир: (.+?)] \\[G]", "").length() < message.length();
    }
    public static boolean mention(String message){
        return message.contains(Player.getName());
    }

    public static boolean isFriend(String message){
        boolean response = false;
        String nickname = message.replaceAll("\\[[+-]] (.+?)", "$1");
        String[] nicknames = PepeLandHelper.config.getString("CHAT.FILTER.FRIENDS", "PWGoood, Gwinsen, Pooshka").replaceAll("[^a-zA-Z0-9_,]", "").split(",");
        for(String friend : nicknames) {
            if(friend.equalsIgnoreCase(nickname)){
                response = true;
                break;
            }
        }
        return response;
    }
    public static boolean isContainsNWords(String message){
        boolean response = false;
        String[] args = message.split(" ");
        String[] words = PepeLandHelper.config.getString("CHAT.FILTER.NWORDS", "").replaceAll("[ ]", "").split(",");
        for(String arg : args) {
            for(String word : words) {
                if(arg.equals(word)){
                    response = true;
                    break;
                }
            }
        }
        return response;
    }

}
