package com.spleefleague.commands.commands;

import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Josh on 06/08/2016.
 */
public class skull extends BasicCommand {

    public skull(CorePlugin plugin, String name, String usage) {
        super(plugin, new skullDispatcher(), name, usage, Rank.SENIOR_MODERATOR);
    }

    @Endpoint
    public void skull(Player p, @StringArg String name) {
        ItemStack give = new ItemStack(Material.PLAYER_HEAD);
        give.setDurability((short) 3);
        SkullMeta skullMeta = (SkullMeta) give.getItemMeta();
        skullMeta.setOwner(name);
        give.setItemMeta(skullMeta);
        p.getInventory().addItem(give);
        success(p, "You have been given " + name + "'s head!");
    }
}
