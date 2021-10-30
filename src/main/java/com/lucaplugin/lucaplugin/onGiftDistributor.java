package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

public class onGiftDistributor
{

    public static void finalDistributor(String skuName, Player selectedUser, String donorName)
    {
        switch (skuName)
        {
            case "50_LIKES_2":
                //Here Comes the command then in
                break;
        }

    }



    public static void triggerEventForGift(String eventInString, Player selectedUser,int userId)
    {
        if(selectedUser == null)
            return;

        JSONObject obj = new JSONObject(eventInString.replace("{event=onGift, data=", "").replace(", channel=public-channel_"+userId+"}", ""));
        JSONArray jsonArray = obj.getJSONObject("message").getJSONArray("stageGifts");

        for (int i = 0; i < jsonArray.length(); i++)
        {
            //Trigger here objects for gifts
            String skuName = jsonArray.getJSONObject(i).getString("SKU");
            String donorName = jsonArray.getJSONObject(i).getString("name");
            finalDistributor(skuName,selectedUser,donorName);
        }
    }

}
