package com.lucaplugin.lucaplugin.util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class McUtils {

    private static final Random random = new Random();

    public static void sendConsoleCommand(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void spawnParticle(Player player, int amount, int interval, Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < amount; i++) {
                    new ParticleBuilder(ParticleEffect.DRIP_LAVA, player.getLocation()).display();
                }
            }
        }.runTaskTimer(plugin, 0, interval);
    }

    public static World getWorld() {
        return Bukkit.getWorld("world");
    }

    public static ChatColor randomColor() {
        int randomInt = generateRandomInt(0, 21);
        int i = 0;
        for (ChatColor chatcolor : ChatColor.values()) {
            if (randomInt == i) return chatcolor;
            i++;
        }
        return ChatColor.WHITE;
    }

    public static DyeColor randomDyeColor() {
        int randomInt = generateRandomInt(0, 15);
        int i = 0;
        for (DyeColor dyeColor : DyeColor.values()) {
            if (randomInt == i) return dyeColor;
            i++;
        }
        return DyeColor.WHITE;
    }

    public static void sayText(String name, String text2, ChatColor color1, ChatColor color2) {
        Bukkit.broadcastMessage(color1 + name + color2 + text2);
    }

    public static void sayTextSimple(String text) {
        Bukkit.broadcastMessage(text);
    }

    public static void playSound(Player player, Sound sound, float volume) {
        player.getWorld().playSound(player.getLocation(), sound, volume, 0.5F);
    }

    public static void stopTasks(Plugin plugin) {
        Bukkit.getServer().getScheduler().cancelTasks(plugin);
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static int generateRandomInt(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    public static double generateRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static void spawnEntityWithParticle(Player player, Particle particle, 
            Particle.DustOptions dustOptions, EntityType entityType, int x, int z) {
        Location spawnLocation = findNonBlockY(player.getLocation().add(x, 1, z), player);
        double randomDouble = generateRandomDouble(0.0, 2.5);
        Location location = new Location(player.getWorld(), spawnLocation.getX(), 
                spawnLocation.getY() + randomDouble, spawnLocation.getZ());
        player.spawnParticle(particle, location, 30, dustOptions);
        player.getWorld().spawnEntity(location, entityType);
    }

    public static EntityType getRandomEntityType(EntityType[] entityTypes) {
        if (entityTypes.length == 0) return EntityType.BAT;
        return entityTypes[random.nextInt(entityTypes.length)];
    }

    public static void playSoundXTimes(Player player, Sound sound, Float volume, int amount) {
        for (int i = 0; i < amount; i++) {
            player.getWorld().playSound(player.getLocation(), sound, volume, 0.5F);
            wait(generateRandomInt(0, 140));
        }
    }

    public static Location findNonBlockY(Location location, Player player) {
        int x = (int) Math.round(location.getX());
        int y = (int) Math.round(location.getY());
        int z = (int) Math.round(location.getZ());
        while (player.getWorld().getBlockAt(x, y, z).getType() == Material.AIR || 
               player.getWorld().getBlockAt(x, y, z).getType() == Material.LAVA) {
            if (player.getWorld().getBlockAt(x, y, z).getType() == Material.LAVA) {
                x += 10; z += 10; y = 100;
            }
            y--;
        }
        return new Location(player.getWorld(), Math.round(x), y + 1, Math.round(z));
    }

    public static Location findNonBlockYFromTop(Location location, Player player) {
        int x = (int) Math.round(location.getX());
        int y = 155;
        int z = (int) Math.round(location.getZ());
        while (player.getWorld().getBlockAt(x, y, z).getType() == Material.AIR ||
               player.getWorld().getBlockAt(x, y, z).getType() == Material.LAVA) {
            if (player.getWorld().getBlockAt(x, y, z).getType() == Material.LAVA) {
                x += 10; z += 10; y = 100;
            }
            y--;
        }
        return new Location(player.getWorld(), Math.round(x), y + 1, Math.round(z));
    }

    public static void circleEffect(final Player player, Plugin plugin, int timeInSeconds,
            int interval, ParticleEffect particleEffect) {
        new BukkitRunnable() {
            double time = 0;
            double phi = 0;
            public void run() {
                phi = phi + Math.PI / 8;
                Location location1 = player.getLocation();
                for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
                    for (double i = 0; i <= 1; i = i + 1) {
                        double x = 0.4 * (2 * Math.PI - t) * 0.5 * Math.cos(t + phi + i * Math.PI);
                        double y = 0.5 * t;
                        double z = 0.4 * (2 * Math.PI - t) * 0.5 * Math.sin(t + phi + i * Math.PI);
                        location1.add(x, y, z);
                        particleEffect.display(location1);
                        location1.subtract(x, y, z);
                    }
                }
                time += (double) interval / 20;
                if (time > timeInSeconds) cancel();
            }
        }.runTaskTimer(plugin, 0, interval);
    }

    public static void coneEffect(final Player player, Plugin plugin, int timeInSeconds,
            int interval, ParticleEffect particleEffect) {
        new BukkitRunnable() {
            double time = 0;
            double phi = 0;
            public void run() {
                phi = phi + Math.PI / 8;
                Location location1 = player.getLocation();
                for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
                    for (double i = 0; i <= 1; i = i + 1) {
                        double x = 0.4 * (2 * Math.PI - t) * 0.5 * Math.cos(t + phi + i * Math.PI);
                        double y = 0.5 * t;
                        double z = 0.4 * (2 * Math.PI - t) * 0.5 * Math.sin(t + phi + i * Math.PI);
                        location1.add(x, y, z);
                        particleEffect.display(location1);
                        location1.subtract(x, y, z);
                    }
                }
                time += (double) interval / 20;
                if (time > timeInSeconds) cancel();
            }
        }.runTaskTimer(plugin, 0, interval);
    }

    public static void teleportPlayer(Player player, double x, double y, double z, Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(new Location(player.getWorld(), x, y, z));
            }
        }.runTask(plugin);
    }

    public static void sendBigText(String title, String subtitle, String titleColor, String subtitleColor) {
        try {
            sendConsoleCommand(String.format(
                "title @a title {\"text\":\"%s\", \"bold\":true, \"italic\":true, \"color\":\"%s\"}",
                title, titleColor));
            sendConsoleCommand(String.format(
                "title @a subtitle {\"text\":\"%s\", \"bold\":true, \"italic\":true, \"color\":\"%s\"}",
                subtitle, subtitleColor));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

