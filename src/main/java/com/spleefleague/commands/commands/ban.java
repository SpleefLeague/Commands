/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.commands.commands;

import static com.spleefleague.annotations.CommandSource.CONSOLE;
import static com.spleefleague.annotations.CommandSource.PLAYER;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.SLPlayerArg;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.commands.Commands;
import java.util.UUID;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.infraction.Infraction;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.StringUtil;
import com.spleefleague.core.infraction.InfractionType;
import com.spleefleague.entitybuilder.EntityBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Manuel
 */
public class ban extends BasicCommand {

    public ban(CorePlugin plugin, String name, String usage) {
        super(plugin, new banDispatcher(), name, usage, Rank.MODERATOR);
    }
    
    @Endpoint(target = {PLAYER})
    public void ban(SLPlayer cs, @SLPlayerArg(exact = true, offline = true) SLPlayer target, @StringArg String[] reason) {
        String banMessage = StringUtil.fromArgsArray(reason);
        UUID senderId = cs.getUniqueId();
        performBan(target, senderId, cs.getName(), banMessage);
        if(target.isOnline()) {
            target.kickPlayer("You have been banned for: " + banMessage);
        }
        if(cs != target) {
            success(cs, "The player has been banned!");
        }
    }
    
    @Endpoint(target = {CONSOLE})
    public void ban(CommandSender cs, @SLPlayerArg(exact = true, offline = true) SLPlayer target, @StringArg String[] reason) {
        String banMessage = StringUtil.fromArgsArray(reason);
        UUID senderId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        performBan(target, senderId, cs.getName(), banMessage);
        if(target.isOnline()) {
            target.kickPlayer("You have been banned for: " + banMessage);
        }
        success(cs, "The player has been banned!");
    }
    
    private void performBan(SLPlayer target, UUID sender, String senderName, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(Commands.getInstance(), () -> {
            Infraction ban = new Infraction(target.getUniqueId(), sender, InfractionType.BAN, System.currentTimeMillis(), -1, message);
            SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").deleteMany(new Document("uuid", target.getUniqueId().toString()));
            EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"), false);
            EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions"), false);
        });
        ChatManager
                .sendMessage(new ComponentBuilder(SpleefLeague.getInstance().getChatPrefix() + " ")
                    .append(target.getName() + " has been banned by " + senderName + "!")
                    .color(ChatColor.GRAY)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + message)
                        .color(ChatColor.GRAY).create())
                    ).create(), ChatChannel.STAFF_NOTIFICATIONS
                );
    }
}
