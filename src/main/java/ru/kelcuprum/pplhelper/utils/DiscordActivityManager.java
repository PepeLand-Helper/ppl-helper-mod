package ru.kelcuprum.pplhelper.utils;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.pplhelper.PepeLandHelper;

import java.io.IOException;
import java.time.Instant;

public class DiscordActivityManager {
    private static Core discordCore;
    private static Activity currentActivity;
    private static long startTime;
    private static boolean initialized = false;

    public static void initialize() {
        try {
            Core.initDownload();
            Core.initDiscordNative("discord_game_sdk");

            CreateParams params = new CreateParams();

            // Воткни сюда свой айди или оставь айдишник моего бота
            params.setClientID(1429038037899411558L);

            params.setFlags(CreateParams.getDefaultFlags());

            discordCore = new Core(params);
            currentActivity = new Activity();
            startTime = Instant.now().getEpochSecond();

            initialized = true;

            Thread callbackThread = new Thread(() -> {
                while (initialized && discordCore != null) {
                    try {
                        discordCore.runCallbacks();
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("Error in Discord callback thread: " + e.getMessage());
                        break;
                    }
                }
            }, "Discord-RPC-Callback");
            callbackThread.setDaemon(true);
            callbackThread.start();

        } catch (Exception e) {
            System.err.println("Failed to initialize Discord RPC: " + e.getMessage());
        }
    }

    public static void updatePresence(String worldName, int playerCount, int maxPlayers) {
        if (!initialized || discordCore == null) return;

        try {
            String details = (Boolean.parseBoolean(PepeLandHelper.config.getString("DS_DETAILS")) ?
                    PepeLandHelper.config.getString("DS_DETAILS") : Component.translatable("pplhelper.configs.ds.details.default").getString())
                    .replace("%playersCount%", String.valueOf(playerCount))
                    .replace("%maxPlayers%", String.valueOf(maxPlayers));

            String state = (Boolean.parseBoolean(PepeLandHelper.config.getString("DS_STATE")) ?
                    PepeLandHelper.config.getString("DS_STATE") : Component.translatable("pplhelper.configs.ds.state.default").getString())
                    .replace("%worldName%", worldName);

            currentActivity.setDetails(details);
            currentActivity.setState(state);
            currentActivity.timestamps().setStart(Instant.ofEpochSecond(startTime));

            currentActivity.assets().setLargeImage("logo");
            currentActivity.assets().setLargeText("Pepeland");

            discordCore.activityManager().updateActivity(currentActivity);
        } catch (Exception e) {
            System.err.println("Error updating Discord presence: " + e.getMessage());
        }
    }

    public static void clearPresence() {
        if (!initialized || discordCore == null) return;

        try {
            discordCore.activityManager().clearActivity();
        } catch (Exception e) {
            System.err.println("Error clearing Discord presence: " + e.getMessage());
        }
    }

    public static void shutDown() {
        initialized = false;
        if (discordCore != null) {
            try {
                clearPresence();
                discordCore.close();
                discordCore = null;
            } catch (Exception e) {
                System.err.println("Error shutting down Discord RPC: " + e.getMessage());
            }
        }
    }
}