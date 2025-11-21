package ru.pplh.mod.mods.sailstatus;

import com.jagrosh.discordipc.entities.ActivityType;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.StatusDisplayType;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.sailstatus.SailStatus;
import ru.kelcuprum.sailstatus.presence.AbstractPresence;
import ru.pplh.mod.PepeLandHelper;

import static ru.kelcuprum.sailstatus.SailStatus.sendPresence;

public class PepeLandPresence extends AbstractPresence {
    public PepeLandPresence() {
        super(TYPES.IN_GAME);
    }

    @Override
    public void execute() {
        String details = AlinLib.localization.getParsedText(Localization.fixFormatCodes(PepeLandHelper.config.getString("DISCORD.DETAILS", Component.translatable("pplhelper.configs.discord.details.default").getString())));
        String state = AlinLib.localization.getParsedText(Localization.fixFormatCodes(PepeLandHelper.config.getString("DISCORD.STATE", Component.translatable("pplhelper.configs.discord.state.default").getString())));
        RichPresence.Builder builder = new RichPresence.Builder();
        builder.setActivityType(ActivityType.Playing);
        builder.setStatusDisplayType(StatusDisplayType.Details);
        builder.setDetails(details);
        builder.setState(state);
        builder.setStartTimestamp(SailStatus.TIME_STARTED_CLIENT);
        builder.setLargeImage("https://wf.kelcu.ru/logos/ppl7.png", "PepeLand", "https://pepeland.net");
        sendPresence(builder.build());
    }

    @Override
    public boolean avaliable() {
        return PepeLandHelper.playerInPPL() && PepeLandHelper.config.getBoolean("DISCORD", false);
    }
}
