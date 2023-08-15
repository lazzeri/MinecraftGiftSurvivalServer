package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class onGiftDistributor
{
    private static ArrayList<YouNowPlayer> playersList;
    private static final eventHandler eventHandlerObj = new eventHandler();


    public static void setPlugin(Plugin plugin)
    {
        onGiftDistributor.plugin = plugin;
    }

    public static Plugin plugin;

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
                triggerEvents(likesInt, playerItem.getPlayer(),donorName);
            }
        }
    }


    public static void triggerEvents(int likes, Player player, String donorName) {


        if(likes == -3){
            eventHandlerObj.makeChickenCompanion(player, donorName, plugin);
            return;
        }
        if(likes == -2){
            eventHandlerObj.createWolfCompanion(player, donorName, plugin);
            return;
        }

        if(likes == -1){
            eventHandlerObj.createThunder(player, donorName);
            return;
        }

        if (likes >= 4500) {
            return;
        } else if (likes >= 1100) {
            eventHandlerObj.tntRain(player, donorName, plugin, likes);
            eventHandlerObj.tpNetherOrOverworld(player, donorName, likes);
            return;
        } else if (likes >= 400) {
            eventHandlerObj.anvilRain(player, donorName, plugin, likes);
            eventHandler.createVillagerCircle(player,donorName,15,likes);
            eventHandlerObj.oneHeart(player, plugin, donorName, likes);
            eventHandlerObj.twentyHeart(player, plugin, donorName, likes);
            return;
        } else if (likes >= 50) {
            return;
        } else {
            eventHandlerObj.throwExpBottles(player, donorName,likes);
            eventHandlerObj.randomTeleportPlayer(player,donorName,likes);
            eventHandler.itemSnack(player,donorName,likes);
            eventHandlerObj.giveSlowPotion(player, donorName, likes);
            eventHandlerObj.giveBlindnessPotion(player, donorName, likes);
            eventHandlerObj.giveRegenPotion(player, donorName, likes);
            return;
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
                        break;
                }
            }
        }
    }
}
