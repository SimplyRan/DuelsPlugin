package me.simplyran.duelsPlugin.managers;

import java.util.UUID;

public class DuelRequest {

    private final UUID sender;
    private final UUID recipient;
    private final KitManager kit;

    public DuelRequest(UUID sender , UUID recipient , KitManager kit){
        this.sender = sender;
        this.recipient = recipient;
        this.kit = kit;
    }

    public KitManager getKit(){
        return kit;
    }

}
