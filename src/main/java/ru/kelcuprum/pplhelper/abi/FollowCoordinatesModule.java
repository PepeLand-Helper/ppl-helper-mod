package ru.kelcuprum.pplhelper.abi;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.abi.modules.abstracts.AbstractModule;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.utils.FollowManager;
import ru.kelcuprum.pplhelper.utils.TabHelper;

public class FollowCoordinatesModule extends AbstractModule {
    public FollowCoordinatesModule() {
        super("ppl_follow_coordinates", "pplhelper", Component.translatable("pplhelper.module.follow_coordinates"));
    }

    @Override
    public Component getMessage() {
        String msg = "";
        FollowManager.Coordinates coordinates = FollowManager.getCurrentCoordinates();
        if (coordinates != null) {
            String huy = "";
            String gameWorld = " "+(FollowManager.playerInCurrentLevel() ? "" : FollowManager.getLevelName(coordinates.level()));
            huy += String.format("&6%s:&r %s%s", coordinates.world().shortName, coordinates.getStringCoordinates(), gameWorld);
            LocalPlayer p = AlinLib.MINECRAFT.player;
            if (FollowManager.playerInCurrentWorld() && gameWorld.isBlank()) {
                long near = (long) FollowManager.dist(coordinates.coordinates()[0], coordinates.coordinates()[coordinates.coordinates().length-1], p.getBlockX(), p.getBlockZ());
                if (near <= PepelandHelper.config.getNumber("SELECTED_PROJECT.AUTO_HIDE", 5).intValue()) {
                    FollowManager.resetCoordinates();
                } else huy += String.format(" &6(%s блоков от вас)&r", near);
            }
            msg = Localization.fixFormatCodes(huy);
        }
        return Component.literal(msg);
    }

    @Override
    public boolean isEnabled() {
        return PepelandHelper.playerInPPL() && FollowManager.getCurrentCoordinates() != null && TabHelper.getWorld() != null && PepelandHelper.config.getBoolean("SPROJECT.ABI", true);
    }
}
