/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.commands.commands;

import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.IntArg;
import com.spleefleague.annotations.LiteralArg;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.commands.Commands;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.virtualworld.FakeWorldManager;
import com.spleefleague.virtualworld.VirtualWorld;
import com.spleefleague.virtualworld.api.FakeBlock;
import com.spleefleague.virtualworld.api.FakeWorld;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class virtualblock extends BasicCommand {

    private Map<String, FakeWorld> knownWorlds;
    private final FakeWorldManager fwm;

    public virtualblock(CorePlugin plugin, String name, String usage) {
        super(plugin, new virtualblockDispatcher(), name, usage);
        this.fwm = VirtualWorld.getInstance().getFakeWorldManager();
    }

    @Endpoint(target = {com.spleefleague.annotations.CommandSource.PLAYER, com.spleefleague.annotations.CommandSource.CONSOLE, com.spleefleague.annotations.CommandSource.COMMAND_BLOCK})
    public void set(CommandSender s, @LiteralArg(value = "set", aliases = {"s"}) String l, @StringArg String name, @IntArg int x, @IntArg int y, @IntArg int z, @StringArg String type) {
        set(s, l, name, x, y, z, type, 0);
    }

    @Endpoint(target = {com.spleefleague.annotations.CommandSource.PLAYER, com.spleefleague.annotations.CommandSource.CONSOLE, com.spleefleague.annotations.CommandSource.COMMAND_BLOCK})
    public void set(CommandSender s, @LiteralArg(value = "set", aliases = {"s"}) String l, @StringArg String name, @IntArg int x, @IntArg int y, @IntArg int z, @StringArg String type, @IntArg int data) {
        Material m = Material.getMaterial(type);
        if ((m == null) || (!m.isBlock())) {
            error(s, type + " is not a valid block name");
            return;
        }
        set(s, name, x, y, z, m, (byte) data);
    }

    private void set(CommandSender s, String name, int x, int y, int z, Material m, byte data) {
        getKnownWorlds();
        FakeWorld world = (FakeWorld) this.knownWorlds.get(name);
        if (world == null) {
            error(s, name + " does not exist.");
            return;
        }
        FakeBlock fb = world.getBlockAt(x, y, z);
        if (fb == null) {
            error(s, "This block is not inside the world");
            return;
        }
        fb.setType(m);
        fb.setData(data);
    }
    
    private void getKnownWorlds() {
        if(knownWorlds == null) {
            virtualworld vw = ((virtualworld)Commands.getInstance().getCommandLoader().getCommand("virtualworld").getExecutor());
            knownWorlds = vw.getKnownWorlds();
        }
    }
}
