package com.spleefleague.commands.commands;

import com.mongodb.client.MongoCollection;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.IntArg;
import com.spleefleague.annotations.SLPlayerArg;
import com.spleefleague.annotations.StringArg;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.player.TemporaryRank;
import com.spleefleague.core.utils.DatabaseConnection;
import com.spleefleague.entitybuilder.EntityBuilder;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Jonas
 */
public class settemprank extends BasicCommand {

    public settemprank(CorePlugin plugin, String name, String usage) {
        super(plugin, new settemprankDispatcher(), name, usage, Rank.DEVELOPER);
    }
    
    @Endpoint(priority = 1)
    public void setrankOnline(CommandSender cs, @SLPlayerArg(exact = true) SLPlayer target, @StringArg String rankName, @IntArg(min = 1) int hours) {
        Rank rank = Rank.valueOf(rankName.toUpperCase());
        if (rank == null) {
            error(cs, "The rank " + rankName + " does not exist!");
            return;
        }
        long endingTime = System.currentTimeMillis() + hours * 60 * 60 * 1000;
        TemporaryRank tempRank = new TemporaryRank(rank, endingTime);
        target.setTemporaryRank(tempRank);
        success(cs, "Temporary rank has been set.");
    }

    @Endpoint(priority = 0)
    public void setrankOffline(CommandSender cs, @SLPlayerArg(offline = true, exact = true) SLPlayer target, @StringArg String rankName, @IntArg(min = 1) int hours) {
        Rank rank = Rank.valueOf(rankName.toUpperCase());
        if (rank == null) {
            error(cs, "The rank " + rankName + " does not exist!");
            return;
        }
        long endingTime = System.currentTimeMillis() + hours * 60 * 60 * 1000;
        TemporaryRank tempRank = new TemporaryRank(rank, endingTime);
        setRankOffline(target, tempRank);
        success(cs, "Temporary rank has been set.");
    }

    private void setRankOffline(SLPlayer player, TemporaryRank tempRank) {
        MongoCollection<Document> collection = SpleefLeague.getInstance().getPluginDB().getCollection("Players");
        Document index = new Document("uuid", player.getUniqueId().toString());
        Document document = EntityBuilder.serialize(tempRank).get("$set", Document.class);
        DatabaseConnection.updateFields(collection,
                index,
                Pair.<String, Object>of("temporaryRank", document)
        );
    }
}
