package com.lucaplugin.lucaplugin;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class eventHandler
{
    //First: take item in hand
    public void itemSnack(Player player, String playerName)
    {
        sayText(playerName, " has send 50 Likes and snaked your item!", ChatColor.GREEN, ChatColor.WHITE);
        player.getInventory().remove(player.getInventory().getItemInMainHand());
    }

    //Throw exp bottles in front of me on inviting
    public void throwExpBottles(Player player, String playerName)
    {
        Location loc = player.getLocation();
        int fullExpSum = 0;
        for (int i = 0; i < 10; i++)
        {
            int setNewSum = generateRandomNumber(1, 3);
            player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3, 10);
            player.giveExp(setNewSum);
            player.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 350, 10, 10, 10, -0.0005);
            wait(generateRandomNumber(50, 150));
            fullExpSum += setNewSum;
        }
        sayText(playerName, " has send an invite with " + fullExpSum + " experience for you!", ChatColor.BLUE, ChatColor.WHITE);
    }


    //Capturing moments creates thunders 1 per person though
    public void createThunder(Player player, String playerName)
    {
        System.out.println(player.getLocation());
        Location test = new Location(player.getWorld(),player.getLocation().getX() + generateRandomNumber(-10,10),player.getLocation().getY() + generateRandomNumber(-10,10),player.getLocation().getZ());
        System.out.println(test);
        player.getWorld().strikeLightning(test);
        sayText(playerName, " has captured a moment and brought thunder! ", ChatColor.RED, ChatColor.WHITE);
    }


    //Becoming a fan, creates 1 Sheep with its name


    public void sayText(String name, String text2, ChatColor color1, ChatColor color2)
    {
        Bukkit.broadcastMessage(color1 + name + color2 + text2);
    }

    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        } catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    public static int generateRandomNumber(int min, int max)
    {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }


}
