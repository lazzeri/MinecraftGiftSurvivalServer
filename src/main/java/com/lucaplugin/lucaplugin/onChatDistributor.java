package com.lucaplugin.lucaplugin;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class onChatDistributor
{
    private static ArrayList<YouNowPlayer> playersList;
    private static final eventHandler eventHandlerObj = new eventHandler();

    public static void setPlayerList(ArrayList<YouNowPlayer> playerList)
    {
        playersList = playerList;
    }

    public static void triggerEventForChat(String jsonString, int broadcasterId)
    {        
        JSONObject obj = new JSONObject(jsonString);
        String data = obj.getString("data");
        JSONObject dataObj = new JSONObject(data);
        JSONArray comments = dataObj.getJSONObject("message").getJSONArray("comments");

        for (int i = 0; i < comments.length(); i++)
        {
            JSONObject comment = comments.getJSONObject(i);
            String message = comment.getString("comment");
            String donorName = comment.getString("name");
            int textStyle = comment.getInt("textStyle");

            // TODO TO TEST
            if (textStyle == 1 && (
                message.contains("has raided the broadcast with") ||
                message.contains("Zuschauer in deinen Broadcast gesendet!") ||
                message.contains("Raid") ||
                message.contains("izleyiciyle yayını bastı")
            )) {
                // Extract raid amount using regex
                String raidAmount = message.replaceAll(".*?(\\d+)(?!.*\\d).*", "$1");
                System.out.println("On Raid Trigger: " + donorName + " raided with " + raidAmount);
                onRaidTrigger(donorName, raidAmount);
            }
            // Handle fan messages
            else if (
                message.contains("I became a fan!") ||
                message.contains("Ich bin Fan geworden!") ||
                message.contains("Me he convertido en fan.")
            ) {
                System.out.println("On Fan Trigger: " + donorName);
                onFanTrigger(donorName);
            }
            // Handle invite messages
            else if (
                (message.contains("invited") && message.contains("fans to this broadcast.")) ||
                (message.contains("hat") && message.contains("zu diesem Broadcast eingeladen.")) ||
                (message.contains("he invitado a") && message.contains("fans a esta transmisión."))
            ) {
                System.out.println("On Invite Trigger: " + donorName);
                onInviteTrigger(donorName);
            }
            else {
                System.out.println(message + " | " + donorName + " | " + broadcasterId);
                triggerChatEvent(message, donorName, broadcasterId);
            }
        }
    }

    public static void triggerChatEvent(String message, String donorName, int broadcasterId)
    {
        //We trigger the event for each user connected to the userId
        for (YouNowPlayer playerItem : playersList)
        {
            if (playerItem.getUserId() == broadcasterId)
            {
                switch (message)
                {
                    case "test":
                        break;
                }
            }
        }
    }

    private static void onFanTrigger(String fanName) {
        System.out.println("On Fan trigger: " + fanName);
    }

    private static void onRaidTrigger(String raiderName, String raidAmount) {
        System.out.println("On Raid trigger: " + raiderName + " with " + raidAmount + " viewers");
    }

    private static void onInviteTrigger(String inviterName) {
        System.out.println("On Invite trigger: " + inviterName);
    }
}
