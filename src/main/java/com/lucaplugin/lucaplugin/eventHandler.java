package com.lucaplugin.lucaplugin;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.NoteColor;
import xyz.xenondevs.particle.data.color.RegularColor;

public class eventHandler
{
    //Villager Raid on Raid
    public void createVillagerCircle(Player player, String donorName, int raidAmount)
    {
        double size = (double) raidAmount;
        int positions = (int) 360 / raidAmount;

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(48, 25, 52), 3.0F);
        for (int i = 0; i < 360; i += positions)
        {
            double angle = (i * Math.PI / 180);
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            //Creates spawner with particle and color of set particle
            McHelperClass.spawnEntityWithParticle(player, Particle.REDSTONE, dustOptions, EntityType.PILLAGER, (int) x, (int) z);
        }
        //Creates Redstone Particles in blue

        //Sets sound
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);


        //Set Message
        McHelperClass.sayText("OH GOD!! ITS A RAID FROM ", donorName, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE);
    }

    //Adrenalin Rush = Health Regen + Jump | Speed | Blindness
    public void adrenalinRush(Player player, String donorName)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

        switch (McHelperClass.generateRandomInt(0, 2))
        {
            case 0:
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 2));
                McHelperClass.playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 1);
                McHelperClass.sayText("Jump jump! ", donorName + " gave you Jump power", ChatColor.BLUE, ChatColor.WHITE);
                break;
            case 1:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 2));
                McHelperClass.playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 8);
                McHelperClass.sayText("Wanna see some real speed?? ", donorName + " gave you a Speed boost!", ChatColor.BLUE, ChatColor.WHITE);
                break;
            case 2:
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2));
                McHelperClass.playSoundXTimes(player, Sound.BLOCK_SAND_HIT, 5F, 15);
                McHelperClass.sayText("The little troll ", donorName + " threw some sand in your eyes.. HA HA!", ChatColor.RED, ChatColor.RED);
                break;
        }
    }

    //Notes
    public void magicNotes(Player player, String donorName, Plugin plugin, int interval)
    {

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < 50; i++)
                {
                    new ParticleBuilder(ParticleEffect.NOTE, player.getLocation())
                            .setParticleData(new NoteColor(1))
                            .setOffset(new Vector(5, 5, 5))
                            .setAmount(40)
                            .display();
                }

            }
        }.runTaskTimer(plugin, 0L, interval);

    }

    //TNT Rain
    public void tntRain(Player player, String donorName, Plugin plugin)
    {
        McHelperClass.playSoundXTimes(player, Sound.ENTITY_CREEPER_PRIMED, 10F, 20);
        McHelperClass.sayText(donorName, " made it rain TNT!", ChatColor.WHITE, ChatColor.RED);
        int randomMax = McHelperClass.generateRandomInt(40, 50);
        for (int i = 0; i < randomMax; i++)
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation().add(McHelperClass.generateRandomInt(-20, 20), McHelperClass.generateRandomInt(10, 20), McHelperClass.generateRandomInt(-20, 20)), EntityType.PRIMED_TNT);
                    ((TNTPrimed) tnt).setFuseTicks(McHelperClass.generateRandomInt(50, 220));
                    McHelperClass.wait(50);
                }
            }.runTask(plugin);
        }
    }

    //First: Takes item if in hand
    public void itemSnack(Player player, String playerName)
    {
        McHelperClass.sayText(playerName, " has send 50 Likes and snaked your item!", ChatColor.GREEN, ChatColor.WHITE);
        player.getInventory().remove(player.getInventory().getItemInMainHand());
    }

    //Throws Exp Bottles on player
    public void throwExpBottles(Player player, String playerName)
    {
        Location loc = player.getLocation();
        int fullExpSum = 0;
        for (int i = 0; i < 10; i++)
        {

            int setNewSum = McHelperClass.generateRandomInt(1, 3);
            player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3, 10);
            player.giveExp(setNewSum);
            player.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 350, 10, 10, 10, -0.0005);
            McHelperClass.wait(McHelperClass.generateRandomInt(50, 150));
            fullExpSum += setNewSum;
        }
        McHelperClass.sayText(playerName, " has send an invite with " + fullExpSum + " experience for you!", ChatColor.BLUE, ChatColor.WHITE);
    }

    //A Thunder shoots in random position next to player
    public void createThunder(Player player, String playerName)
    {
        Location test = new Location(player.getWorld(), player.getLocation().getX() + McHelperClass.generateRandomInt(-15, 15), player.getLocation().getY() + McHelperClass.generateRandomInt(-15, 15), player.getLocation().getZ());
        player.getWorld().strikeLightning(test);
        McHelperClass.sayText(playerName, " has captured a moment and brought thunder! ", ChatColor.RED, ChatColor.WHITE);
    }

    //Create a chicken companion
    public void makeChickenCompanion(Player player, String donorName, Plugin plugin)
    {
        for (int c = 0; c < 30; c++)
        {
            Location location = player.getLocation().add(2, 0, 1);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE, location, 100, 10, 10, 10, -0.0005);
        }

        Location chickenPosition = McHelperClass.findNonBlockY(player.getLocation().add(2, 3, 1), player);
        chickenPosition.add(0, 3, 0);

        Chicken chicken = (Chicken) player.getWorld().spawnEntity(chickenPosition, EntityType.CHICKEN);
        chicken.setCustomName(ChatColor.YELLOW + donorName);
        chicken.setBaby();
        player.getWorld().playEffect(chicken.getLocation(), Effect.ANVIL_BREAK, 20);
        chickenPermanentFollower(player, chicken, plugin);
    }

    //Task for teleporting him every x seconds
    public void chickenPermanentFollower(Player player, Animals entity, Plugin plugin)
    {
        BukkitTask task = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!entity.isValid())
                    cancel();

                entity.teleport(player.getLocation());
                entity.setTarget(player);

            }
        }.runTaskTimer(plugin, 10 * 12, 20 * 12);
    }

    //Creates a wolf companion
    public void createWolfCompanion(Player player, String donorName, Plugin plugin)
    {
        Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
        wolf.setTamed(true);
        wolf.setCustomName(McHelperClass.randomColor() + donorName);
        wolf.setCollarColor(McHelperClass.randomDyeColor());
        wolf.setOwner(player);
        McHelperClass.sayText(donorName, " has spawned your new best friend", McHelperClass.randomColor(), McHelperClass.randomColor());
    }


    public void test(Player player, String donorName, Plugin plugin)
    {

    }
}
