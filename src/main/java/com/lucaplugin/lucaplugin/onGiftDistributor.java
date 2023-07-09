package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class onGiftDistributor
{
    private static ArrayList<YouNowPlayer> playersList;
    private static final eventHandler eventHandlerObj = new eventHandler();

    public static void setPlayerList(ArrayList<YouNowPlayer> playerList)
    {
        playersList = playerList;
    }

    public static void triggerEventForGift(String eventInString, int broadcasterId)
    {
        JSONObject obj = new JSONObject(eventInString.replace("{event=onGift, data=", "").replace(", channel=public-channel_" + broadcasterId + "}", ""));
        JSONArray jsonArray = obj.getJSONObject("message").getJSONArray("stageGifts");
        System.out.println(jsonArray);
        for (int i = 0; i < jsonArray.length(); i++)
        {
            //Trigger here objects for gifts
            String skuName = jsonArray.getJSONObject(i).getString("SKU");
            String donorName = jsonArray.getJSONObject(i).getString("name");
            triggerGiftEvent(skuName, donorName, broadcasterId);

        }
    }

    public static void triggerGiftEvent(String skuName, String donorName, int broadcasterId)
    {
        //We trigger the event for each user connected to the userId
        for (YouNowPlayer playerItem : playersList)
        {
            if (playerItem.getUserId() == broadcasterId)
            {
                switch (skuName)
                {
                    case "50_LIKES_2":
                        //Here Comes the command then in
                        eventHandlerObj.itemSnack(playerItem.getPlayer(), donorName);
                        break;
                }
            }
        }
    }
}
