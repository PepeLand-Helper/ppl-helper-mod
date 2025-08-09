package ru.kelcuprum.pplhelper.interactive;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.screens.DialogScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class Action {
    public String type;
    public String content;
    public List<int[]> area = new ArrayList<>();
    public boolean isExecuted = false;
    public Action(JsonObject jsonObject){
        type = jsonObject.get("type").getAsString();
        content = jsonObject.get("content").getAsString();
        String stringedCoordinates = jsonObject.get("area").getAsString();
        String[] coordinates = stringedCoordinates.split(", ");
        for(String buh : coordinates){
            String[] buh2 = buh.split(" ");
            int[] buh3 = new int[3];
            for(int gih = 0; gih < buh2.length;gih++)
                buh3[gih] = parseInt(buh2[gih]);
            area.add(buh3);
        }
        if(area.size() == 1) area.add(area.get(0));
        int[] pos1 = area.get(0);
        int[] pos2 = area.get(1);
        int[] c1 = validIntPos(pos1[0], pos2[0]);
        int[] c2 = validIntPos(pos1[1], pos2[1]);
        int[] c3 = validIntPos(pos1[2], pos2[2]);
        pos1 = new int[]{c1[0], c2[0], c3[0]};
        pos2 = new int[]{c1[1], c2[1], c3[1]};
        area = List.of(pos1, pos2);

    }
    public static int[] validIntPos(int one, int two){
        if(one > two) return new int[] {two, one};
        else return new int[] {one, two};
    }

    public void execute(){
        if(!isExecuted || type.equals("particle_circle")){
            isExecuted = true;
            switch (type){
                case "dialog" -> AlinLib.MINECRAFT.setScreen(new DialogScreen(AlinLib.MINECRAFT.screen, content.split("\n"), null));
                case "chat" -> AlinLib.MINECRAFT.getChatListener().handleSystemMessage(Component.literal(content), false);
                case "actionbar" -> AlinLib.MINECRAFT.getChatListener().handleSystemMessage(Component.literal(content), true);
                case "sound" ->
                    AlinLib.MINECRAFT.level.playLocalSound(AlinLib.MINECRAFT.player, new SoundEvent(ResourceLocation.parse(content), Optional.of(1f)), SoundSource.MASTER, 1f, 1f);
                case "stop_sound" -> {
                    if(content.isEmpty()) AlinLib.MINECRAFT.getSoundManager().stop();
                    else AlinLib.MINECRAFT.getSoundManager().stop(ResourceLocation.parse(content), SoundSource.MASTER);
                }
                case "particle_circle" -> {
                    int[] coordinates = area.getFirst();
                    particleCircle(AlinLib.MINECRAFT.levelRenderer, coordinates[0], coordinates[1], coordinates[2], parseInt(content.split(", ")[0]), (int) parseLong(content.split(", ")[1], 16));
                }
                default -> AlinLib.MINECRAFT.getChatListener().handleSystemMessage(Component.literal(String.format("[PPL Helper] Действие %s с контентом \"%s\" не поддерживается", type, content)), false);
            }
        }
    }
    public static void particleCircle(LevelRenderer level, double x, double y, double z, double r, int color){
        long i = System.currentTimeMillis() % 2000;
        long j = i >= 1000 ? -(1000-i) : i-1000;
        double h = ((double) j /1000);
        double xP = r * Math.cos(h*360 * (Math.PI/180));
        double zP = r * Math.sin(h*360 * (Math.PI/180));
        level.addParticle(new DustParticleOptions(color, 1.0f), true, true, x+xP, y, z+zP, 0, 0, 0);
    }
}
