/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.commands.commands;

import static com.spleefleague.annotations.CommandSource.PLAYER;
import com.spleefleague.annotations.Dispatcher;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.SLPlayerArg;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;

/**
 * This is dumb
 * 
 * @author NickM13
 */
public class marry extends BasicCommand {
    
    private Map<UUID, UUID> proposals = new HashMap<>();
    
    public marry(CorePlugin plugin, String name, String usage) {
        super(plugin, new marryDispatcher(), name, usage);
    }
    
    @Endpoint(target = {PLAYER})
    public void marry(SLPlayer slp, @SLPlayerArg SLPlayer target) {
        if (slp.getUniqueId().equals(target.getUniqueId())) {
            if (slp.getMarried() != null) {
                SLPlayer married = SpleefLeague.getInstance().getPlayerManager().get(slp.getMarried());
                error(slp, "You're married to " + married.getDisplayName());
            } else {
                error(slp, "You can't marry yourself!");
            }
        } else if (slp.getMarried() != null) {
            if (slp.getMarried().equals(target.getUniqueId())) {
                slp.setMarried(null);
                target.setMarried(null);
                ChatManager.sendMessage(slp.getDisplayName() + ChatColor.RED + " divorced " + target.getDisplayName() + ChatColor.RED + "! :(", ChatChannel.GLOBAL);
            } else {
                SLPlayer married = SpleefLeague.getInstance().getPlayerManager().get(slp.getMarried());
                error(slp, "You're already married to " + married.getDisplayName());
            }
        } else if (target.getMarried() != null) {
            SLPlayer married = SpleefLeague.getInstance().getPlayerManager().get(slp.getMarried());
            error(slp, "They're already married to " + married.getDisplayName());
        } else {
            if (proposals.containsKey(target.getUniqueId()) && proposals.get(target.getUniqueId()).equals(slp.getUniqueId())) {
                slp.setMarried(target.getUniqueId());
                target.setMarried(slp.getUniqueId());
                ChatManager.sendMessage(slp.getDisplayName() + ChatColor.GREEN + " said Yes to " + target.getDisplayName() + ChatColor.GREEN + "!!!", ChatChannel.GLOBAL);
            } else if (!proposals.containsKey(slp.getUniqueId()) || proposals.get(slp.getUniqueId()) != target.getUniqueId()) {
                proposals.put(slp.getUniqueId(), target.getUniqueId());
                ChatManager.sendMessage(slp.getDisplayName() + ChatColor.GREEN + " proposed to " + target.getDisplayName() + ChatColor.GREEN + "!", ChatChannel.GLOBAL);
                ChatManager.sendMessagePlayer(target, ChatColor.GREEN + "Type /marry " + slp.getName() + " to say Yes!");
            }
        }
    }
    
}
