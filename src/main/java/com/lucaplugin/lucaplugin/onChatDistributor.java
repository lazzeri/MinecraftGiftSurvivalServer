package com.lucaplugin.lucaplugin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
        JSONObject obj = new JSONObject(jsonString.replace("{event=onChat, data=", "").replace(", channel=public-channel_" + broadcasterId + "}", ""));
        JSONArray jsonArray = obj.getJSONObject("message").getJSONArray("comments");

        for (int i = 0; i < jsonArray.length(); i++)
        {
            String message = jsonArray.getJSONObject(i).getString("comment");
            String donorName = jsonArray.getJSONObject(i).getString("name");

            System.out.println(message + " | " + donorName + " | " + broadcasterId);
            triggerChatEvent(message, donorName, broadcasterId);
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
}
