package me.simplyran.duelsPlugin;

import com.google.gson.Gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.simplyran.duelsPlugin.managers.ArenaManager;
import me.simplyran.duelsPlugin.managers.MainManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class DuelsPlugin extends JavaPlugin {

    private MainManager mainManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Plugin startup logic
        mainManager = new MainManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public MainManager getMainManager(){
        return mainManager;
    }


}
