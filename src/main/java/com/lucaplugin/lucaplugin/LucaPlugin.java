package com.lucaplugin.lucaplugin;

import java.net.URI;

import org.bukkit.plugin.java.JavaPlugin;

import com.lucaplugin.lucaplugin.commands.ActionHandler;
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
    private static final String WEBSOCKET_URL = "ws://localhost:3005/ws";

    // Game state
    public static double xBorderCenter = 0;
    public static double yBorderCenter = 0;
    public static boolean gameStarted = false;

    // Components
    private ChatRelayClient chatRelayClient;
    private CommandHandler commandHandler;
    private ActionHandler actionHandler;
    private final PlayerWrapper selectedUser = new PlayerWrapper();
    private final GameEventHandler eventHandlerObj = new GameEventHandler();

    @Override
    public void onEnable() {
        System.out.println("[LucaPlugin] Starting plugin...");

        // Initialize command handler
        commandHandler = new CommandHandler(this, selectedUser, eventHandlerObj);

        // Initialize action handler for backend-triggered commands
        actionHandler = new ActionHandler(this);

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

    public void connectToBackend() {
        try {
            chatRelayClient = new ChatRelayClient(new URI(WEBSOCKET_URL), this);
            // Wire the action handler to process backend commands
            chatRelayClient.setActionHandler(actionHandler);
            chatRelayClient.connectBlocking();
            System.out.println("[LucaPlugin] WebSocket connection established to " + WEBSOCKET_URL);
        } catch (Exception e) {
            System.out.println("[LucaPlugin] Failed to connect to backend: " + e.getMessage());
            System.out.println("[LucaPlugin] Plugin will continue without stream integration.");
        }
    }

    public void disconnectFromBackend() {
        if (chatRelayClient != null) {
            chatRelayClient.disconnect();
            chatRelayClient = null;
            System.out.println("[LucaPlugin] Disconnected from backend server.");
        }
    }

    public boolean isWebSocketConnected() {
        return chatRelayClient != null && chatRelayClient.isOpen();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(eventHandlerObj, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this, commandHandler), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new com.lucaplugin.lucaplugin.listeners.PortalListener(), this);
    }
}
