/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.commands.commands;

import static com.spleefleague.annotations.CommandSource.PLAYER;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;

/**
 * @author NickM13
 */
public class sit extends BasicCommand {

    public sit(CorePlugin plugin, String name, String usage) {
        super(plugin, new sitDispatcher(), name, usage, Rank.MODERATOR, Rank.BUILDER);
    }

    @Endpoint(target = {PLAYER})
    public void sit(SLPlayer slp) {
        Arrow arrow = slp.getPlayer().getWorld().spawn(slp.getPlayer().getEyeLocation(), Arrow.class, (entity) -> {
            entity.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            entity.setVelocity(slp.getPlayer().getLocation().getDirection().multiply(2));
            entity.addPassenger(slp.getPlayer());
        });
    }
}
