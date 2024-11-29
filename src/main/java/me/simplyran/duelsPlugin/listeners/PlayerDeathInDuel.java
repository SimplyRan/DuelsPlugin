package me.simplyran.duelsPlugin.listeners;

import me.simplyran.duelsPlugin.managers.DuelGameManager;
import me.simplyran.duelsPlugin.managers.MainManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class PlayerDeathInDuel implements Listener {

    private final MainManager mainManager;

    public PlayerDeathInDuel(MainManager mainManager){
        this.mainManager = mainManager;
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e){
        Player loser = e.getEntity();
        e.getDrops().clear();
        DuelGameManager game = mainManager.getGameByUUID(loser.getUniqueId());
        if (game != null){
            game.endGame(loser);
        }
    }


}
