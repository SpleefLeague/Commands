/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.commands.commands;

import static com.spleefleague.annotations.CommandSource.PLAYER;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.SLPlayerArg;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.PlayerState;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.ServerType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 *
 * @author Jonas
 */
public class tp extends BasicCommand {

    public tp(CorePlugin plugin, String name, String usage) {
        super(plugin, new tpDispatcher(), name, usage, Rank.MODERATOR, Rank.ORGANIZER);
        setAdditionalRanksDependingOnServerType(ServerType.BUILD, Rank.BUILDER);
    }

    @Endpoint(target = {PLAYER})
    public void teleport(SLPlayer slp, @SLPlayerArg SLPlayer target) {
        if (slp.getRank() == Rank.ORGANIZER) {
            if (target.getState() != PlayerState.INGAME) {
                error(slp, "You may only to teleport to ingame players!");
                return;
            }
        }
        slp.teleport(target, TeleportCause.COMMAND);
    }
    
}