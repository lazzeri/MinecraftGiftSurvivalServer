package com.lucaplugin.lucaplugin;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class YouNowWebSocketClient extends WebSocketClient {

    private final String channelId;

    public YouNowWebSocketClient(String channelId) throws URISyntaxException {
        super(new URI("wss://younow-ws.zerody.one/?usePusherProtocol=true?channelId=" + channelId));
        this.channelId = channelId;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to YouNow WebSocket!");
        // Subscribe to onChat channel
        String subscribeMessage = "{\"event\":\"pusher:subscribe\",\"data\":{\"channel\":\"public-channel_" + channelId + "\"}}";
        send(subscribeMessage);
    }

    @Override
    public void onMessage(String message) {
/*        System.out.println("Received raw message: " + message);
*/
        if (message.contains("onChat")) {
            onChatDistributor.triggerEventForChat(message, Integer.parseInt(channelId));
        } else if (message.contains("onGift")) {
            onGiftDistributor.triggerEventForGift(message, Integer.parseInt(channelId));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("WebSocket error occurred:");
        ex.printStackTrace();
    }
}
