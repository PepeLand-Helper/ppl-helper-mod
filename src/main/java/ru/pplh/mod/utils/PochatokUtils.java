package ru.pplh.mod.utils;


import net.theevilm.pochatok.PochatokClient;
import net.theevilm.pochatok.util.InfoProvider;

public class PochatokUtils {
    public static void setSignName(String name){
        name = name.replace("-", " ");
        String altName = "";
        for(String arg : name.split(" ")){
            if(arg.length() > 3) {
                if (!name.startsWith(arg)) altName += " ";
                altName += arg;
            }
        }
        PochatokClient.signRenderer.setSearchString(altName);
        PochatokClient.signRenderer.clearVisited();
        InfoProvider.showSignMessage(true, altName);
    }
}
