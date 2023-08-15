package com.lucaplugin.lucaplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.NoteColor;
import xyz.xenondevs.particle.data.color.RegularColor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class eventHandler {
    public static boolean dirtOnFire = false;

    public static Location[] teamSpawnPoints = new Location[]{
            new Location(McHelperClass.getWorld(), 100, 100, 100),
            new Location(McHelperClass.getWorld(), 200, 200, 200),
            // ... and so on for each team
    };

    //Villager Raid on Raid
    public static void createVillagerCircle(Player player, String donorName, int raidAmount, int likes)
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
        McHelperClass.sayText(" has send " + likes + " likes, and spawned a Pillager Raid!", donorName, ChatColor.RED, ChatColor.WHITE);
    }



    //Adrenalin Rush = Health Regen + Jump | Speed | Blindness
    public static void adrenalinRush(Player player, String donorName, int likes)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

        switch (McHelperClass.generateRandomInt(0, 2))
        {
            case 0:
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 2));
                McHelperClass.playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 1);
                McHelperClass.sayText("Jump jump! ", donorName + " has send " + likes + " likes and gave you Jump power", ChatColor.BLUE, ChatColor.WHITE);
                break;
            case 1:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 2));
                McHelperClass.playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 8);
                McHelperClass.sayText("Wanna see some real speed?? ", donorName + " has send " + likes + " likes and gave you a Speed boost!", ChatColor.BLUE, ChatColor.WHITE);
                break;
            case 2:
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2));
                McHelperClass.playSoundXTimes(player, Sound.BLOCK_SAND_HIT, 5F, 15);
                McHelperClass.sayText("The little troll ", donorName + " has send " + likes + " likes and threw some sand in your eyes.. HA HA!", ChatColor.RED, ChatColor.RED);
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
    public void tntRain(Player player, String donorName, Plugin plugin, Integer likes) {
        McHelperClass.playSoundXTimes(player, Sound.ENTITY_CREEPER_PRIMED, 10F, 20);

        McHelperClass.sendBigText(donorName, "made it rain TNT!", "yellow", "white");

        McHelperClass.sayText(donorName, " has send " + likes + " and made it rain TNT!", ChatColor.GOLD, ChatColor.WHITE);
        int randomMax = McHelperClass.generateRandomInt(40, 50);
        for (int i = 0; i < randomMax; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation().add(McHelperClass.generateRandomInt(-20, 20), McHelperClass.generateRandomInt(10, 20), McHelperClass.generateRandomInt(-20, 20)), EntityType.PRIMED_TNT);
                    ((TNTPrimed) tnt).setFuseTicks(McHelperClass.generateRandomInt(50, 220));
                    McHelperClass.wait(50);
                }
            }.runTask(plugin);
        }
    }

    //First: Takes item if in hand
    public static void itemSnack(Player player, String donorName, Integer likes)
    {
        Inventory inventory = player.getInventory();
        ItemStack[] toolbarItems = new ItemStack[9]; // Assuming the first 9 slots are the toolbar

        for (int i = 0; i < 9; i++)
        {
            toolbarItems[i] = inventory.getItem(i);
        }

        boolean found = false;
        for(int i = 0; i < 9; i++){
            // Check if the slot contains an item before removing it
            if (toolbarItems[i] != null && toolbarItems[i].getType() != Material.AIR)
            {
                found = true;
                inventory.setItem(i, new ItemStack(Material.AIR));
                McHelperClass.sayText(donorName,  " has send " + likes + " likes and stole an item from your ToolBar!", ChatColor.GREEN, ChatColor.WHITE);
                return;
            }
        }

            McHelperClass.sayText(donorName,  " has send " + likes + " likes and saw that your toolbar is empty, here have a cookie!", ChatColor.GREEN, ChatColor.WHITE);
            inventory.setItem(0, new ItemStack(Material.COOKIE));
    }


    //Throws Exp Bottles on player
    public void throwExpBottles(Player player, String donorName, Integer likes)
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
        McHelperClass.sayText(donorName, " has send " + likes + " likes and gave " + fullExpSum + " experience to you!", ChatColor.GREEN, ChatColor.WHITE);
    }


    public void zombieInvasion (Player player, String donorName, Integer likes)
    {
        Location loc = player.getLocation();


        McHelperClass.sayText(donorName, " has send " + likes + " likes and spawn some unfriendly Guys!", ChatColor.GREEN, ChatColor.WHITE);
    }

    //A Thunder shoots in random position next to player
    public void createThunder(Player player, String donorName)
    {
        Location location = new Location(player.getWorld(), player.getLocation().getX() + McHelperClass.generateRandomInt(-10, 10), player.getLocation().getY() + McHelperClass.generateRandomInt(-10, 10), player.getLocation().getZ());
        Location fixedYLocation = McHelperClass.findNonBlockY(location, player);
        player.getWorld().strikeLightning(fixedYLocation);
        McHelperClass.sayText(donorName, " has invited his fans and brought a thunder! ", ChatColor.GREEN, ChatColor.WHITE);
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
        chicken.setCustomName(McHelperClass.randomColor() + donorName);
        chicken.setBaby();
        player.getWorld().playEffect(chicken.getLocation(), Effect.ANVIL_BREAK, 20);
        McHelperClass.sayText(donorName, " became a fan! And is now a chicken!", ChatColor.BLUE, ChatColor.WHITE);

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

    public void teleportTeams(Scoreboard scoreboard, Plugin plugin)
    {
        // Get all the teams
        Collection<Team> teams = scoreboard.getTeams();
        // Loop through each team
        int i = 0;
        System.out.println(teams.size());
        for (Team team : teams)
        {
            // Get the team's spawn point
            System.out.println(i);
            Location spawnPoint = teamSpawnPoints[i];
            // Check if the spawn point exists
            if (spawnPoint != null)
            {
                // Teleport all players in the team to the spawn point
                for (OfflinePlayer player : team.getPlayers())
                {
                    if (player.isOnline())
                    {
                        Player onlinePlayer = player.getPlayer();
                        McHelperClass.teleportPlayer(onlinePlayer, spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), plugin);
                    }
                }
            }
            i++;
        }
    }

    //Gives potion effect
    public static void givePotionEffect(Player player, String donorName, String text, ChatColor chatColor, Integer likes, PotionEffectType potionEffect, Integer duration, Integer amplifier) {
        player.addPotionEffect(new PotionEffect(potionEffect, duration, amplifier));
        McHelperClass.sayText(donorName, " has send " + likes + " likes and " + text, chatColor, ChatColor.WHITE);
    }

    //Gives slow potion effect
    public void giveSlowPotion(Player player, String donorName, int likes) {
        eventHandler.givePotionEffect(player, donorName , "made you gain 100 pounds", ChatColor.GREEN, likes,PotionEffectType.SLOW, 600, 2);
        player.playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 3, 10);
        player.spawnParticle(Particle.DRIPPING_HONEY, player.getLocation(), 350, 10, 10, 10, -0.0005);
    }

    //Gives blindness potion
    public void giveBlindnessPotion(Player player, String donorName, int likes) {
        eventHandler.givePotionEffect(player, donorName , "blew out the candles!", ChatColor.GREEN, likes,PotionEffectType.BLINDNESS, 600, 2);
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 3, 10);
        player.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 350, 10, 10, 10, -0.0005);
    }

    //Set down to 1 heart for 60sec
    public void oneHeart(Player player, Plugin plugin, String donorName, int likes) {
        double oldPlayerHealth = player.getHealth();
        player.setMaxHealth(2); // Set the player's maximum health to 1 heart (2 HP)
        McHelperClass.sayText(donorName, " has send " + likes + " likes and shortened your life for a minute", ChatColor.RED, ChatColor.WHITE);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setMaxHealth(20); // Restore the player's original max health
                player.setHealth(oldPlayerHealth); // Also restore their current health to avoid overhealing
            }
        }.runTaskLater(plugin, 1200); // 20 ticks per second, so 60 seconds = 1200 ticks
    }

    //Gives 20 Hearts for 2 Minutes
    public void twentyHeart(Player player, Plugin plugin, String donorName, int likes) {
        double oldPlayerHealth = player.getHealth();
        player.setMaxHealth(40);// Set the player's maximum health to 20 heart (40 HP)
        player.setHealth(40);
        McHelperClass.sayText(donorName, " has send " + likes + " likes doubled your Lifetime for 2 minutes", ChatColor.RED, ChatColor.WHITE);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setMaxHealth(20); // Restore the player's original max health
                player.setHealth(oldPlayerHealth); // Also restore their current health to avoid overhealing
            }
        }.runTaskLater(plugin, 2400); // 20 ticks per second, so 120 seconds = 2400 ticks
    }

    //TPs to nether if not in nether and vice versa
    public void tpNetherOrOverworld(Player player, String donorName, int likes) {
        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            // Player is in the Nether, teleport them to the Overworld
            tpWorld(player, donorName, likes, "world"); // Replace "world" with your actual Overworld name
        } else {
            // Player is in the Overworld, teleport them to the Nether
            tpWorld(player, donorName, likes, "world_nether"); // Replace "world_nether" with your actual Nether world name
        }
    }

    public void tpWorld(Player player, String donorName, int likes, String worldName) {
        Location to = player.getLocation();
        Location netherLocation = new Location(Bukkit.getWorld(worldName), to.getX(), to.getY(), to.getZ());
        player.teleport(netherLocation);
        Location fixedPosition = McHelperClass.findNonBlockY(player.getLocation(), player);
        player.teleport(fixedPosition);
    }


    //Gives health regen potion
    public void giveRegenPotion(Player player, String donorName, int likes) {
        eventHandler.givePotionEffect(player, donorName , "saves yo a$$", ChatColor.GREEN, likes,PotionEffectType.REGENERATION, 80, 2);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 3, 10);
        player.spawnParticle(Particle.CRIT_MAGIC, player.getLocation(), 350, 10, 10, 10, -0.0005);
    }

    //Creates a wolf companion
    public void createWolfCompanion(Player player, String donorName, Plugin plugin) {
        Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
        wolf.setTamed(true);
        wolf.setCustomName(McHelperClass.randomColor() + donorName);
        wolf.setCollarColor(McHelperClass.randomDyeColor());
        wolf.setOwner(player);
        McHelperClass.sayText(donorName, " has spawned your new best friend", ChatColor.GOLD, McHelperClass.randomColor());
    }

    //Teleport
    public void randomTeleportPlayer(Player player, String donorName, Integer likes)
    {
        Location newPosition = new Location(player.getWorld(), player.getLocation().getX() + McHelperClass.generateRandomInt(-100, 300), player.getLocation().getY(), player.getLocation().getZ() - McHelperClass.generateRandomInt(-100, 300));
        newPosition = McHelperClass.findNonBlockY(newPosition, player);
        player.teleport(newPosition);
        McHelperClass.sayText(donorName, " has send " + likes + " likes teleported you haha!", ChatColor.GREEN, ChatColor.WHITE);
    }

    //Make it rain anvils
    public void anvilRain(Player player, String donorName, Plugin plugin, Integer likes) {
        int timeInSeconds = 10;
        int interval = 10;

        McHelperClass.sayText(donorName, " has send " + likes + " likes and made it rain anvils", ChatColor.LIGHT_PURPLE, ChatColor.WHITE);

        new BukkitRunnable() {
            double time = 0;

            public void run() {
                Block block = player.getWorld().getBlockAt(player.getLocation().add(McHelperClass.generateRandomInt(-3, 3),
                        McHelperClass.generateRandomInt(3, 5),
                        McHelperClass.generateRandomInt(-3, 3)));
                block.setType(Material.ANVIL);

                time += (double) interval / 20;
                if (time > timeInSeconds)
                    cancel();

                McHelperClass.wait(McHelperClass.generateRandomInt(50, 200));
            }

        }.runTaskTimer(plugin, 0, interval);
    }

    public void test(Player player, String donorName, Plugin plugin) {

    }
}
