package com.spleefleague.commands.commands;

import static com.spleefleague.annotations.CommandSource.PLAYER;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.IntArg;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.plugin.CorePlugin;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class rules extends BasicCommand {

    private final String url = Settings.getString("rules_thread_url").get();
    private final int rulesPerPage = 6;

    public rules(CorePlugin plugin, String name, String usage) {
        super(plugin, new rulesDispatcher(), name, usage);
    }

        @Endpoint(target = {PLAYER})
    public void rules(Player p) {
        rules(p, 1);
    }
    
    @Endpoint(target = {PLAYER})
    public void rules(Player p, @IntArg(min = 1) int page) {
        page--;
        int low = (page * rulesPerPage);
        int high = low + rulesPerPage;
        if (low < 0) {
            low = 0;
            high = low + rulesPerPage;
        }
        while (low >= rules.length) {
            low -= rulesPerPage;
        }
        if (high >= rules.length) {
            high = rules.length;
        }
        page = low / rulesPerPage;
        int totalPages = rules.length / rulesPerPage;
        page++;
        totalPages++;
        if(page > totalPages) {
            error(p, "There are only " + totalPages + " pages.");
            return;
        }
        p.sendMessage(ChatColor.GRAY + "[==== " + ChatColor.RED + "Server Rules [page " + page + "/" + totalPages + "]" + ChatColor.GRAY + " ====]");
        for (int i = low; i < high; i++) {
            p.sendMessage(ChatColor.GOLD + "#" + (i + 1) + ": " + ChatColor.YELLOW + rules[i]);
        }
        String jsonMsg = "[\"\",{\"text\":\"For a complete list of all the rules \",\"color\":\"yellow\"},{\"text\":\"click here\",\"color\":\"aqua\",\"underlined\":true,\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}}]";
        tellraw(p, jsonMsg);
    }

    private void tellraw(Player p, String txt) {
        IChatBaseComponent localIChatBaseComponent = IChatBaseComponent.ChatSerializer.a(txt);
        ((CraftPlayer) p).getHandle().sendMessage(localIChatBaseComponent);
    }

    // shortened rules
    private final String[] rules = new String[]{
        "No spam or full caps statements",
        "No links to illegal or lewd websites of any kind",
        "No hacks or mods which give you an unfair advantage",
        "No inappropriate language and/or avoiding the language filter",
        "No hard trolling",
        "No excessive advertisements",
        "No inappropriate skins or names",
        "No alt accounts to avoid bans",
        "No more than one account per player in a tournament",
        "No cheating in any form",
        "No islanding/camping in SuperSpleef™",
        "Do not share coordinates in the public chat",
        "No hard glitching is allowed",
        "Be respectful"
    };
}
