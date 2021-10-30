package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class onChatDistributor
{

    public static List<String> newFans = new ArrayList<String>();
    public static String lastMoment = "";

    public static void finalDistributor(String eventName, Player playerObj, String donorName)
    {
        switch (eventName)
        {
            case "invite":
                break;
            case "moment":
                break;
            case "raid":
                break;
            case "fan":
                break;
            default:
                System.out.println("Problem in switch for " + eventName);
        }
    }


    public static void triggerEventForChat(String jsonString, Player selectedUser, String userName, int userId)
    {
        if (selectedUser == null)
            return;

        JSONObject obj = new JSONObject(jsonString.replace("{event=onChat, data=", "").replace(", channel=public-channel_" + userId + "}", ""));

        JSONArray jsonArray = obj.getJSONObject("message").getJSONArray("comments");

        for (int i = 0; i < jsonArray.length(); i++)
        {
            String message = jsonArray.getJSONObject(i).getString("comment");
            String donorName = jsonArray.getJSONObject(i).getString("name");

            if (message.contains("I became a fan!") ||
                    message.contains("Ich bin Fan geworden!") ||
                    message.contains("Me he convertido en fan."))
            {
                if (!newFans.contains(userName))
                {
                    newFans.add(userName);
                    //Trigger here for fan
                    System.out.println("Triggered Fan");
                    finalDistributor("fan", selectedUser.getPlayer(), donorName);
                    return;
                }
            }

            //Also removed on subs for comments

            if (message.contains("captured a moment of") && jsonArray.getJSONObject(i).has("subscriptionData"))
            {
                if (Objects.equals(userName, lastMoment))
                    return;
                lastMoment = userName;

                //MOMENT TRIGGER
                System.out.println("Triggered Moment");
                finalDistributor("moment", selectedUser.getPlayer(), donorName);
                return;
            }

            if (message.contains("invited") && message.contains("fans to this broadcast.") ||
                    (message.contains("hat") && message.contains("zu diesem Broadcast eingeladen.")) ||
                    message.contains("he invitado a") && message.contains("fans a esta transmisión."))
            {
                if (message.indexOf(" ") == 0)
                {
                    //Trigger invite
                    System.out.println("Triggered Invite");
                    finalDistributor("invite", selectedUser.getPlayer(), donorName);
                    return;
                }
            }

            if (jsonArray.getJSONObject(i).has("textStyle"))
                if (message.contains("has raided the broadcast with") && jsonArray.getJSONObject(i).getInt("textStyle") == 1)
                {
                    System.out.println("Trigger raid");
                    finalDistributor("raid", selectedUser.getPlayer(), donorName);
                }

        }
    }
}
