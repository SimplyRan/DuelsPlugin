package me.simplyran.duelsPlugin.listeners;

import me.simplyran.duelsPlugin.managers.DuelGameManager;
import me.simplyran.duelsPlugin.managers.MainManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerHitToDeath implements Listener {

    private final MainManager mainManager;

    public PlayerHitToDeath(MainManager mainManager){
        this.mainManager = mainManager;
    }


    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player loser = (Player) e.getEntity();
            DuelGameManager game = mainManager.getGameByUUID(loser.getUniqueId());
            if (((Player) e.getEntity()).getPlayer().getHealth() - e.getFinalDamage() <= 0 && game != null) {
                e.setCancelled(true);
                game.endGame(loser);
            }
        }
    }
}

