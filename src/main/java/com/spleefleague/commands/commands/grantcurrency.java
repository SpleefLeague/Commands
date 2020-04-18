package com.spleefleague.commands.commands;

import com.mongodb.client.MongoCollection;
import static com.spleefleague.annotations.CommandSource.COMMAND_BLOCK;
import static com.spleefleague.annotations.CommandSource.CONSOLE;
import static com.spleefleague.annotations.CommandSource.PLAYER;
import com.spleefleague.annotations.Endpoint;
import com.spleefleague.annotations.IntArg;
import com.spleefleague.annotations.LiteralArg;
import com.spleefleague.annotations.SLPlayerArg;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.DatabaseConnection;
import com.spleefleague.core.utils.UtilChat;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class grantcurrency extends BasicCommand {

    public grantcurrency(CorePlugin plugin, String name, String usage) {
        super(plugin, new grantcurrencyDispatcher(), name, usage, Rank.DEVELOPER);
    }

    @Endpoint(target = {PLAYER, CONSOLE, COMMAND_BLOCK}, priority = 1)
    public void grantCoinsOnline(CommandSender cs, @SLPlayerArg SLPlayer target, @LiteralArg(value = "coin", aliases = {"coins"}) String l, @IntArg(min = 1) int amount) {
        target.changeCoins(amount);
        UtilChat.s(Theme.SUCCESS, cs, "You've just granted &6%d coins &ato &e%s&a.", amount, target.getName());
    }

    @Endpoint(target = {PLAYER, CONSOLE, COMMAND_BLOCK}, priority = 1)
    public void grantPremiumCoinsOnline(CommandSender cs, @SLPlayerArg SLPlayer target, @LiteralArg(value = "credit", aliases = {"credits", "premiumcredit", "premiumcredits"}) String l, @IntArg(min = 1) int amount) {
        target.changePremiumCredits(amount);
        UtilChat.s(Theme.SUCCESS, cs, "Now this player has exactly &b%d premium credits&a.", target.getPremiumCredits());
        UtilChat.s(Theme.SUCCESS, cs, "You've just granted &b%d premium credits &ato &e%s&a.", amount, target.getName());
    }

    @Endpoint(target = {PLAYER, CONSOLE, COMMAND_BLOCK}, priority = 1)
    public void removeCoinsOnline(CommandSender cs, @SLPlayerArg SLPlayer target, @LiteralArg(value = "coin", aliases = {"coins"}) String l, @IntArg(max = -1) int amount) {
        target.changeCoins(amount);
        UtilChat.s(Theme.SUCCESS, cs, "You've just took &6%d coins &afrom &e%s&a.", -amount, target.getName());
    }

    @Endpoint(target = {PLAYER, CONSOLE, COMMAND_BLOCK}, priority = 1)
    public void removePremiumCoinsOnline(CommandSender cs, @SLPlayerArg SLPlayer target, @LiteralArg(value = "credit", aliases = {"credits", "premiumcredit", "premiumcredits"}) String l, @IntArg(max = -1) int amount) {
        target.changePremiumCredits(amount);
        UtilChat.s(Theme.SUCCESS, cs, "Now this player has exactly &b%d premium credits&a.", target.getPremiumCredits());
        UtilChat.s(Theme.SUCCESS, cs, "You've just took &b%d premium credits &afrom &e%s&a.", -amount, target.getName());
    }

    @Endpoint(target = {PLAYER, CONSOLE, COMMAND_BLOCK})
    public void grantCoinsOffline(CommandSender cs, @SLPlayerArg(offline = true) SLPlayer target, @LiteralArg(value = "coin", aliases = {"coins"}) String l, @IntArg(min = 1) int amount) {
        updateCurrencyInDatabase(target.getUniqueId(), "coins", target.getCoins() + amount);
        UtilChat.s(Theme.SUCCESS, cs, "You've just granted &6%d coins &ato &e%s&a.", amount, target.getName());
    }

    @Endpoint(target = {PLAYER, CONSOLE, COMMAND_BLOCK})
    public void grantPremiumCoinsOffline(CommandSender cs, @SLPlayerArg(offline = true) SLPlayer target, @LiteralArg(value = "credit", aliases = {"credits", "premiumcredit", "premiumcredits"}) String l, @IntArg(min = 1) int amount) {
        updateCurrencyInDatabase(target.getUniqueId(), "premiumCredits", target.getCoins() + amount);
        UtilChat.s(Theme.SUCCESS, cs, "Now this player has exactly &b%d premium credits&a.", target.getPremiumCredits() + amount);
        UtilChat.s(Theme.SUCCESS, cs, "You've just granted &b%d premium credits &ato &e%s&a.", amount, target.getName());
    }

    @Endpoint(target = {PLAYER, CONSOLE, COMMAND_BLOCK}) 
    public void removeCoinsOffline(CommandSender cs, @SLPlayerArg(offline = true) SLPlayer target, @LiteralArg(value = "coin", aliases = {"coins"}) String l, @IntArg(max = -1) int amount) {
        updateCurrencyInDatabase(target.getUniqueId(), "coins", target.getCoins() + amount);
        UtilChat.s(Theme.SUCCESS, cs, "You've just took &6%d coins &afrom &e%s&a.", -amount, target.getName());
    }

    @Endpoint(target = {PLAYER, CONSOLE, COMMAND_BLOCK})
    public void removePremiumCoinsOffline(CommandSender cs, @SLPlayerArg(offline = true) SLPlayer target, @LiteralArg(value = "credit", aliases = {"credits", "premiumcredit", "premiumcredits"}) String l, @IntArg(max = -1) int amount) {
        updateCurrencyInDatabase(target.getUniqueId(), "premiumCredits", target.getCoins() + amount);
        UtilChat.s(Theme.SUCCESS, cs, "Now this player has exactly &b%d premium credits&a.", target.getPremiumCredits() + amount);
        UtilChat.s(Theme.SUCCESS, cs, "You've just took &b%d premium credits &afrom &e%s&a.", -amount, target.getName());
    }

    private void updateCurrencyInDatabase(UUID uuid, String currencyFieldName, int amount) {
        MongoCollection<Document> collection = SpleefLeague.getInstance().getPluginDB().getCollection("Players");
        Document index = new Document("uuid", uuid.toString());
        DatabaseConnection.updateFields(collection, index, Pair.<String, Object>of(currencyFieldName, amount));
    }
}
