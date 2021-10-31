package com.lucaplugin.lucaplugin;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.block.Block;

import java.util.Random;

public class eventHandler
{
    public void createVillagerCircle(Player player, String donorName)
    {
        double size = 7;

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(48, 25, 52), 3.0F);
        for (int i = 0; i < 360; i += 70)
        {
            double angle = (i * Math.PI / 180);
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            //Creates spawner with particle and color of set particle
            spawnEntityWithParticle(player, Particle.REDSTONE, dustOptions, EntityType.PILLAGER, (int) x, (int) z);
        }
        //Creates Redstone Particles in blue

        //Sets sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PILLAGER_AMBIENT, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PILLAGER_AMBIENT, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PILLAGER_AMBIENT, 5.0F, 0.5F);

        //Set Message
        sayText("OH GOD!! ITS A RAID FROM ", donorName, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE);
    }

    public void adrenalinRush(Player player, String donorName)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

        switch (generateRandomInt(0, 2))
        {
            case 0:
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 2));
                playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 1);
                sayText("The new Uku Member ", donorName + " gave you Jump power", ChatColor.BLUE, ChatColor.WHITE);
                break;
            case 1:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 2));
                playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 8);
                sayText("The new Crew Member ", donorName + " gave you a Speed boost!", ChatColor.BLUE, ChatColor.WHITE);
                break;
            case 2:
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2));
                playSoundXTimes(player, Sound.BLOCK_SAND_HIT, 5F, 15);
                sayText("The little troll ", donorName + " threw some sand in your eyes.. HA HA!", ChatColor.RED, ChatColor.RED);
                break;
        }
    }

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
            int setNewSum = generateRandomInt(1, 3);
            player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3, 10);
            player.giveExp(setNewSum);
            player.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 350, 10, 10, 10, -0.0005);
            wait(generateRandomInt(50, 150));
            fullExpSum += setNewSum;
        }
        sayText(playerName, " has send an invite with " + fullExpSum + " experience for you!", ChatColor.BLUE, ChatColor.WHITE);
    }

    //Capturing moments creates thunders 1 per person though
    public void createThunder(Player player, String playerName)
    {
        Location test = new Location(player.getWorld(), player.getLocation().getX() + generateRandomInt(-10, 10), player.getLocation().getY() + generateRandomInt(-10, 10), player.getLocation().getZ());
        player.getWorld().strikeLightning(test);
        sayText(playerName, " has captured a moment and brought thunder! ", ChatColor.RED, ChatColor.WHITE);
    }

    public void makeChickenCompanion(Player player, String donorName, Plugin plugin)
    {
        for (int c = 0; c < 30; c++)
        {
            Location location = player.getLocation().add(2, 0, 1);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE, location, 100, 10, 10, 10, -0.0005);
        }

        Location chickenPosition = findNonBlockY(player.getLocation().add(2, 3, 1), player);
        Chicken chicken = (Chicken) player.getWorld().spawnEntity(chickenPosition, EntityType.CHICKEN);
        chicken.setCustomName(ChatColor.YELLOW + donorName);
        chicken.setBaby();
        player.getWorld().playEffect(chicken.getLocation(), Effect.ANVIL_BREAK, 20);
        chickenPermanentFollower(player, chicken, plugin);
    }

    public void chickenPermanentFollower(Player player, Chicken entity, Plugin plugin)
    {
        BukkitTask task = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                System.out.println("Triggered");

                if (!entity.isValid())
                    cancel();

                entity.teleport(player.getLocation().add(generateRandomInt(0, 5), 0, generateRandomInt(0, 5)));
                entity.setTarget(player);

            }
        }.runTaskTimer(plugin, 10 * 12, 20 * 12);
    }

    //-------------------------------------------------------- HELPERS
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
            location = new Location(player.getWorld(),(double)spawnLocation.getX(), (double) spawnLocation.getY() + randomDouble,(double) spawnLocation.getZ());
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
