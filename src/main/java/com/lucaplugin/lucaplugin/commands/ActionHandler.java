package com.lucaplugin.lucaplugin.commands;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.lucaplugin.lucaplugin.LucaPlugin;
import com.lucaplugin.lucaplugin.events.GameEventHandler;
import com.lucaplugin.lucaplugin.util.McUtils;

/**
 * Handles action commands received from the backend WebSocket.
 * Actions can target all players or specific usernames.
 */
public class ActionHandler {

    private final LucaPlugin plugin;
    private final Logger logger;

    public ActionHandler(LucaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    /**
     * Represents a text segment with styling for titles/subtitles.
     */
    public static class TitleSegment {
        public String text;
        public String color;
        public boolean bold;
        public boolean italic;
        public boolean underlined;
        public boolean strikethrough;
        public boolean obfuscated;

        public TitleSegment(String text, String color, boolean bold, boolean italic,
                           boolean underlined, boolean strikethrough, boolean obfuscated) {
            this.text = text;
            this.color = color;
            this.bold = bold;
            this.italic = italic;
            this.underlined = underlined;
            this.strikethrough = strikethrough;
            this.obfuscated = obfuscated;
        }
    }

    /**
     * Execute an action command on target players.
     *
     * @param command The command to execute (e.g., "spawnwithers", "tntrain")
     * @param targetUsernames List of target usernames. If null or empty, targets all online players.
     * @param donorName The name to display as the donor/trigger source
     */
    public void executeAction(String command, List<String> targetUsernames, String donorName) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Collection<? extends Player> targetPlayers = getTargetPlayers(targetUsernames);

            if (targetPlayers.isEmpty()) {
                logger.warning("No target players found for action: " + command);
                return;
            }

            logger.info("Executing action '" + command + "' from " + donorName + " on " + targetPlayers.size() + " player(s)");

            for (Player player : targetPlayers) {
                executeCommandOnPlayer(player, command, donorName);
            }
        });
    }

    /**
     * Execute a sendtitle action with custom styled title/subtitle segments.
     *
     * @param titleSegments List of title segments (up to 5)
     * @param subtitleSegments List of subtitle segments (up to 5)
     * @param targetUsernames List of target usernames. If null or empty, targets all online players.
     * @param fadeIn Fade in time in ticks
     * @param stay Stay time in ticks
     * @param fadeOut Fade out time in ticks
     */
    public void executeSendTitle(List<TitleSegment> titleSegments, List<TitleSegment> subtitleSegments,
                                  List<String> targetUsernames, int fadeIn, int stay, int fadeOut) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Collection<? extends Player> targetPlayers = getTargetPlayers(targetUsernames);

            if (targetPlayers.isEmpty()) {
                logger.warning("No target players found for sendtitle action");
                return;
            }

            logger.info("Executing sendtitle on " + targetPlayers.size() + " player(s)");

            McUtils.TitleBuilder builder = McUtils.titleBuilder();
            builder.times(fadeIn, stay, fadeOut);

            // Add title segments (max 5)
            if (titleSegments != null) {
                for (int i = 0; i < Math.min(titleSegments.size(), 5); i++) {
                    TitleSegment seg = titleSegments.get(i);
                    McUtils.TextSegment textSeg = McUtils.text(seg.text, seg.color)
                        .bold(seg.bold)
                        .italic(seg.italic)
                        .underlined(seg.underlined)
                        .strikethrough(seg.strikethrough)
                        .obfuscated(seg.obfuscated);
                    builder.addTitle(textSeg);
                }
            }

            // Add subtitle segments (max 5)
            if (subtitleSegments != null) {
                for (int i = 0; i < Math.min(subtitleSegments.size(), 5); i++) {
                    TitleSegment seg = subtitleSegments.get(i);
                    McUtils.TextSegment textSeg = McUtils.text(seg.text, seg.color)
                        .bold(seg.bold)
                        .italic(seg.italic)
                        .underlined(seg.underlined)
                        .strikethrough(seg.strikethrough)
                        .obfuscated(seg.obfuscated);
                    builder.addSubtitle(textSeg);
                }
            }

            // Send to each target player
            for (Player player : targetPlayers) {
                builder.sendTo(player);
            }
        });
    }

    /**
     * Execute a sendchat action with custom styled chat segments.
     *
     * @param chatSegments List of chat segments (up to 10)
     * @param targetUsernames List of target usernames. If null or empty, targets all online players.
     */
    public void executeSendChat(List<TitleSegment> chatSegments, List<String> targetUsernames) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Collection<? extends Player> targetPlayers = getTargetPlayers(targetUsernames);

            if (targetPlayers.isEmpty()) {
                logger.warning("No target players found for sendchat action");
                return;
            }

            if (chatSegments == null || chatSegments.isEmpty()) {
                logger.warning("No chat segments provided for sendchat action");
                return;
            }

            logger.info("Executing sendchat on " + targetPlayers.size() + " player(s)");

            // Convert TitleSegments to McUtils.TextSegments
            java.util.List<McUtils.TextSegment> textSegments = new java.util.ArrayList<>();
            for (int i = 0; i < Math.min(chatSegments.size(), 10); i++) {
                TitleSegment seg = chatSegments.get(i);
                McUtils.TextSegment textSeg = McUtils.text(seg.text, seg.color)
                    .bold(seg.bold)
                    .italic(seg.italic)
                    .underlined(seg.underlined)
                    .strikethrough(seg.strikethrough)
                    .obfuscated(seg.obfuscated);
                textSegments.add(textSeg);
            }

            // Send to each target player
            for (Player player : targetPlayers) {
                McUtils.sendStyledChat(player, textSegments);
            }
        });
    }

    /**
     * Get the collection of players to target.
     * 
     * @param targetUsernames Specific usernames to target, or null/empty for all players
     * @return Collection of target players
     */
    private Collection<? extends Player> getTargetPlayers(List<String> targetUsernames) {
        if (targetUsernames == null || targetUsernames.isEmpty()) {
            // Target all online players
            return Bukkit.getOnlinePlayers();
        }

        // Target specific players by username
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> targetUsernames.contains(player.getName()))
                .toList();
    }

    /**
     * Execute a specific command on a player.
     */
    private void executeCommandOnPlayer(Player player, String command, String donorName) {
        String cmd = command.toLowerCase();

        switch (cmd) {
            case "spawnwithers":
                GameEventHandler.spawnWithers(player, donorName);
                break;
            case "spawnzombiearmy":
                GameEventHandler.spawnZombieArmy(player, donorName, plugin);
                break;
            case "spawntemporarywither":
                GameEventHandler.spawnTemporaryWither(player, donorName, plugin);
                break;
            case "spawnzombieccircle":
                GameEventHandler.spawnZombieCircle(player.getName(), plugin);
                break;
            case "createraid":
                GameEventHandler.createRaid(player, donorName);
                break;
            case "adrenalinrush":
                GameEventHandler.adrenalinRush(player, donorName);
                break;
            case "magicnotes":
                GameEventHandler.magicNotes(player, donorName, plugin, 20);
                break;
            case "tntrain":
                GameEventHandler.tntRain(player, donorName, plugin);
                break;
            case "itemsnack":
                GameEventHandler.itemSnack(player, donorName);
                break;
            case "throwexpbottles":
                GameEventHandler.throwExpBottles(player, donorName);
                break;
            case "spawnarmorstand":
                GameEventHandler.spawnEnchantedDiamondArmorStandInFrontOfPlayer(player, donorName);
                break;
            case "elytraandrockets":
                GameEventHandler.elytraAndRockets(player, donorName);
                break;
            case "netherattack":
                GameEventHandler.netherAttack(player, donorName);
                break;
            case "loadedcreeperattack":
                GameEventHandler.loadedCreeperAttack(player, donorName);
                break;
            case "zombieinvasion":
                GameEventHandler.zombieInvasion(player, donorName);
                break;
            case "farmtime":
                GameEventHandler.farmTime(player, donorName);
                break;
            case "createthunder":
                GameEventHandler.createThunder(player, donorName);
                break;
            case "giveslowpotion":
                GameEventHandler.giveSlowPotion(player, donorName);
                break;
            case "giveregenpotion":
                GameEventHandler.giveRegenPotion(player, donorName);
                break;
            case "randomteleport":
                GameEventHandler.randomTeleportPlayer(player, donorName);
                break;
            case "anvilrain":
                GameEventHandler.anvilRain(player, donorName, plugin);
                break;
            case "opsword":
                GameEventHandler.opSword(player, donorName);
                break;
            case "tpnetheroroverworld":
                GameEventHandler.tpNetherOrOverworld(player, donorName);
                break;
            case "startlava":
                GameEventHandler.startLava(player, System.currentTimeMillis(), plugin, 1, 2);
                break;
            case "spawnrandommob":
                GameEventHandler.spawnRandomEntityWithNametag(player, donorName);
                break;
            case "skeletonriders":
                GameEventHandler.createSkeletonRiders(player, donorName, 25, 153, 11, 163, 3.0F, plugin);
                break;
            case "chickencompanion":
                GameEventHandler.makeChickenCompanion(player, donorName, plugin);
                break;
            case "oneheart":
                GameEventHandler.oneHeart(player, plugin, donorName);
                break;
            case "twentyheart":
                GameEventHandler.twentyHeart(player, plugin, donorName);
                break;
            case "wolfcompanion":
                GameEventHandler.createWolfCompanion(player, donorName, plugin);
                break;
            default:
                logger.warning("Unknown action command: " + command);
                break;
        }
    }
}

