/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.commands.command;

import com.google.common.collect.Sets;
import com.spleefleague.annotations.CommandSource;
import com.spleefleague.annotations.DispatchResult;
import com.spleefleague.annotations.DispatchableCommand;
import com.spleefleague.annotations.Dispatcher;
import java.util.regex.Pattern;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.ServerType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public abstract class BasicCommand extends DispatchableCommand implements CommandExecutor {

    protected String prefix;
    protected CorePlugin plugin;
    protected String name;
    protected Rank requiredRank;
    protected final Set<Rank> additionalRanks;
    protected final Map<ServerType, Set<Rank>> additionalRanksPerServerTypes = new HashMap<>();
    private String[] usages = null;
    public static final String NO_COMMAND_PERMISSION_MESSAGE = "You don't have permission to use this command!";
    public static final String PLAYERDATA_ERROR_MESSAGE = "Your player data hasn't yet been loaded. Please try again.";
    public static final String NO_PLAYER_INSTANCE = Theme.WARNING.buildTheme(false) + "This command can only be run by an instance of a player.";

    public BasicCommand(CorePlugin plugin, Dispatcher dispatcher, String name, String usage) {
        this(plugin, dispatcher, name, usage, Rank.DEFAULT);
    }
    
    public BasicCommand(CorePlugin plugin, String prefix, Dispatcher dispatcher, String name, String usage) {
        this(plugin, prefix, dispatcher, name, usage, Rank.DEFAULT);
    }
    
    public BasicCommand(CorePlugin plugin, Dispatcher dispatcher, String name, String usage, Rank requiredRank, Rank... additionalRanks) {
        this(plugin, plugin.getChatPrefix(), dispatcher, name, usage, requiredRank, additionalRanks);
    }

    public BasicCommand(CorePlugin plugin, String prefix, Dispatcher dispatcher, String name, String usage, Rank requiredRank, Rank... additionalRanks) {
        super(dispatcher);
        this.prefix = prefix;
        this.plugin = plugin;
        this.name = name;
        this.requiredRank = requiredRank;
        this.additionalRanks = Sets.newHashSet(additionalRanks);
        usage = usage.replaceAll(Pattern.quote("<command>"), name);
        this.usages = StringUtils.split(usage, "\n");
        plugin.getCommand(name).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        // Hacky way around not having CommandHook anymore
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("@p") || args[i].equals("@s")) {
                if (sender instanceof Player) {
                    args[i] = ((Player) sender).getName();
                } else if (sender instanceof BlockCommandSender) {
                    Player closest = null;
                    double closestDist = 0;
                    Location blockLoc = ((BlockCommandSender) sender).getBlock().getLocation();
                    for (Player player : ((BlockCommandSender) sender).getBlock().getWorld().getPlayers()) {
                        double dist = player.getLocation().distance(blockLoc);
                        if (closest == null || dist < closestDist) {
                            closest = player;
                            closestDist = dist;
                        }
                    }
                    if (closest != null) {
                        args[i] = closest.getName();
                    } else {
                        return false;
                    }
                }
            }
        }
        try {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(p);
                if (slp != null) {
                    boolean hasPermissions = false;
                    Rank rank = slp.getRank();
                    if(rank != null) {
                        hasPermissions = rank.hasPermission(requiredRank);
                        hasPermissions |= additionalRanks.contains(rank);
                        Set<Rank> additionalRanksForCurrentServerType = additionalRanksPerServerTypes.get(SpleefLeague.getInstance().getServerType());
                        if(additionalRanksForCurrentServerType != null)
                            hasPermissions |= additionalRanksForCurrentServerType.contains(rank);
                    }
                    if (hasPermissions) {
                        performRun(CommandSource.PLAYER, slp, args);
                    } else {
                        error(sender, NO_COMMAND_PERMISSION_MESSAGE);
                    }
                } else {
                    error(sender, PLAYERDATA_ERROR_MESSAGE);
                }
            } else if (sender instanceof ConsoleCommandSender) {
                performRun(CommandSource.CONSOLE, sender, args);
            } else if (sender instanceof BlockCommandSender) {
                performRun(CommandSource.COMMAND_BLOCK, sender, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public String getName() {
        return name;
    }
    
    protected void setAdditionalRanksDependingOnServerType(ServerType type, Rank... ranks) {
        additionalRanksPerServerTypes.put(type, Sets.newHashSet(ranks));
    }

    protected void error(CommandSender cs, String message) {
        cs.sendMessage(prefix + " " + Theme.ERROR.buildTheme(false) + message);
    }

    protected void success(CommandSender cs, String message) {
        cs.sendMessage(prefix + " " + Theme.SUCCESS.buildTheme(false) + message);
    }

    protected void sendUsage(CommandSender cs) {
        cs.sendMessage(prefix + " " + Theme.ERROR.buildTheme(false) + "Correct Usage: ");
        for (String m : usages) {
            cs.sendMessage(prefix + " " + Theme.INCOGNITO.buildTheme(false) + m);
        }
    }
    
    private void performRun(CommandSource source, CommandSender cs, String[] args) {
        DispatchResult result = super.run(cs, source, args);
        if(null != result.getType()) switch (result.getType()) {
            case NO_ROUTE:
                cs.sendMessage(prefix + " " + NO_PLAYER_INSTANCE);
                break;
            case NO_VALID_ROUTE:
                sendUsage(cs);
                break;
            case OTHER:
                error(cs, result.getMessage().orElse("An error has occured."));
                break;
        }
    }
}
