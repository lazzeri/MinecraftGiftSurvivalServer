package com.lucaplugin.lucaplugin.network;

import java.net.URI;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import com.lucaplugin.lucaplugin.commands.ActionHandler;
import com.lucaplugin.lucaplugin.util.McUtils;

public class ChatRelayClient extends WebSocketClient {

    private final Plugin plugin;
    private final Logger logger;
    private boolean shouldReconnect = true;
    private ActionHandler actionHandler;

    public ChatRelayClient(URI serverUri, Plugin plugin) {
        super(serverUri);
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    /**
     * Set the action handler to process action commands from the backend.
     */
    public void setActionHandler(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
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
                case "action":
                    handleActionMessage(data);
                    break;
                default:
                    logger.fine("Unknown message type: " + type);
            }
        } catch (Exception e) {
            logger.warning("Error parsing message: " + e.getMessage());
        }
    }

    private void handleChatMessage(JSONObject data) {
        if (data == null) {
            return;
        }

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
       
        if (isModerator) {
            logMessage.append("[MOD] ");
        }
        if (isSubscriber) {
            logMessage.append("[SUB] ");
        }
        if (!userId.isEmpty()) {
            logMessage.append(" (").append(userId).append(")");
        }
        logMessage.append(": ").append(content);

        logger.info(logMessage.toString());

        if (donationAmount != null && !donationAmount.isEmpty()) {
            logger.info("DONATION: " + username + " donated " + donationAmount + " - Message: " + content);
        }
        McUtils.showBroadcasterMessage(platform, username, logMessage.toString());
        System.out.println("[ChatRelay] " + logMessage.toString());
    }

    /**
     * Handle action messages from the backend.
     *
     * Standard action format:
     * { "type": "action", "data": { "command": "spawnwithers", "targetUsernames": ["player1"] or null, "donorName": "StreamerName", "likes": 5 } }
     *
     * SendTitle action format:
     * { "type": "action", "data": { "command": "sendtitle", "targetUsernames": ["player1"] or null,
     *   "titleSegments": [{ "text": "Hello", "color": "gold", "bold": true, "italic": false, "underlined": false, "strikethrough": false, "obfuscated": false }],
     *   "subtitleSegments": [{ "text": "World", "color": "aqua", "bold": false, "italic": true, "underlined": false, "strikethrough": false, "obfuscated": false }],
     *   "fadeIn": 10, "stay": 70, "fadeOut": 20 } }
     */
    private void handleActionMessage(org.json.JSONObject data) {
        if (data == null) {
            logger.warning("Received action message with null data");
            return;
        }

        if (actionHandler == null) {
            logger.warning("ActionHandler not set, cannot process action");
            return;
        }

        String command = data.optString("command", "");
        if (command.isEmpty()) {
            logger.warning("Received action message with empty command");
            return;
        }

        // Parse target usernames (null or empty means all players)
        java.util.List<String> targetUsernames = parseTargetUsernames(data);

        // Check if title data is present (can be sent with any command)
        boolean hasTitleData = data.has("titleSegments") || data.has("subtitleSegments");
        if (hasTitleData) {
            handleSendTitleAction(data, targetUsernames);
        }

        // If the command is just "sendtitle", we're done after showing the title
        if (command.equalsIgnoreCase("sendtitle")) {
            return;
        }

        // Handle regular action commands
        String donorName = data.optString("donorName", "Backend");
        int likes = data.optInt("likes", 1);

        logger.info("Received action: " + command + " from " + donorName
                + " (likes: " + likes + ", targets: "
                + (targetUsernames == null ? "all" : targetUsernames.toString()) + ")");

        actionHandler.executeAction(command, targetUsernames, donorName);
    }

    /**
     * Parse target usernames from JSON data.
     */
    private java.util.List<String> parseTargetUsernames(org.json.JSONObject data) {
        org.json.JSONArray targetsArray = data.optJSONArray("targetUsernames");
        if (targetsArray == null || targetsArray.length() == 0) {
            return null;
        }

        java.util.List<String> targetUsernames = new java.util.ArrayList<>();
        for (int i = 0; i < targetsArray.length(); i++) {
            String username = targetsArray.optString(i, null);
            if (username != null && !username.isEmpty()) {
                targetUsernames.add(username);
            }
        }
        return targetUsernames.isEmpty() ? null : targetUsernames;
    }

    /**
     * Handle the sendtitle action with custom styled title/subtitle/chat segments.
     */
    private void handleSendTitleAction(org.json.JSONObject data, java.util.List<String> targetUsernames) {
        // Parse title segments
        java.util.List<ActionHandler.TitleSegment> titleSegments = parseTextSegments(data.optJSONArray("titleSegments"));
        java.util.List<ActionHandler.TitleSegment> subtitleSegments = parseTextSegments(data.optJSONArray("subtitleSegments"));
        java.util.List<ActionHandler.TitleSegment> chatSegments = parseTextSegments(data.optJSONArray("chatSegments"));

        // Parse timing (with defaults)
        int fadeIn = data.optInt("fadeIn", 10);
        int stay = data.optInt("stay", 70);
        int fadeOut = data.optInt("fadeOut", 20);

        logger.info("Received sendtitle action (targets: "
                + (targetUsernames == null ? "all" : targetUsernames.toString())
                + ", titleSegments: " + (titleSegments != null ? titleSegments.size() : 0)
                + ", subtitleSegments: " + (subtitleSegments != null ? subtitleSegments.size() : 0)
                + ", chatSegments: " + (chatSegments != null ? chatSegments.size() : 0) + ")");

        actionHandler.executeSendTitle(titleSegments, subtitleSegments, targetUsernames, fadeIn, stay, fadeOut);

        // Also send chat segments if provided
        if (chatSegments != null && !chatSegments.isEmpty()) {
            actionHandler.executeSendChat(chatSegments, targetUsernames);
        }
    }

    /**
     * Parse an array of text segments from JSON.
     */
    private java.util.List<ActionHandler.TitleSegment> parseTextSegments(org.json.JSONArray segmentsArray) {
        if (segmentsArray == null || segmentsArray.length() == 0) {
            return null;
        }

        java.util.List<ActionHandler.TitleSegment> segments = new java.util.ArrayList<>();
        int maxSegments = Math.min(segmentsArray.length(), 5); // Max 5 segments

        for (int i = 0; i < maxSegments; i++) {
            org.json.JSONObject segObj = segmentsArray.optJSONObject(i);
            if (segObj != null) {
                String text = segObj.optString("text", "");
                String color = segObj.optString("color", "white");
                boolean bold = segObj.optBoolean("bold", false);
                boolean italic = segObj.optBoolean("italic", false);
                boolean underlined = segObj.optBoolean("underlined", false);
                boolean strikethrough = segObj.optBoolean("strikethrough", false);
                boolean obfuscated = segObj.optBoolean("obfuscated", false);

                segments.add(new ActionHandler.TitleSegment(text, color, bold, italic, underlined, strikethrough, obfuscated));
            }
        }

        return segments.isEmpty() ? null : segments;
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
        logger.severe(() -> "WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }

    public void disconnect() {
        shouldReconnect = false;
        close();
        logger.info("Disconnected from backend server.");
    }
}
