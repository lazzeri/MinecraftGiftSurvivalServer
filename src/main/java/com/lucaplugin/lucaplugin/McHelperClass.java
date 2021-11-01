package com.lucaplugin.lucaplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Random;

public class McHelperClass
{
    public static void sayText(String name, String text2, ChatColor color1, ChatColor color2)
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

    public static int generateRandomInt(int min, int max)
    {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    public static double generateRandomDouble(double min, double max)
    {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public static void spawnEntityWithParticle(Player player, Particle particle, Particle.DustOptions dustOptions, EntityType entityType, int x, int z)
    {
        double randomDouble;
        Location spawnLocation = findNonBlockY(player.getLocation().add(x, 1, z), player);
        Location location;

        for (int c = 0; c < 30; c++)
        {
            randomDouble = generateRandomDouble(0.0, 2.5);
            location = new Location(player.getWorld(), (double) spawnLocation.getX(), (double) spawnLocation.getY() + randomDouble, (double) spawnLocation.getZ());
            player.spawnParticle(particle, location, 30, dustOptions);
        }

        player.getWorld().spawnEntity(spawnLocation, entityType);
    }

    public static void playSoundXTimes(Player player, Sound sound, Float volume, int amount)
    {
        for (int i = 0; i < amount; i++)
        {
            player.getWorld().playSound(player.getLocation(), sound, volume, 0.5F);
            wait(generateRandomInt(0, 140));
        }
    }

    public static Location findNonBlockY(Location location, Player player)
    {
        int adder = 0;

        while (true)
        {
            Block block = player.getWorld().getBlockAt((int) location.getX(), (int) location.getY() + adder, (int) location.getZ());
            if (block.isEmpty())
            {
                return block.getLocation();
            }
            adder++;
        }
    }
}
