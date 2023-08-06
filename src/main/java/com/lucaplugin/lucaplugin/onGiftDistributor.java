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
            System.out.println(jsonArray.getJSONObject(i).toString());
            String skuName = jsonArray.getJSONObject(i).getString("SKU");
            String donorName = jsonArray.getJSONObject(i).getString("name");
            String likes = jsonArray.getJSONObject(i).getJSONObject("extraData").getString("likes");
            triggerGiftEventWithLikes(likes, donorName, broadcasterId);
        }
    }

    public static void triggerGiftEventWithLikes(String likes, String donorName, int broadcasterId)
    {
        //We trigger the event for each user connected to the userId
        for (YouNowPlayer playerItem : playersList)
        {
            if (playerItem.getUserId() == broadcasterId)
            {
                int likesInt = Integer.parseInt(likes);

                if (likesInt < 100) {
                    // Here comes the command for likes smaller than 100
                    eventHandlerObj.itemSnack(playerItem.getPlayer(), donorName);
                } else if (likesInt >= 100 && likesInt < 200) {
                    // Gifts for likes between 100 and 199
                    // Add your code here for this case
                } else if (likesInt >= 200 && likesInt < 500) {
                    // Gifts for likes between 200 and 499
                    // Add your code here for this case
                } else if (likesInt >= 500 && likesInt < 1000) {
                    // Gifts for likes between 500 and 999
                    // Add your code here for this case
                } else if (likesInt >= 1000) {
                    // Gifts for likes higher than or equal to 1000
                    // Add your code here for this case
                }

            }
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
