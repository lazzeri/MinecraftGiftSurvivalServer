package com.lucaplugin.lucaplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class eventHandler
{
    //First: take item in hand
    public void itemSnack(Player player, String playerName)
    {
        sayText(playerName," has send 50 Likes and snaked your item!",ChatColor.GREEN,ChatColor.WHITE);
        player.getInventory().remove(player.getInventory().getItemInMainHand());
    }


    //Throw exp bottles in front of me on inviting
    //Capturing moments creates thunders 1 per person though
    //Becoming a fan, creates 1 Sheep with its name


    public void sayText(String name, String text2, ChatColor color1, ChatColor color2)
    {
        Bukkit.broadcastMessage(color1 + name + color2 + text2);
    }
}
