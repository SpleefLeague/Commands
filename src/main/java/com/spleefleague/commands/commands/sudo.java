/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.commands.commands;

import static com.spleefleague.annotations.CommandSource.PLAYER;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.SLPlayerArg;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class sudo extends BasicCommand {

    public sudo(CorePlugin plugin, String name, String usage) {
        super(plugin, new sudoDispatcher(), name, usage, Rank.DEVELOPER, Rank.BUILDER);
    }
    
    @Endpoint(target = {PLAYER})
    public void sudo(CommandSender cs, @SLPlayerArg SLPlayer target, @StringArg String[] msg) {
        System.out.println("Sudo command from " + cs.getName() + ", " + target.getName() + ", " + StringUtil.fromArgsArray(msg));
        Bukkit.dispatchCommand(target.getPlayer(), StringUtil.fromArgsArray(msg));
    }
    
}
