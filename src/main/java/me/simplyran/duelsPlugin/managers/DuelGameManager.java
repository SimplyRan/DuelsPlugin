package me.simplyran.duelsPlugin.managers;

import me.simplyran.duelsPlugin.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class DuelGameManager {

    private final ArenaManager arena;
    private final KitManager kit;
    private final UUID[] playersUUID;
    private GameState currentState;
    private final Location[] locationsBeforeGame;
    private final MainManager mainManager;

    public DuelGameManager(ArenaManager arena , KitManager kit , UUID[] playersUUID , Location[] locationsBeforeGame , MainManager mainManager){
        this.locationsBeforeGame =locationsBeforeGame;
        this.arena = arena;
        this.currentState = GameState.STARTING;
        this.playersUUID = playersUUID;
        this.kit = kit;
        this.mainManager = mainManager;
        startGame();
    }

    public void startGame(){
        Location spawnLocation1 = arena.getSpawnLocation()[0];
        Location spawnLocation2 = arena.getSpawnLocation()[1];
        Player player1 = Bukkit.getPlayer(playersUUID[0]);
        Player player2 = Bukkit.getPlayer(playersUUID[1]);

        player1.setHealth(player1.getMaxHealth());
        player2.setHealth(player2.getMaxHealth());


        player1.teleport(spawnLocation1);
        player2.teleport(spawnLocation2);
        player1.getInventory().clear();
        player2.getInventory().clear();

        player1.getInventory().setArmorContents(kit.getArmor());
        player2.getInventory().setArmorContents(kit.getArmor());

        player1.getInventory().addItem(kit.getItems());
        player2.getInventory().addItem(kit.getItems());

        player1.setGameMode(GameMode.ADVENTURE);
        player2.setGameMode(GameMode.ADVENTURE);
        currentState = GameState.IN_PROGRESS;
    }

    public UUID[] getPlayersUUID(){
        return playersUUID;
    }

    public void endGame(Player loser){
        currentState = GameState.ENDING;
        Player winner = playersUUID[0] == loser.getUniqueId() ?
                Bukkit.getPlayer(playersUUID[1]) : Bukkit.getPlayer(playersUUID[0]);
        if (winner != null) {
            winner.sendTitle("You Won!", "", 10 , 40 , 10);
            mainManager.getPlugin().getServer().getScheduler().runTaskLater(mainManager.getPlugin(), () -> {
                winner.teleport(playersUUID[0] == loser.getUniqueId() ? locationsBeforeGame[1] : locationsBeforeGame[0]);
                }, 5 *20L);
            winner.setHealth(winner.getMaxHealth());
            winner.getInventory().clear();
        }
        loser.setHealth(loser.getMaxHealth());
        loser.sendTitle("You Lost...", "", 10 , 40 , 10);
        loser.teleport(playersUUID[0] == loser.getUniqueId() ? locationsBeforeGame[0] : locationsBeforeGame[1]);
        loser.getInventory().clear();
        mainManager.getEmptyArenas().add(arena);
        for (Set<UUID> uuids : mainManager.getActiveGames().keySet()) {
            if (uuids.contains(loser.getUniqueId()))
                mainManager.getActiveGames().remove(uuids);
        }

    }



}
