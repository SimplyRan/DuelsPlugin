package me.simplyran.duelsPlugin.managers;

import org.bukkit.Location;

public class ArenaManager {

    private final Location[] spawnLocation;
    private boolean inUse;
    private final String name;

    public ArenaManager(String name, Location[] spawnLocation){
        this.name = name;
        this.spawnLocation = spawnLocation;
        this.inUse = false;
    }

    public Location[] getSpawnLocation() {
        return spawnLocation;
    }
    public String getName(){
        return name;
    }
}
