package com.spleefleague.commands.commands;

import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class sping extends BasicCommand {

    public sping(CorePlugin plugin, String name, String usage) {
        super(plugin, new spingDispatcher(), name, usage);
    }

    @Endpoint
    public void spingAll(Player p) {
        Map<String, Integer> pings = new LinkedHashMap<>();
        for (SLPlayer slpl : SpleefLeague.getInstance().getPlayerManager().getAll()) {
            pings.put(slpl.getRank().getColor() + slpl.getName(), slpl.getPing());
        }
        pings = sortByValue(pings);
        p.sendMessage(ChatColor.DARK_AQUA + "[====== " + ChatColor.GOLD + "Everyone's Pings" + ChatColor.DARK_AQUA + " ======]");
        for (Map.Entry<String, Integer> pv : pings.entrySet()) {
            p.sendMessage(formatPing(pv.getValue()) + ChatColor.GRAY + " >> " + pv.getKey());
        }
    }

    @Endpoint
    public void sping(Player p, @StringArg String[] args) {
        Set<String> names = new HashSet<>();
        names.addAll(Arrays.asList(args));
        Map<String, Integer> pings = new LinkedHashMap<>();
        for (SLPlayer slpl : SpleefLeague.getInstance().getPlayerManager().getAll()) {
            if (!names.contains(slpl.getName())) {
                continue;
            }
            pings.put(slpl.getRank().getColor() + slpl.getName(), slpl.getPing());
        }
        pings = sortByValue(pings);
        p.sendMessage(ChatColor.DARK_AQUA + "[====== " + ChatColor.GOLD + "Everyone's Pings" + ChatColor.DARK_AQUA + " ======]");
        for (Map.Entry<String, Integer> pv : pings.entrySet()) {
            p.sendMessage(formatPing(pv.getValue()) + ChatColor.GRAY + " >> " + pv.getKey());
        }
    }
    
    private String formatPing(int ping) {
        ChatColor c;
        if (ping < 30) {
            c = ChatColor.DARK_GREEN;
        } else if (ping < 60) {
            c = ChatColor.GREEN;
        } else if (ping < 120) {
            c = ChatColor.YELLOW;
        } else if (ping < 250) {
            c = ChatColor.GOLD;
        } else if (ping < 500) {
            c = ChatColor.RED;
        } else {
            c = ChatColor.DARK_RED;
        }
        if(ping != 1337) {
            return c + Integer.toString(ping);
        }
        else { 
            return "" + ChatColor.GREEN + 1 + ChatColor.RED + 3 + ChatColor.YELLOW + 3 + ChatColor.BLUE + 7;
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (Map.Entry<K, V> o1, Map.Entry<K, V> o2) -> (o1.getValue()).compareTo(o2.getValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
