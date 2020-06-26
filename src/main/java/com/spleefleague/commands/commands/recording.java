package com.spleefleague.commands.commands;

import com.spleefleague.annotations.*;

import com.spleefleague.commands.Commands;
import com.spleefleague.commands.command.BasicCommand;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.recording.Recording;
import com.spleefleague.core.utils.recording.Replay;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Josh on 09/08/2016.
 */
public class recording extends BasicCommand {

    private Map<UUID, Replay> replays;
    public static final TextComponent PLAY_PAUSE = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "⏵⏸" + ChatColor.DARK_GRAY + "]");
    public static final TextComponent STOP = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "⏹" + ChatColor.DARK_GRAY + "]");
    public static final TextComponent SKIP_FORWARD = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "⏵⏵" + ChatColor.DARK_GRAY + "]");
    public static final TextComponent SKIP_BACKWARD = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "⏴⏴" + ChatColor.DARK_GRAY + "]");
    public static final TextComponent SPEED_UP_1 = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "+" + ChatColor.DARK_GRAY + "]");
    public static final TextComponent SPEED_UP_5 = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "++" + ChatColor.DARK_GRAY + "]");
    public static final TextComponent SPEED_DOWN_1 = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "-" + ChatColor.DARK_GRAY + "]");
    public static final TextComponent SPEED_DOWN_5 = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "--" + ChatColor.DARK_GRAY + "]");


    public recording(CorePlugin plugin, String name, String usage) {
        super(plugin, new recordingDispatcher(), name, usage);
        replays = new HashMap<>();
        Bukkit.getScheduler().runTaskTimer(Commands.getInstance(), () -> replays.values().removeIf(Replay::isDone), 0, 20 * 60 * 5);
    }

    @Endpoint
    public void startRecording(CommandSender cs, @LiteralArg(value = "start") String literal, @PlayerArg Player player) {
        if(cs instanceof SLPlayer) {
            SLPlayer slp = (SLPlayer)cs;
            if (!slp.getRank().hasPermission(Rank.MODERATOR)) {
                sendUsage(cs);
                return;
            }
        }
        SpleefLeague.getInstance().getRecordingManager().startRecording(player);
        success(cs, "Started recording of " + ChatColor.RED + player.getName() + ChatColor.GREEN + "!");
    }

    @Endpoint(target = CommandSource.PLAYER)
    public void stopRecording(SLPlayer cs, @LiteralArg(value = "stop") String literal, @PlayerArg Player player) {
        if (!cs.getRank().hasPermission(Rank.MODERATOR)) {
            sendUsage(cs);
            return;
        }
        Recording recording = SpleefLeague.getInstance().getRecordingManager().stopRecording(player.getUniqueId());
        if(recording == null) {
            error(cs, "The player "+ ChatColor.WHITE + player.getName() + ChatColor.GREEN + "is not being recorded right now!");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Commands.getInstance(), () -> {
            ObjectId recordingId = SpleefLeague.getInstance().getRecordingManager().saveRecording(recording);
            success(cs, "Stopped recording of " + ChatColor.RED + player.getName() + ChatColor.GREEN + "!");
            TextComponent recordingIdFirst = new TextComponent("Recording ID: ");
            recordingIdFirst.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
            TextComponent recordingIdSecond = new TextComponent(recordingId.toHexString());
            recordingIdSecond.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            recordingIdSecond.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to copy") } ));
            recordingIdSecond.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, recordingId.toHexString()));
            recordingIdFirst.addExtra(recordingIdSecond);
            cs.spigot().sendMessage(recordingIdFirst);
        });
    }

    @Endpoint(target = {CommandSource.COMMAND_BLOCK, CommandSource.CONSOLE})
    public void stopRecording(CommandSender cs, @LiteralArg(value = "stop") String literal, @PlayerArg Player player) {
        if(cs instanceof SLPlayer) {
            SLPlayer slp = (SLPlayer)cs;
            if (!slp.getRank().hasPermission(Rank.MODERATOR) && slp.getRank() != Rank.ORGANIZER) {
                sendUsage(cs);
                return;
            }
        }
        Recording recording = SpleefLeague.getInstance().getRecordingManager().stopRecording(player.getUniqueId());
        if(recording == null) {
            error(cs, "The player "+ ChatColor.WHITE + player.getName() + ChatColor.GREEN + "is not being recorded right now!");
            return;
        }
        ObjectId recordingId = SpleefLeague.getInstance().getRecordingManager().saveRecording(recording);
        success(cs, "Stopped recording of " + ChatColor.RED + player.getName() + ChatColor.GREEN + "!");
        success(cs, ChatColor.DARK_GRAY + "Recording ID: " + ChatColor.GRAY + recordingId.toHexString());
    }

    @Endpoint(target = CommandSource.PLAYER)
    public void playRecording(SLPlayer cs, @LiteralArg(value = "play") String literal, @StringArg String hexId) {
        if (!cs.getRank().hasPermission(Rank.MODERATOR) && cs.getRank() != Rank.ORGANIZER) {
            sendUsage(cs);
            return;
        }
        ObjectId id = null;
        try {
            id = new ObjectId(hexId);
        } catch (Exception e) {
            error(cs, "Invalid recording id: " + ChatColor.GRAY + hexId);
            return;
        }
        Recording recording = SpleefLeague.getInstance().getRecordingManager().loadRecording(id);
        if(recording == null) {
            error(cs, "There is no recording with id: "+ ChatColor.GRAY + hexId);
            return;
        }
        Replay replay = SpleefLeague.getInstance().getRecordingManager().playRecording(recording, cs);
        TextComponent recordingTpMessage = new TextComponent(SpleefLeague.getInstance().getChatPrefix() + net.md_5.bungee.api.ChatColor.GREEN + "Started recording." + net.md_5.bungee.api.ChatColor.DARK_GRAY + " [" + ChatColor.RED + "Click to tp" + net.md_5.bungee.api.ChatColor.DARK_GRAY + "]");
        recordingTpMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Tp to start of recording") } ));
        recordingTpMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tppos " + recording.getStart().getX() + " " + recording.getStart().getY() + " " + recording.getStart().getZ()));
        cs.spigot().sendMessage(recordingTpMessage);
        UUID replayId = UUID.randomUUID();
        replays.put(replayId, replay);
        cs.spigot().sendMessage(buildMediaControlls(replayId.toString()));
    }

    @Endpoint(target = {CommandSource.PLAYER, CommandSource.CONSOLE, CommandSource.COMMAND_BLOCK})
    public void playforRecording(CommandSender cs, @LiteralArg(value = "playfor") String literal, @PlayerArg Player target, @StringArg String hexId) {
        if(cs instanceof SLPlayer) {
            SLPlayer slp = (SLPlayer)cs;
            if (!slp.getRank().hasPermission(Rank.MODERATOR) && slp.getRank() != Rank.ORGANIZER) {
                sendUsage(cs);
                return;
            }
        }
        ObjectId id = null;
        try {
            id = new ObjectId(hexId);
        } catch (Exception e) {
            error(cs, "Invalid recording id: " + ChatColor.GRAY + hexId);
            return;
        }
        Recording recording = SpleefLeague.getInstance().getRecordingManager().loadRecording(id);
        if(recording == null) {
            error(cs, "There is no recording with id: "+ ChatColor.GRAY + hexId);
            return;
        }
        Replay replay = SpleefLeague.getInstance().getRecordingManager().playRecording(recording, target);
        TextComponent recordingTpMessage = new TextComponent(SpleefLeague.getInstance().getChatPrefix() + net.md_5.bungee.api.ChatColor.GREEN + "Started recording." + net.md_5.bungee.api.ChatColor.DARK_GRAY + " [" + ChatColor.RED + "Click to tp" + net.md_5.bungee.api.ChatColor.DARK_GRAY + "]");
        recordingTpMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Tp to start of recording") } ));
        recordingTpMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tppos " + recording.getStart().getX() + " " + recording.getStart().getY() + " " + recording.getStart().getZ()));
        target.spigot().sendMessage(recordingTpMessage);
        UUID replayId = UUID.randomUUID();
        replays.put(replayId, replay);
        target.spigot().sendMessage(buildMediaControlls(replayId.toString()));
    }

    private BaseComponent buildMediaControlls(String replay) {
//        TextComponent comp1 = SPEED_DOWN_5.duplicate();
        SPEED_DOWN_5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recording adjustPlayback speed_dec_5 " + replay));
//        TextComponent comp2 = SPEED_DOWN_1.duplicate();
        SPEED_DOWN_1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recording adjustPlayback speed_dec_1 " + replay));

//        TextComponent comp3 = SKIP_BACKWARD.duplicate();
//        comp3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recording adjustPlayback backward " + replay));

//        TextComponent comp4 = STOP.duplicate();
        STOP.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recording adjustPlayback stop " + replay));
//        TextComponent comp5 = PLAY_PAUSE.duplicate();
        PLAY_PAUSE.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recording adjustPlayback toggle_paused " + replay));

//        TextComponent comp6 = SKIP_FORWARD.duplicate();
//        comp6.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recording adjustPlayback forward " + replay));

//        TextComponent comp7 = SPEED_UP_1.duplicate();
        SPEED_UP_1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recording adjustPlayback speed_inc_1 " + replay));
//        TextComponent comp8 = SPEED_UP_5.duplicate();
        SPEED_UP_5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recording adjustPlayback speed_inc_5 " + replay));
        TextComponent mediaControlls = new TextComponent();
        mediaControlls.addExtra(SPEED_DOWN_5);
        mediaControlls.addExtra(SPEED_DOWN_1);
//        mediaControlls.addExtra(comp3);
        mediaControlls.addExtra(STOP);
        mediaControlls.addExtra(PLAY_PAUSE);
//        mediaControlls.addExtra(comp6);
        mediaControlls.addExtra(SPEED_UP_1);
        mediaControlls.addExtra(SPEED_UP_5);
        return mediaControlls;
    }

    @Endpoint(target = CommandSource.PLAYER)
    public void adjustPlayback(Player cs, @LiteralArg(value = "adjustPlayback") String literal, @StringArg String setting, @StringArg String replayId) {
        Replay replay = replays.get(UUID.fromString(replayId));
        if(replay == null) {
            error(cs, "Replay not found.");
        }
        else if(replay.isDone() && !"toggle_paused".equals(setting)) {
            error(cs, "This replay has ended. Press play to start it again.");
        }
        switch(setting) {
            case "speed_inc_1": {
                replay.setSpeed(Math.min(20, replay.getSpeed() + 1));
                break;
            }
            case "speed_dec_1": {
                replay.setSpeed(Math.max(0, replay.getSpeed() - 1));
                break;
            }
            case "speed_inc_5": {
                if(replay.getSpeed() < 0 && replay.getSpeed() > -5) {
                    replay.setSpeed(0);
                }
                else {
                    replay.setSpeed(Math.min(20, replay.getSpeed() + 5));
                }

                break;
            }
            case "speed_dec_5": {
                if(replay.getSpeed() > 0 && replay.getSpeed() < 5) {
                    replay.setSpeed(0);
                }
                else {
                    replay.setSpeed(Math.max(0, replay.getSpeed() - 5));
                }
                break;
            }
            case "toggle_paused": {
                if(replay.isDone()) {
                    replay = SpleefLeague.getInstance().getRecordingManager().playRecording(replay.getRecording(), cs);
                    replays.put(UUID.fromString(replayId), replay);
                }
                else {
                    replay.setPaused(!replay.isPaused());
                }
                break;
            }
            case "stop": {
                replay.cancel();
                break;
            }
            case "forward": {
                replay.setSpeed(Math.abs(replay.getSpeed()));
                replay.setPaused(false);
                break;
            }
            case "backward": {
                replay.setSpeed(-Math.abs(replay.getSpeed()));
                replay.setPaused(false);
                break;
            }
        }
    }
}
