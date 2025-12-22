package com.lucaplugin.lucaplugin.network;

import java.net.URI;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class ChatRelayClient extends WebSocketClient {

    private final Plugin plugin;
    private final Logger logger;
    private boolean shouldReconnect = true;

    public ChatRelayClient(URI serverUri, Plugin plugin) {
        super(serverUri);
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.info("Connected to backend server!");
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");
            JSONObject data = json.optJSONObject("data");

            switch (type) {
                case "connected":
                    logger.info("Server acknowledged: " + (data != null ? data.optString("message", "") : ""));
                    break;
                case "chat":
                    handleChatMessage(data);
                    break;
                case "platform_connected":
                    String platform = data != null ? data.optString("platform", "unknown") : "unknown";
                    logger.info("Platform connected: " + platform);
                    break;
                case "platform_disconnected":
                    String disconnectedPlatform = data != null ? data.optString("platform", "unknown") : "unknown";
                    logger.info("Platform disconnected: " + disconnectedPlatform);
                    break;
                case "platform_error":
                    String errorPlatform = data != null ? data.optString("platform", "unknown") : "unknown";
                    String error = data != null ? data.optString("error", "Unknown error") : "Unknown error";
                    logger.warning("Platform error (" + errorPlatform + "): " + error);
                    break;
                default:
                    logger.fine("Unknown message type: " + type);
            }
        } catch (Exception e) {
            logger.warning("Error parsing message: " + e.getMessage());
        }
    }

    private void handleChatMessage(JSONObject data) {
        if (data == null) return;

        String platform = data.optString("platform", "unknown");
        String content = data.optString("content", "");
        JSONObject author = data.optJSONObject("author");
        String username = author != null ? author.optString("username", "Anonymous") : "Anonymous";
        String userId = author != null ? author.optString("userId", "") : "";
        boolean isSubscriber = author != null && author.optBoolean("isSubscriber", false);
        boolean isModerator = author != null && author.optBoolean("isModerator", false);

        JSONObject monetary = data.optJSONObject("monetary");
        String donationAmount = monetary != null ? monetary.optString("amount", null) : null;

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[").append(platform.toUpperCase()).append("] ");
        if (isModerator) logMessage.append("[MOD] ");
        if (isSubscriber) logMessage.append("[SUB] ");
        logMessage.append(username);
        if (!userId.isEmpty()) logMessage.append(" (").append(userId).append(")");
        logMessage.append(": ").append(content);

        logger.info(logMessage.toString());

        if (donationAmount != null && !donationAmount.isEmpty()) {
            logger.info("DONATION: " + username + " donated " + donationAmount + " - Message: " + content);
        }

        System.out.println("[ChatRelay] " + logMessage.toString());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Connection closed (code: " + code + "): " + reason);

        if (shouldReconnect) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                logger.info("Attempting to reconnect...");
                try {
                    reconnect();
                } catch (Exception e) {
                    logger.warning("Reconnection failed: " + e.getMessage());
                }
            }, 100L);
        }
    }

    @Override
    public void onError(Exception ex) {
        logger.severe("WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }

    public void disconnect() {
        shouldReconnect = false;
        close();
        logger.info("Disconnected from backend server.");
    }
}

