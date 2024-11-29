package me.simplyran.duelsPlugin.commands;

import com.google.gson.*;
import me.simplyran.duelsPlugin.DuelsPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class CreateNewKit implements CommandExecutor {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final DuelsPlugin plugin;
    public CreateNewKit(DuelsPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)){
            System.out.println("Player must use this command!");
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("duels.kit")){
            player.sendMessage("You don't have perms for this command");
        }


        if (strings.length < 1) {
            player.sendMessage("Usage: /kit <name>");
            return true;
        }
        createKit(player , strings[0]);
        plugin.getMainManager().loadData();
        return true;
    }

    private void createKit(Player player, String kitName) {
        File kitFile = new File(plugin.getDataFolder(), "data/kits.json");
        JsonObject kitJson;
        boolean isThereKit = false;

        try {
            if (kitFile.exists()) {
                try (FileReader reader = new FileReader(kitFile)) {
                    kitJson = JsonParser.parseReader(reader).getAsJsonObject();
                }
            } else {
                kitJson = new JsonObject();
            }

            if (kitJson.has(kitName)) {
                isThereKit = true;
            }


            JsonObject newKit = new JsonObject();

            JsonArray contents = new JsonArray();
            JsonArray armor = new JsonArray();
            Set<Material> armorSet = new HashSet<>();

            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    armorSet.add(item.getType());
                    JsonObject armorItem = new JsonObject();
                    armorItem.addProperty("material", item.getType().name());
                    armorItem.addProperty("amount", item.getAmount());
                    armor.add(armorItem);
                }
            }

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR && !armorSet.contains(item.getType())) {
                    JsonObject contentItem = new JsonObject();
                    contentItem.addProperty("material", item.getType().name());
                    contentItem.addProperty("amount", item.getAmount());
                    contents.add(contentItem);
                }
            }

            newKit.add("contents", contents);
            newKit.add("armor", armor);
            kitJson.add(kitName, newKit);


            try (FileWriter writer = new FileWriter(kitFile)) {
                gson.toJson(kitJson, writer);
            }
            if (!isThereKit)
                player.sendMessage("Kit '" + kitName + "' created successfully!");
            else player.sendMessage("Kit '" + kitName + "' changed successfully!");



        }catch (Exception e){

        }

    }


}
