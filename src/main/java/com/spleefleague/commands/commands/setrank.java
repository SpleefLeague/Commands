/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.commands.commands;

import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.LiteralArg;
import com.spleefleague.annotations.SLPlayerArg;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.DatabaseConnection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Jonas
 */
public class setrank extends BasicCommand {

    public setrank(CorePlugin plugin, String name, String usage) {
        super(plugin, new setrankDispatcher(), name, usage, Rank.DEVELOPER);
    }

    
    @Endpoint(priority = 0)
    public void setrankOffline(CommandSender cs, @SLPlayerArg(exact = true, offline = true) SLPlayer target, @StringArg String rankName) {
        Rank rank = Rank.valueOf(rankName.toUpperCase());
        if(rank == null) {
            error(cs, "The rank " + rankName + " does not exist!");
            return;
        }
        setRankOffline(target, rank);
        success(cs, "Rank has been set.");
    }
    
    @Endpoint
    public void setrankOnlineForceOverride(CommandSender cs, @LiteralArg("force") String force, @SLPlayerArg(exact = true) SLPlayer target, @StringArg String rankName) {
        Rank rank = Rank.valueOf(rankName.toUpperCase());
        if(rank == null) {
            error(cs, "The rank " + rankName + " does not exist!");
            return;
        }
        target.setRank(rank);
        target.setTemporaryRank(null);
        target.getTemporaryRankList().clear();
    }
    
    @Endpoint(priority = 1)
    public void setrankOnlineUnforced(CommandSender cs, @SLPlayerArg(exact = true) SLPlayer target, @StringArg String rankName) {
        Rank rank = Rank.valueOf(rankName.toUpperCase());
        
        if(rank == null) {
            error(cs, "The rank " + rankName + " does not exist!");
            return;
        }
        boolean tempRanksSet = !target.getTemporaryRankList().isEmpty() || target.getBaseRank() != target.getRank();
        target.setRank(rank);
        if(tempRanksSet) {
            BaseComponent[] askForce = new ComponentBuilder(SpleefLeague.getInstance().getChatPrefix())
                    .append(" The player currently has active temporary ranks that may interfere with this command. To override all temporary ranks, click").color(ChatColor.YELLOW.asBungee())
                    .append(" [").color(ChatColor.GRAY.asBungee()).append("here").color(ChatColor.DARK_GREEN.asBungee()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setrank force " + target.getName() + " " + rankName)).append("]").color(ChatColor.GRAY.asBungee())
                    .create();
            cs.spigot().sendMessage(askForce);
        }
        else {
            success(cs, "Rank has been set.");
        }
    }

    private void setRankOffline(SLPlayer player, Rank rank) {
       DatabaseConnection.updateFields(SpleefLeague.getInstance().getPluginDB().getCollection("Players"),
            new Document("uuid", player.getUniqueId().toString()),
            Pair.<String, Object>of("rank", rank.getName())
        );
    }
}
