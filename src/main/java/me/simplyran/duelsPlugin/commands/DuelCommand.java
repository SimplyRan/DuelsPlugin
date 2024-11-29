package me.simplyran.duelsPlugin.commands;

import com.sun.source.tree.IfTree;
import me.simplyran.duelsPlugin.DuelsPlugin;
import me.simplyran.duelsPlugin.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DuelCommand implements CommandExecutor {

    private HashMap<UUID, DuelRequest> waitingForResponde;
    private MainManager mainManager;

    public DuelCommand(MainManager mainManager){
        waitingForResponde = new HashMap<>();
        this.mainManager = mainManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)){
            System.out.println("You must be a player to send/accept a duel");
            return true;
        }

        Player player = (Player) commandSender;
        if (!player.hasPermission("duels.duel")){
            player.sendMessage("You don't have perms for this command");
        }

        if (strings.length < 2){
            return false;
        }
        String option = strings[0];



        switch (option.toLowerCase()){
            case "send":
                Player player2 = Bukkit.getPlayer(strings[1]);
                if (strings.length < 3){
                    player.sendMessage("Enter kit name!");
                    break;
                }
                String kitName = strings[2];
                if (player2 == null){
                    player.sendMessage("Player not found");
                    break;
                }
                if (!mainManager.getKits().containsKey(kitName)){
                    player.sendMessage("Kit not found");
                    break;
                }
                if (waitingForResponde.containsKey(player.getUniqueId())){
                    player.sendMessage("You already sent a duel for someone!");
                    break;
                }
                if (player.getUniqueId() == player2.getUniqueId()){
                    player.sendMessage("You cannot send duel to yourself!");
                    break;
                }

                sendDuel(player , player2 , mainManager.getKits().get(kitName));
                break;

            case "accept":
                Player sender = Bukkit.getPlayer(strings[1]);
                if (sender == null){
                    player.sendMessage("Player not found");
                    break;
                }

                if (!waitingForResponde.containsKey(sender.getUniqueId())){
                    player.sendMessage("Request for duel not found");
                    break;
                }
                if (mainManager.getEmptyArenas().isEmpty()){
                    player.sendMessage("All arenas in game");
                    break;
                }
                acceptDuel(sender , player);

                break;
            default:
                player.sendMessage("Please choose send|accept");
                break;

        }

        return false;
    }

    private void sendDuel(Player sender , Player recipient , KitManager kit){
        recipient.sendMessage(sender.getName() + " invited you to a "+ kit.getKitName() +
                " duel! /duel accept " + sender.getName() + " to accept");
        sender.sendMessage("You invited " + recipient.getName() +
                " to a " + kit.getKitName() + " duel");
        waitingForResponde.put(sender.getUniqueId() , new DuelRequest(sender.getUniqueId() , recipient.getUniqueId() , kit));

        mainManager.getPlugin().getServer().getScheduler().runTaskLater(mainManager.getPlugin(), () -> {
            if (waitingForResponde.containsKey(sender.getUniqueId())) {

                waitingForResponde.remove(sender.getUniqueId());
                sender.sendMessage("Your duel request to " + recipient.getName() + " has expired.");
                recipient.sendMessage("The duel request from " + sender.getName() + " has expired.");
            }
        }, 60 * 20L);
    }

    private void acceptDuel(Player sender , Player recipient){
        DuelRequest duelRequest = waitingForResponde.get(sender.getUniqueId());
        KitManager kit = duelRequest.getKit();
        ArenaManager arena = mainManager.getEmptyArenas().remove();
        UUID[] playersUUID = {sender.getUniqueId() , recipient.getUniqueId()};
        Location[] locationsBeforeGame = {sender.getLocation() , recipient.getLocation()};
        DuelGameManager newGame = new DuelGameManager(arena , kit , playersUUID , locationsBeforeGame , mainManager);
        mainManager.getActiveGames().put(new HashSet<>(Set.of(playersUUID[0], playersUUID[1])), newGame);
        waitingForResponde.remove(sender.getUniqueId());
    }



}
