package com.lucaplugin.lucaplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

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
                triggerEvents(likesInt, playerItem.getPlayer(), donorName);
            }
        }
    }


    public static void triggerEvents(int likes, Player player, String donorName)
    {
        int[] possibleLikes = {-3, -2, -1, 20, 51, 401, 1111, 5000};

        Random random = new Random();
        int randomIndex = random.nextInt(possibleLikes.length);
        int selectedLikes = possibleLikes[randomIndex];
        likes = selectedLikes;

        if (likes == -3)
        {
            eventHandler.makeChickenCompanion(player, donorName, plugin);
            return;
        }
        if (likes == -2)
        {
            eventHandler.createWolfCompanion(player, donorName, plugin);
            return;
        }

        if (likes == -1)
        {
            eventHandler.createThunder(player, donorName);
            return;
        }

        if (likes >= 4500)
        {
            int randomNumber = random.nextInt(4); // Generates a random number between 0 and 3
            switch (randomNumber)
            {
                case 0:
                    eventHandler.spawnWithers(player, donorName, likes);
                    break;
                case 1:
                    eventHandler.spawnEnchantedDiamondArmorStandInFrontOfPlayer(player, donorName, likes);
                    break;
                case 2:
                    eventHandler.loadedCreeperAttack(player, donorName, likes);
                    break;
                case 3:
                    eventHandler.opSword(player, donorName, likes);
                    break;
                default:
                    // This case should not be reached, but you can handle it if needed
                    break;
            }
            return;
        }
        if (likes >= 1100)
        {
            int randomNumber = random.nextInt(5); // Generates a random number between 0 and 3
            switch (randomNumber)
            {
                case 0:
                    eventHandler.elytraAndRockets(player, donorName, likes);
                    break;
                case 1:
                    //TODO Wont work in nether
                    eventHandler.createSkeletonRiders(
                            player,
                            donorName,
                            likes,
                            10,
                            220,
                            170,
                            255,
                            3.0F,
                            plugin
                    );
                    break;
                case 2:
                    eventHandler.netherAttack(player, donorName, likes);
                    break;
                case 3:
                    eventHandler.tntRain(player, donorName, plugin, likes);
                    break;
                case 4:
                    eventHandler.tpNetherOrOverworld(player, donorName, likes);
                    break;
                default:
                    // This case should not be reached, but you can handle it if needed
                    break;
            }
            return;

        }
        if (likes >= 400)
        {
            int randomNumber = random.nextInt(4); // Generates a random number between 0 and 3
            switch (randomNumber)
            {
                case 0:
                    eventHandler.createRaid(player, donorName, likes);
                    break;
                case 1:
                    eventHandler.startValuableItemRain(player, donorName, likes, plugin);
                    break;
                case 2:
                    eventHandler.oneHeart(player, plugin, donorName, likes);
                    break;
                case 3:
                    eventHandler.twentyHeart(player, plugin, donorName, likes);
                    break;
                default:
                    // This case should not be reached, but you can handle it if needed
                    break;
            }
            return;
        }
        if (likes >= 50)
        {
            int randomNumber = random.nextInt(4); // Generates a random number between 0 and 3
            switch (randomNumber)
            {
                case 0:
                    eventHandler.farmTime(player, donorName, likes);
                    break;
                case 1:
                    eventHandler.giveRegenPotion(player, donorName, likes);
                    break;
                case 2:
                    eventHandler.zombieInvasion(player, donorName, likes);
                    break;
                case 3:
                    eventHandler.anvilRain(player, donorName, plugin, likes);
                    break;
                default:
                    // This case should not be reached, but you can handle it if needed
                    break;
            }
            return;
        }

        int randomNumber = random.nextInt(6); // Generates a random number between 0 and 3
        switch (randomNumber)
        {
            case 0:
                eventHandler.spawnRandomEntityWithNametag(player, donorName, likes);
                break;
            case 1:
                eventHandler.throwExpBottles(player, donorName, likes);
                break;
            case 2:
                eventHandler.randomTeleportPlayer(player, donorName, likes);
                break;
            case 3:
                eventHandler.itemSnack(player, donorName, likes);
                break;
            case 4:
                eventHandler.giveSlowPotion(player, donorName, likes);
                break;
            case 5:
                eventHandler.giveBlindnessPotion(player, donorName, likes);
                break;
            default:
                // This case should not be reached, but you can handle it if needed
                break;
        }
    }

    public static void triggerEvent(int num, Player player, String donorName, int likes)
    {
        switch (num)
        {
            case 0:
                eventHandler.makeChickenCompanion(player, donorName, plugin);
                break;
            case 1:
                eventHandler.createWolfCompanion(player, donorName, plugin);
                break;
            case 2:
                eventHandler.createThunder(player, donorName);
                break;
            case 3:
                eventHandler.spawnWithers(player, donorName, likes);
                break;
            case 4:
                eventHandler.spawnEnchantedDiamondArmorStandInFrontOfPlayer(player, donorName, likes);
                break;
            case 5:
                eventHandler.loadedCreeperAttack(player, donorName, likes);
                break;
            case 6:
                eventHandler.opSword(player, donorName, likes);
                break;
            case 7:
                eventHandler.elytraAndRockets(player, donorName, likes);
                break;
            case 8:
                eventHandler.createSkeletonRiders(player, donorName, likes, 10, 220, 170, 255, 3.0F, plugin);
                break;
            case 9:
                eventHandler.netherAttack(player, donorName, likes);
                break;
            case 10:
                eventHandler.tntRain(player, donorName, plugin, likes);
                break;
            case 11:
                eventHandler.tpNetherOrOverworld(player, donorName, likes);
                break;
            case 12:
                eventHandler.createRaid(player, donorName, 15);
                break;
            case 13:
                eventHandler.startValuableItemRain(player, donorName, likes, plugin);
                break;
            case 14:
                eventHandler.oneHeart(player, plugin, donorName, likes);
                break;
            case 15:
                eventHandler.twentyHeart(player, plugin, donorName, likes);
                break;
            case 16:
                eventHandler.farmTime(player, donorName, likes);
                break;
            case 17:
                eventHandler.giveRegenPotion(player, donorName, likes);
                break;
            case 18:
                eventHandler.zombieInvasion(player, donorName, likes);
                break;
            case 19:
                eventHandler.anvilRain(player, donorName, plugin, likes);
                break;
            case 20:
                eventHandler.spawnRandomEntityWithNametag(player, donorName, likes);
                break;
            case 21:
                eventHandler.throwExpBottles(player, donorName, likes);
                break;
            case 22:
                eventHandler.randomTeleportPlayer(player, donorName, likes);
                break;
            case 23:
                eventHandler.itemSnack(player, donorName, likes);
                break;
            case 24:
                eventHandler.giveSlowPotion(player, donorName, likes);
                break;
            case 25:
                eventHandler.giveBlindnessPotion(player, donorName, likes);
                break;
            default:
                // Handle invalid input or default behavior
                break;
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
