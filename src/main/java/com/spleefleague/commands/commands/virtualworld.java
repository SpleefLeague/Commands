/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.commands.commands;

import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.IntArg;
import com.spleefleague.annotations.LiteralArg;
import com.spleefleague.annotations.PlayerArg;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.virtualworld.Area;
import com.spleefleague.virtualworld.FakeWorldManager;
import com.spleefleague.virtualworld.VirtualWorld;
import com.spleefleague.virtualworld.api.FakeWorld;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class virtualworld extends BasicCommand {

    private final Map<String, FakeWorld> knownWorlds;
    private final FakeWorldManager fwm;

    public virtualworld(CorePlugin plugin, String name, String usage) {
        super(plugin, new virtualworldDispatcher(), name, usage);
        this.knownWorlds = new HashMap<>();
        this.fwm = VirtualWorld.getInstance().getFakeWorldManager();
    }
    
    public Map<String, FakeWorld> getKnownWorlds() {
        return knownWorlds;
    }

    @Endpoint(target = {com.spleefleague.annotations.CommandSource.PLAYER, com.spleefleague.annotations.CommandSource.CONSOLE, com.spleefleague.annotations.CommandSource.COMMAND_BLOCK})
    public void create(CommandSender s, @LiteralArg(value = "create", aliases = {"c"}) String l, @StringArg String name, @IntArg int x1, @IntArg int y1, @IntArg int z1, @IntArg int x2, @IntArg int y2, @IntArg int z2) {
        World world = SpleefLeague.DEFAULT_WORLD;
        Vector min = new Vector(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        Vector max = new Vector(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
        Area area = new Area(min, max);
        create(s, world, area, name);
    }

    public void create(CommandSender cs, World world, Area area, String name) {
        FakeWorld current = (FakeWorld) this.knownWorlds.get(name);
        if (current != null) {
            this.fwm.removeWorld(current);
            success(cs, Theme.INFO.buildTheme(false) + "Removing old world...");
        }
        current = this.fwm.createWorld(world, area);
        this.knownWorlds.put(name, current);
        success(cs, "World has been created");
    }

    @Endpoint(target = {com.spleefleague.annotations.CommandSource.PLAYER, com.spleefleague.annotations.CommandSource.CONSOLE, com.spleefleague.annotations.CommandSource.COMMAND_BLOCK})
    public void delete(CommandSender s, @LiteralArg(value = "delete", aliases = {"d"}) String l, @StringArg String name) {
        FakeWorld current = (FakeWorld) this.knownWorlds.remove(name);
        if (current != null) {
            this.fwm.removeWorld(current);
            success(s, "Removed world.");
        } else {
            error(s, name + " does not exist");
        }
    }

    @Endpoint(target = {com.spleefleague.annotations.CommandSource.PLAYER, com.spleefleague.annotations.CommandSource.CONSOLE, com.spleefleague.annotations.CommandSource.COMMAND_BLOCK})
    public void addPlayerDefaultPriority(CommandSender s, @LiteralArg(value = "add", aliases = {"a"}) String l, @StringArg String name, @PlayerArg Player[] players) {
        addPlayer(s, l, name, 0, players);
    }

    @Endpoint(target = {com.spleefleague.annotations.CommandSource.PLAYER, com.spleefleague.annotations.CommandSource.CONSOLE, com.spleefleague.annotations.CommandSource.COMMAND_BLOCK})
    public void addPlayer(CommandSender s, @LiteralArg(value = "add", aliases = {"a"}) String l, @StringArg String name, @IntArg int priority, @PlayerArg Player[] players) {
        FakeWorld world = (FakeWorld) this.knownWorlds.get(name);
        if (world == null) {
            error(s, name + " does not exist");
            return;
        }
        boolean senderIncluded = false;
        for (Player player : players) {
            senderIncluded = (senderIncluded) || (player == s);
            this.fwm.addWorld(player.getUniqueId(), world, priority);
        }
        if (!senderIncluded) {
            success(s, "The players have been added");
        }
    }

    @Endpoint(target = {com.spleefleague.annotations.CommandSource.PLAYER, com.spleefleague.annotations.CommandSource.CONSOLE, com.spleefleague.annotations.CommandSource.COMMAND_BLOCK})
    public void removePlayer(CommandSender s, @LiteralArg(value = "remove", aliases = {"r"}) String l, @StringArg String name, @PlayerArg Player[] players) {
        FakeWorld world = (FakeWorld) this.knownWorlds.get(name);
        if (world == null) {
            error(s, name + " does not exist");
            return;
        }
        boolean senderIncluded = false;
        for (Player player : players) {
            senderIncluded = (senderIncluded) || (player == s);
            this.fwm.removeWorld(player.getUniqueId(), world);
            success(player, "You have been removed from " + name);
        }
        if (!senderIncluded) {
            success(s, "The players have been removed");
        }
    }
}
