package me.simplyran.duelsPlugin.completers;

import me.simplyran.duelsPlugin.managers.KitManager;
import me.simplyran.duelsPlugin.managers.MainManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DuelCompleter implements TabCompleter {

    private final MainManager mainManager;
    public DuelCompleter(MainManager mainManager){
        this.mainManager = mainManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1){
            List<String> list = new ArrayList<>();
            list.add("send");
            list.add("accept");
            return list;
        }
        if (strings.length == 2){
            List<String> playersName = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()){
                playersName.add(p.getName());
            }
            return playersName;
        }

        if (strings.length == 3 && strings[0].equalsIgnoreCase("send")){
            return new ArrayList<>(mainManager.getKits().keySet());
        }
        return List.of();
    }
}
