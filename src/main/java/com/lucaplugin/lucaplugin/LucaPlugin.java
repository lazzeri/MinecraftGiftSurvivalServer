package com.lucaplugin.lucaplugin;

import java.net.URI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.lucaplugin.lucaplugin.commands.CommandHandler;
import com.lucaplugin.lucaplugin.events.GameEventHandler;
import com.lucaplugin.lucaplugin.game.spawn.PlayerWrapper;
import com.lucaplugin.lucaplugin.listeners.DeathListener;
import com.lucaplugin.lucaplugin.listeners.EntityDamageListener;
import com.lucaplugin.lucaplugin.listeners.PlayerChatListener;
import com.lucaplugin.lucaplugin.listeners.PlayerJoinListener;
import com.lucaplugin.lucaplugin.listeners.PlayerMoveListener;
import com.lucaplugin.lucaplugin.network.ChatRelayClient;

public final class LucaPlugin extends JavaPlugin {

    // Configuration
    private static final String WEBSOCKET_URL = "ws://localhost:3001/ws";

    // Game state
    public static double xBorderCenter = 0;
    public static double yBorderCenter = 0;
    public static boolean gameStarted = false;

    // Components
    private ChatRelayClient chatRelayClient;
    private CommandHandler commandHandler;
    private final PlayerWrapper selectedUser = new PlayerWrapper();
    private final GameEventHandler eventHandlerObj = new GameEventHandler();

    @Override
    public void onEnable() {
        System.out.println("[LucaPlugin] Starting plugin...");

        // Initialize command handler
        commandHandler = new CommandHandler(this, selectedUser, eventHandlerObj);

        // Connect to backend WebSocket server
        connectToBackend();

        // Register event listeners
        registerListeners();

        System.out.println("[LucaPlugin] Plugin enabled successfully.");
    }

    @Override
    public void onDisable() {
        disconnectFromBackend();
        System.out.println("[LucaPlugin] Plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return commandHandler.handleCommand(sender, cmd, label, args);
    }

    private void connectToBackend() {
        try {
            chatRelayClient = new ChatRelayClient(new URI(WEBSOCKET_URL), this);
            chatRelayClient.connectBlocking();
            System.out.println("[LucaPlugin] WebSocket connection established to " + WEBSOCKET_URL);
        } catch (Exception e) {
            System.out.println("[LucaPlugin] Failed to connect to backend: " + e.getMessage());
            System.out.println("[LucaPlugin] Plugin will continue without stream integration.");
        }
    }

    private void disconnectFromBackend() {
        if (chatRelayClient != null) {
            chatRelayClient.disconnect();
            System.out.println("[LucaPlugin] Disconnected from backend server.");
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(eventHandlerObj, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
    }
}
