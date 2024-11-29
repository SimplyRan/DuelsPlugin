package me.simplyran.duelsPlugin.managers;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.simplyran.duelsPlugin.DuelsPlugin;
import me.simplyran.duelsPlugin.commands.CreateNewArena;
import me.simplyran.duelsPlugin.commands.CreateNewKit;
import me.simplyran.duelsPlugin.commands.DuelCommand;
import me.simplyran.duelsPlugin.completers.ArenaCompleter;
import me.simplyran.duelsPlugin.completers.DuelCompleter;
import me.simplyran.duelsPlugin.listeners.PlayerDeathInDuel;
import me.simplyran.duelsPlugin.listeners.PlayerHitToDeath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MainManager {

    private final DuelsPlugin plugin;
    private Queue<ArenaManager> emptyArenas;
    private Map<String , KitManager> kits;
    private Map<String, ArenaManager> arenas;
    private HashMap<Set<UUID> , DuelGameManager> activeGames;


    public MainManager(DuelsPlugin plugin){
        activeGames = new HashMap<>();
        this.plugin = plugin;
        loadData();
        loadCommands();
        loadListeners();
        loadComplaters();
    }

    public void loadCommands(){
        plugin.getCommand("arena").setExecutor(new CreateNewArena(plugin));
        plugin.getCommand("duelkit").setExecutor(new CreateNewKit(plugin));
        plugin.getCommand("duel").setExecutor(new DuelCommand(this));

    }
    public void loadListeners(){
        plugin.getServer().getPluginManager().registerEvents(new PlayerDeathInDuel(this) , plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerHitToDeath(this) , plugin);
    }
    public void loadComplaters(){
        plugin.getCommand("arena").setTabCompleter(new ArenaCompleter(this));
        plugin.getCommand("duel").setTabCompleter(new DuelCompleter(this));
    }

    public Queue<ArenaManager> getEmptyArenas() {
        return emptyArenas;
    }
    public Map<String ,ArenaManager> getArenas(){
        return arenas;
    }

    public void loadData() {
        emptyArenas = new LinkedList<>();
        kits = new HashMap<>();
        arenas = new HashMap<>();
        File arenaFile = new File(plugin.getDataFolder() , "data/arenas.json");
        File kitsFile = new File(plugin.getDataFolder() , "data/kits.json");

        //Checking if files exists
        if (!arenaFile.exists()){
            plugin.saveResource("data/arenas.json" , true);
        }
        if (!kitsFile.exists()){
            plugin.saveResource("data/kits.json" , true);
        }

        //load arenas
        JsonObject arenaJson = getJsonObjectFromFile(arenaFile);
        for (String arenaName : arenaJson.keySet()){
            JsonObject spawnpoint1 = arenaJson.getAsJsonObject(arenaName).getAsJsonObject("spawnpoint1");
            JsonObject spawnpoint2 = arenaJson.getAsJsonObject(arenaName).getAsJsonObject("spawnpoint2");
            Location spawnpoint1Location = getSpawnLoction(spawnpoint1);
            Location spawnpoint2Location = getSpawnLoction(spawnpoint2);
            Location[] spawnpointArray = {spawnpoint1Location , spawnpoint2Location};
            ArenaManager arena = new ArenaManager(arenaName ,spawnpointArray);
            arenas.put(arena.getName() , arena);
            emptyArenas.add(arena);
        }


        //load kits
        JsonObject kitJson = getJsonObjectFromFile(kitsFile);
        for (String kitName : kitJson.keySet()) {
            ArrayList<ItemStack> itemsList = new ArrayList<>();
            ArrayList<ItemStack> armorList = new ArrayList<>();
            JsonArray armor = kitJson.getAsJsonObject(kitName).getAsJsonArray("armor");
            JsonArray contents = kitJson.getAsJsonObject(kitName).getAsJsonArray("contents");
            contents.forEach(item -> {
                JsonObject itemObj = item.getAsJsonObject();
                Material material = Material.getMaterial(itemObj.get("material").getAsString());
                int amount = itemObj.get("amount").getAsInt();
                ItemStack itemStack = new ItemStack(material , amount);
                itemsList.add(itemStack);
            });
            armor.forEach(armorPiece -> {
                JsonObject itemObj = armorPiece.getAsJsonObject();
                Material material = Material.getMaterial(itemObj.get("material").getAsString());
                int amount = itemObj.get("amount").getAsInt();
                ItemStack itemStack = new ItemStack(material , amount);
                armorList.add(itemStack);
            });
            KitManager kit = new KitManager(itemsList.toArray(new ItemStack[0]), kitName , armorList.toArray(new ItemStack[0]));
            kits.put(kitName , kit);
        }
    }

    public Location getSpawnLoction(JsonObject spawnpoint){
        String world = spawnpoint.get("world").getAsString();
        double x = spawnpoint.get("x").getAsDouble();
        double y = spawnpoint.get("y").getAsDouble();
        double z = spawnpoint.get("z").getAsDouble();
        float yaw = spawnpoint.get("yaw").getAsFloat();
        float pitch = spawnpoint.get("pitch").getAsFloat();
        return new Location(Bukkit.getWorld(world) , x , y ,z , yaw , pitch);
    }

    public JsonObject getJsonObjectFromFile(File file){
        try (FileReader reader = new FileReader(file)){
            char[] buffer = new char[(int) file.length()];
            reader.read(buffer);
            String fileContent = new String(buffer);
            return JsonParser.parseString(fileContent).getAsJsonObject();

        }catch (IOException e){
            System.out.println("Error in data files: " + e.getMessage());
        }
        return new JsonObject();
    }

    public DuelGameManager getGameByUUID(UUID uuid){
        for (Set<UUID> set : activeGames.keySet()){
            if (set.contains(uuid)){
                return activeGames.get(set);
            }
        }
        return null;
    }

    public Map<String , KitManager> getKits(){
        return this.kits;
    }

    public DuelsPlugin getPlugin(){
        return plugin;
    }
    public HashMap<Set<UUID> , DuelGameManager> getActiveGames(){
        return activeGames;
    }






}
