package ru.pplh.mod.sailstatus;

import ru.kelcuprum.sailstatus.SailStatus;

public class SailStatusManager {
    public static void register(){
        SailStatus.registerModPresence(new PepeLandPresence());
    }

}
