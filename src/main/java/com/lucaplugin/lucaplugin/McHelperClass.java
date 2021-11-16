package com.lucaplugin.lucaplugin;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.Random;

public class McHelperClass
{
    public static void sayText(String name, String text2, ChatColor color1, ChatColor color2)
    {
        Bukkit.broadcastMessage(color1 + name + color2 + text2);
    }

    public static void sayTextSimple(String text)
    {
        Bukkit.broadcastMessage(text);
    }

    public static void playSound(Player player, Sound sound, float volume)
    {
        player.getWorld().playSound(player.getLocation(), sound, volume, 0.5F);
    }

    public static void stopTasks(Plugin plugin)
    {
        Bukkit.getServer().getScheduler().cancelTasks(plugin);
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

        for (int c = 0; c < 10; c++)
        {
            randomDouble = generateRandomDouble(0.0, 2.5);
            location = new Location(player.getWorld(), (double) spawnLocation.getX(), (double) spawnLocation.getY() + randomDouble, (double) spawnLocation.getZ());
            player.spawnParticle(particle, location, 30, dustOptions);
        }

        player.getWorld().spawnEntity(spawnLocation.add(0, 1, 0), entityType);
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
        int y = 255;
        while (player.getWorld().getBlockAt((int) location.getX(), y, (int) location.getZ()).getType() == Material.AIR)
        {
            System.out.println(player.getWorld().getBlockAt((int) location.getX(), y, (int) location.getZ()).getType());
            y--;
        }
        return new Location(player.getWorld(), location.getX(), y, location.getZ());
    }



    //Particle Effects

    //Displays Cone Effect on player (interval normaly set to 3)
    public static void coneEffect(final Player player, Plugin plugin, int timeInSeconds, int interval, ParticleEffect particleEffect)
    {
        new BukkitRunnable()
        {
            double time = 0;

            double phi = 0;

            public void run()
            {
                phi = phi + Math.PI / 8;
                double x, y, z;

                Location location1 = player.getLocation();
                for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16)
                {
                    for (double i = 0; i <= 1; i = i + 1)
                    {
                        x = 0.4 * (2 * Math.PI - t) * 0.5 * Math.cos(t + phi + i * Math.PI);
                        y = 0.5 * t;
                        z = 0.4 * (2 * Math.PI - t) * 0.5 * Math.sin(t + phi + i * Math.PI);
                        location1.add(x, y, z);
                        particleEffect.display(location1);
                        location1.subtract(x, y, z);
                    }

                }
                time += (double) interval/20;

                System.out.println(time);
                if (time > timeInSeconds)
                    cancel();
            }

        }.runTaskTimer(plugin, 0, interval);

    }
}
