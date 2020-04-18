package com.spleefleague.commands.commands;

import static com.spleefleague.annotations.CommandSource.PLAYER;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.LiteralArg;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class fakeentity extends BasicCommand {

    public fakeentity(CorePlugin plugin, String name, String usage) {
        super(plugin, new fakeentityDispatcher(), name, usage, Rank.DEVELOPER);
    }

    @Endpoint(target = {PLAYER})
    public void pending(Player p) {
        /*
        if (FakeCreaturesWorker.isPending(p) || FakeCreaturesWorker.isWorking(p)) {
            FakeCreaturesWorker.removeWorking(p);
            UtilChat.s(Theme.SUCCESS, p, "You are no longer working with fake creatures.");
        } else {
            FakeCreaturesWorker.addPending(p);
            UtilChat.s(Theme.SUCCESS, p, "Now you're working with fake creatures. Right click a fake entity to begin.");
        }
        */
    }

    @Endpoint(target = {PLAYER})
    public void spawn(Player p, @LiteralArg(value = "spawn") String l, @StringArg String className, @StringArg String suid, @StringArg String name) {
        /*
        UUID uuid;
        switch (suid) {
            case "null":
                uuid = null;
                break;
            case "random":
                uuid = UUID.randomUUID();
                break;
            default:
                try {
                    uuid = UUID.fromString(suid);
                } catch (Exception ex) {
                    UtilChat.s(Theme.ERROR, p, "Wrong uuid!");
                    return;
                }
                break;
        }
        if (name.length() > 16) {
            UtilChat.s(Theme.ERROR, p, "This name is too far long.");
            return;
        }
        try {
            Class clazz = Class.forName(className);
            FakeCreature creature;
            if (FakeNpc.class.isAssignableFrom(clazz)) {
                creature = (FakeCreature) clazz.getDeclaredConstructor(UUID.class, String.class, Location.class)
                        .newInstance(uuid, name, p.getLocation());
            } else {
                creature = (FakeCreature) clazz.getDeclaredConstructor(Location.class).newInstance(p.getLocation());
                try {
                    Method m = clazz.getDeclaredMethod("setName", String.class);
                    m.setAccessible(true);
                    m.invoke(creature, name);
                    m.setAccessible(false);
                } catch (Exception ex) {
                } //This creature can't have custom name.
            }
            creature.spawn();
            UtilChat.s(Theme.SUCCESS, p, "Fake creature has been spawned.");
        } catch (Exception ex) {
            UtilChat.s(Theme.ERROR, p, "Unknown fake creature type class.");
        }
        */
    }
}
