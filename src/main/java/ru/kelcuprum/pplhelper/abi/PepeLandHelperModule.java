package ru.kelcuprum.pplhelper.abi;

import net.minecraft.network.chat.Component;
import ru.kelcuprum.abi.ActionBarInfo;
import ru.kelcuprum.abi.modules.abstracts.AbstractModule;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.pplhelper.PepeLandHelper;

public class PepeLandHelperModule extends AbstractModule {
    public PepeLandHelperModule() {
        super("ppl_info", "pplhelper", Component.translatable("pplhelper.module.info"));
    }

    @Override
    public Component getMessage() {
        String msg = AlinLib.localization.getParsedText(Localization.fixFormatCodes(PepeLandHelper.config.getString("INFO.PPLHELPER", ActionBarInfo.localization.getLocalization("info.pplhelper", false, false, false))));
        return Component.literal(msg);
    }

    @Override
    public boolean isEnabled() {
        return PepeLandHelper.playerInPPL();
    }
}
