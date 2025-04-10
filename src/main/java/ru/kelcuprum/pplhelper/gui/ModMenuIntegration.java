package ru.kelcuprum.pplhelper.gui;

import com.terraformersmc.modmenu.api.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.VersionInfo;
import ru.kelcuprum.pplhelper.gui.screens.NewsListScreen;
import ru.kelcuprum.pplhelper.gui.screens.UpdaterScreen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return PepeLandHelperAPI.apiAvailable() ? NewsListScreen::new : new UpdaterScreen()::build;
    }

    @Override
    public UpdateChecker getUpdateChecker() {
        if (PepeLandHelperAPI.apiAvailable()) {
            return () -> {
                VersionInfo versionInfo = PepeLandHelperAPI.getAutoUpdate(new Config("config/pplhelper/config.json").getBoolean("UPDATER.FOLLOW_TWO_DOT_ZERO", true));

                return new UpdateInfo() {
                    @Override
                    public @Nullable Component getUpdateMessage() {
                        return versionInfo.state == VersionInfo.State.NEW_UPDATE ? Component.translatable("pplhelper.update", versionInfo.latestVersion) : null;
                    }

                    @Override
                    public boolean isUpdateAvailable() {
                        return versionInfo.state == VersionInfo.State.NEW_UPDATE;
                    }

                    @Override
                    public String getDownloadLink() {
                        return versionInfo.page;
                    }

                    @Override
                    public UpdateChannel getUpdateChannel() {
                        return versionInfo.latestVersion.contains("alpha") ? UpdateChannel.ALPHA : versionInfo.latestVersion.contains("beta") || versionInfo.latestVersion.contains("rc") ? UpdateChannel.BETA : UpdateChannel.RELEASE;
                    }
                };
            };
        } else return null;
    }
}