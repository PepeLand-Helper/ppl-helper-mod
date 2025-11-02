package ru.pplh.mod.utils;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.ActivityType;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.StatusDisplayType;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.pplh.mod.PepeLandHelper;

import java.io.IOException;
import java.time.Instant;

public class DiscordActivityManager {
    private static IPCClient client;
    private static RichPresence currentActivity;
    private static long startTime;
    private static boolean initialized = false;

    public static void initialize() {
        try {
            client = new IPCClient(1299064772943155322L);
            startTime = Instant.now().getEpochSecond();

            initialized = true;
            client.connect();

        } catch (Exception e) {
            System.err.println("Failed to initialize Discord RPC: " + e.getMessage());
        }
    }

    public static void updatePresence() {
        if (!initialized || client == null) return;
        try {
            String details = AlinLib.localization.getParsedText(Localization.fixFormatCodes(PepeLandHelper.config.getString("DISCORD.DETAILS", Component.translatable("pplhelper.configs.discord.details.default").getString())));
            String state = AlinLib.localization.getParsedText(Localization.fixFormatCodes(PepeLandHelper.config.getString("DISCORD.STATE", Component.translatable("pplhelper.configs.discord.state.default").getString())));
            RichPresence.Builder builder = new RichPresence.Builder();
            builder.setActivityType(ActivityType.Playing);
            builder.setStatusDisplayType(StatusDisplayType.Details);
            builder.setDetails(details);
            builder.setState(state);
            builder.setStartTimestamp(startTime);
            builder.setLargeImage("https://wf.kelcu.ru/logos/ppl7.png", "PepeLand", "https://pepeland.net");
            if(currentActivity == builder.build()) return;
            currentActivity = builder.build();
            client.sendRichPresence(currentActivity);
        } catch (Exception e) {
            System.err.println("Error updating Discord presence: " + e.getMessage());
        }
    }

    public static void clearPresence() {
        if (!initialized || client == null) return;

        try {

        } catch (Exception e) {
            System.err.println("Error clearing Discord presence: " + e.getMessage());
        }
    }

    public static void shutDown() {
        initialized = false;
        if (client != null) {
            try {
                clearPresence();
                client.close();
                client = null;
            } catch (Exception e) {
                System.err.println("Error shutting down Discord RPC: " + e.getMessage());
            }
        }
    }
}