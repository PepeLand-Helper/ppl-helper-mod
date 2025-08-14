package ru.pplh.mod.abi;

import ru.kelcuprum.abi.modules.ModulesManager;

public class ABIManager {
    public static void register(){
        ModulesManager.registerModule(new PepeLandHelperModule());
        ModulesManager.registerModule(new FollowCoordinatesModule());
    }
}
