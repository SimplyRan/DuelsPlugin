package me.simplyran.duelsPlugin.completers;

import me.simplyran.duelsPlugin.managers.ArenaManager;
import me.simplyran.duelsPlugin.managers.MainManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArenaCompleter implements TabCompleter {
    private final MainManager mainManager;

    public ArenaCompleter(MainManager mainManager){
        this.mainManager = mainManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length == 1){
            List<String> list = new ArrayList<>();
            list.add("create");
            list.add("edit");
            return list;
        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("edit")){
            return new ArrayList<>(mainManager.getArenas().keySet());
        }
        if (strings.length == 3 && strings[0].equalsIgnoreCase("edit")){
            List<String> list = new ArrayList<>();
            list.add("spawnpoint1");
            list.add("spawnpoint2");
            return list;
        }

        return List.of();
    }
}
