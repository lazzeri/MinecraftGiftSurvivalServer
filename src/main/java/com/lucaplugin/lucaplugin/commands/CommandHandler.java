package com.lucaplugin.lucaplugin.commands;

import org.bukkit.entity.Player;

import com.lucaplugin.lucaplugin.LucaPlugin;
import com.lucaplugin.lucaplugin.events.GameEventHandler;
import com.lucaplugin.lucaplugin.game.spawn.PlayerWrapper;

public class CommandHandler {

    private final LucaPlugin plugin;
    private final PlayerWrapper selectedUser;
    private final GameEventHandler eventHandlerObj;

    public CommandHandler(LucaPlugin plugin, PlayerWrapper selectedUser, GameEventHandler eventHandlerObj) {
        this.plugin = plugin;
        this.selectedUser = selectedUser;
        this.eventHandlerObj = eventHandlerObj;
    }

    public void handleCommand(Player player, String[] args) {
        String donorName = player.getName();
        if (args.length == 0) {
            player.sendMessage("§eUsage: test <command>");
            player.sendMessage("§7Type 'test help' for a list of commands.");
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelpMessage(player);
                break;
            case "ping":
                player.sendMessage("Pong!");
                break;
            case "wsconnect":
                if (plugin.isWebSocketConnected()) {
                    player.sendMessage("§eWebSocket is already connected.");
                } else {
                    player.sendMessage("§aConnecting to WebSocket server...");
                    plugin.connectToBackend();
                    if (plugin.isWebSocketConnected()) {
                        player.sendMessage("§aWebSocket connected successfully!");
                    } else {
                        player.sendMessage("§cFailed to connect to WebSocket server.");
                    }
                }
                break;
            case "wsdisconnect":
                if (!plugin.isWebSocketConnected()) {
                    player.sendMessage("§eWebSocket is not connected.");
                } else {
                    plugin.disconnectFromBackend();
                    player.sendMessage("§cWebSocket disconnected.");
                }
                break;
            case "wsstatus":
                if (plugin.isWebSocketConnected()) {
                    player.sendMessage("§aWebSocket status: Connected");
                } else {
                    player.sendMessage("§cWebSocket status: Disconnected");
                }
                break;
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
            case "resetworld":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: test resetworld <seed>");
                    return;
                }
                try {
                    long seed = Long.parseLong(args[1]);
                    GameEventHandler.resetWorld(player, donorName, plugin, seed);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cInvalid seed! Please provide a valid number.");
                }
                break;
            default:
                player.sendMessage("§cUnknown subcommand: " + subCommand);
                player.sendMessage("§7Type 'test help' for a list of commands.");
                break;
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== LucaPlugin Commands ===");
        player.sendMessage("§7Type 'test <command>' in chat to execute");
        player.sendMessage("§eping §7- Verification command");
        player.sendMessage("§ewsconnect §7- Connect to WebSocket");
        player.sendMessage("§ewsdisconnect §7- Disconnect from WebSocket");
        player.sendMessage("§ewsstatus §7- Check WebSocket status");
        player.sendMessage("§6--- Mob Spawning ---");
        player.sendMessage("§espawnwithers, spawnzombiearmy, spawntemporarywither");
        player.sendMessage("§espawnzombieccircle, spawnrandommob, createraid");
        player.sendMessage("§enetherattack, loadedcreeperattack, zombieinvasion");
        player.sendMessage("§efarmtime, skeletonriders");
        player.sendMessage("§6--- Companions ---");
        player.sendMessage("§echickencompanion, wolfcompanion");
        player.sendMessage("§6--- Items ---");
        player.sendMessage("§espawnarmorstand, elytraandrockets, opsword, itemsnack");
        player.sendMessage("§6--- Effects ---");
        player.sendMessage("§eadrenalinrush, giveslowpotion, giveregenpotion");
        player.sendMessage("§eoneheart, twentyheart, throwexpbottles");
        player.sendMessage("§6--- Environment ---");
        player.sendMessage("§ecreatethunder, tntrain, anvilrain, startlava, magicnotes");
        player.sendMessage("§6--- Teleport ---");
        player.sendMessage("§erandomteleport, tpnetheroroverworld");
        player.sendMessage("§6--- World Management ---");
        player.sendMessage("§eresetworld <seed> §7- Reset world with new seed");
    }
}

