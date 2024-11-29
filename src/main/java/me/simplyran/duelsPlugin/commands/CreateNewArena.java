package me.simplyran.duelsPlugin.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.simplyran.duelsPlugin.DuelsPlugin;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;

public class CreateNewArena implements CommandExecutor {

    private DuelsPlugin plugin;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CreateNewArena(DuelsPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)){
            System.out.println("Player must use this command!");
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("duels.arena")){
            player.sendMessage("You don't have perms for this command");
        }

        if (strings.length < 2) {
            player.sendMessage("Usage: /arena edit|create <name> options");
            return false;
        }
        switch (strings[0].toLowerCase()){
            case "create":
                createArena(player , strings[1]);
                break;
            case "edit":
                if (strings.length < 3){
                    player.sendMessage("Enter spawnpoint to edit! spawnpoint1 | spawnpoint2");
                    break;
                }
                if (!strings[2].equalsIgnoreCase("spawnpoint1") && !strings[2].equalsIgnoreCase("spawnpoint2")) {
                    player.sendMessage("Enter valid spawnpoint!");
                    break;
                }
                changeSpawnLocation(strings[1] , strings[2] , player);
                plugin.getMainManager().loadData();
                break;
            default:
                player.sendMessage("Option not found!");
        }





        return true;
    }

    private void changeSpawnLocation(String arenaName , String spawnPointType , Player player){
        Location locationOfPlayer = player.getLocation();
        JsonObject spawnLocation = getJsonObjectByLoction(locationOfPlayer);
        File arenaFile = new File(plugin.getDataFolder(), "data/arenas.json");
        JsonObject arenaJson;


        try {
            if (arenaFile.exists()) {
                try (FileReader reader = new FileReader(arenaFile)) {
                    arenaJson = JsonParser.parseReader(reader).getAsJsonObject();
                }
            } else {
                arenaJson = new JsonObject();
            }
            if (!arenaJson.has(arenaName)){
                player.sendMessage( arenaName + " not found. ");
                return;
            }
            JsonObject arenaData = arenaJson.getAsJsonObject(arenaName);
            arenaData.add(spawnPointType.toLowerCase(), spawnLocation);
            arenaJson.add(arenaName, arenaData);

            try (FileWriter writer = new FileWriter(arenaFile)) {
                gson.toJson(arenaJson, writer);
                player.sendMessage("Edited " + spawnPointType + " to your new location!");
            }

            // Notify the player
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createArena(Player player, String arenaName) {
        File arenaFile = new File(plugin.getDataFolder(), "data/arenas.json");
        JsonObject arenaJson;

        try {
            if (arenaFile.exists()) {
                try (FileReader reader = new FileReader(arenaFile)) {
                    arenaJson = JsonParser.parseReader(reader).getAsJsonObject();
                }
            } else {
                arenaJson = new JsonObject();
            }

            if (arenaJson.has(arenaName)) {
                player.sendMessage("Arena '" + arenaName + "' already exists.");
                return;
            }

            JsonObject newArena = new JsonObject();
            arenaJson.add(arenaName, newArena);
            JsonObject spawnLocation = getJsonObjectByLoction(player.getLocation());
            newArena.add("spawnpoint1", spawnLocation);
            newArena.add("spawnpoint2", spawnLocation);
            arenaJson.add(arenaName , newArena);


            try (FileWriter writer = new FileWriter(arenaFile)) {
                gson.toJson(arenaJson, writer);
            }

            player.sendMessage("Arena '" + arenaName + "' created successfully!");

        } catch (IOException e) {
            player.sendMessage("An error occurred while creating the arena.");
            e.printStackTrace();
        }
    }

    public JsonObject getJsonObjectByLoction(Location location){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", location.getWorld().getName());
        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());
        jsonObject.addProperty("yaw", location.getYaw());
        jsonObject.addProperty("pitch", location.getPitch());
        return jsonObject;
    }

}
