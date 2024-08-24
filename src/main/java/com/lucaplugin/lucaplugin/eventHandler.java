package com.lucaplugin.lucaplugin;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
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

import java.util.*;

public class eventHandler
{
    public static boolean dirtOnFire = false;

    public static Location[] teamSpawnPoints = new Location[]{
            new Location(McHelperClass.getWorld(), 100, 100, 100),
            new Location(McHelperClass.getWorld(), 200, 200, 200),
            // ... and so on for each team
    };

    public static void spawnWithers(Player player, String donorName, int likes)
    {
        World world = player.getWorld();
        Random random = new Random();

        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize();

        for (int i = 0; i < 3; i++)
        {
            double xOffset = direction.getX() * (i + 1) * 3;
            double yOffset = 1.5; // Adjust the height as needed
            double zOffset = direction.getZ() * (i + 1) * 3;

            Location spawnLocation = playerLocation.clone().add(xOffset, yOffset, zOffset);
            Wither wither = (Wither) world.spawnEntity(spawnLocation, EntityType.WITHER);

            // Add lightning particle where the wither spawns
            world.strikeLightningEffect(spawnLocation);

            ChatColor randomColor = ChatColor.values()[random.nextInt(ChatColor.values().length)];
            wither.setCustomName(randomColor + donorName);
            wither.setCustomNameVisible(true);
        }

        McHelperClass.sendBigText(donorName, "spawned WITHERS!", "yellow", "white");
        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CAVE, 5.0F, 0.5F);
        McHelperClass.sayText(donorName, " has send " + likes + " likes and spawned WITHERS!", ChatColor.YELLOW, ChatColor.WHITE);

    }


    //Villager Raid on Raid
    public static void createRaid(Player player, String donorName, int likes)
    {
        //Creates Redstone Particles in blue
        EntityType[] entityTypes = {EntityType.PILLAGER, EntityType.RAVAGER, EntityType.EVOKER, EntityType.VINDICATOR};

        createEntityAttack(
                player,
                donorName,
                likes,
                25,
                153,
                11,
                163,
                3.0F,
                ChatColor.RED,
                "spawned a Pillager Raid!",
                false,
                "gold",
                entityTypes
        );

        //Sets sound
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
    }


    //Adrenalin Rush = Health Regen + Jump | Speed | Blindness
    public static void adrenalinRush(Player player, String donorName, int likes)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

        switch (McHelperClass.generateRandomInt(0, 2))
        {
            case 0:
                McHelperClass.playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 1);
                eventHandler.givePotionEffect(player, donorName, " has send " + likes + " likes and gave you Jump power!", ChatColor.GREEN, likes, PotionEffectType.JUMP, 1200, 2);
                break;
            case 1:
                McHelperClass.playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 8);
                eventHandler.givePotionEffect(player, donorName, " has send " + likes + " likes and gave you a Speed boost!", ChatColor.GREEN, likes, PotionEffectType.SPEED, 1200, 2);
                break;
            case 2:
                McHelperClass.playSoundXTimes(player, Sound.BLOCK_SAND_HIT, 5F, 15);
                eventHandler.givePotionEffect(player, donorName, " has send " + likes + " likes and threw some sand in your eyes.. HA HA", ChatColor.GREEN, likes, PotionEffectType.BLINDNESS, 600, 2);
                break;
        }
    }

    //Notes
    public static void magicNotes(Player player, String donorName, Plugin plugin, int interval)
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
    public static void tntRain(Player player, String donorName, Plugin plugin, Integer likes)
    {
        McHelperClass.playSoundXTimes(player, Sound.ENTITY_CREEPER_PRIMED, 10F, 20);

        McHelperClass.sendBigText(donorName, "made it rain TNT!", "yellow", "white");

        McHelperClass.sayText(donorName, " has send " + likes + " likes and made it rain TNT!", ChatColor.GOLD, ChatColor.WHITE);
        int randomMax = McHelperClass.generateRandomInt(40, 50);
        for (int i = 0; i < randomMax; i++)
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation().add(McHelperClass.generateRandomInt(-20, 20), McHelperClass.generateRandomInt(5, 15), McHelperClass.generateRandomInt(-20, 20)), EntityType.PRIMED_TNT);
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
        for (int i = 0; i < 9; i++)
        {
            // Check if the slot contains an item before removing it
            if (toolbarItems[i] != null && toolbarItems[i].getType() != Material.AIR)
            {
                found = true;
                inventory.setItem(i, new ItemStack(Material.AIR));
                McHelperClass.sayText(donorName, " has send " + likes + " likes and stole an item from your ToolBar!", ChatColor.GREEN, ChatColor.WHITE);
                return;
            }
        }

        McHelperClass.sayText(donorName, " has send " + likes + " likes and saw that your toolbar is empty, here have a cookie!", ChatColor.GREEN, ChatColor.WHITE);
        inventory.setItem(0, new ItemStack(Material.COOKIE));
    }


    //Throws Exp Bottles on player
    public static void throwExpBottles(Player player, String donorName, Integer likes)
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

    public static void spawnEnchantedDiamondArmorStandInFrontOfPlayer(Player player, String donorName, int likes)
    {
        Location playerLocation = player.getLocation();
        Location spawnLocation = playerLocation.add(playerLocation.getDirection().multiply(2)); // Adjust the distance as needed
        spawnLocation.setY(spawnLocation.getY() + 1);

        ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setInvulnerable(true);
        armorStand.setBasePlate(false);

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        helmet.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 4);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.setDisplayName(ChatColor.GOLD + donorName + "'s Helmet");
        helmet.setItemMeta(helmetMeta);

        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        chestplateMeta.setDisplayName(ChatColor.GOLD + donorName + "'s Chestplate");
        chestplate.setItemMeta(chestplateMeta);

        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        ItemMeta leggingsMeta = leggings.getItemMeta();
        leggingsMeta.setDisplayName(ChatColor.GOLD + donorName + "'s Leggings");
        leggings.setItemMeta(leggingsMeta);

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 4);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.setDisplayName(ChatColor.GOLD + donorName + "'s Boots");
        boots.setItemMeta(bootsMeta);

        armorStand.getEquipment().setHelmet(helmet);
        armorStand.getEquipment().setChestplate(chestplate);
        armorStand.getEquipment().setLeggings(leggings);
        armorStand.getEquipment().setBoots(boots);

        player.spawnParticle(Particle.EXPLOSION_NORMAL, spawnLocation, 50, 0, 0, 0, 0);
        player.playSound(spawnLocation, Sound.BLOCK_SMITHING_TABLE_USE, 3, 10);

        String message = " has sent " + likes + " likes and gifted full Diamond Armor!";
        McHelperClass.sayText(donorName, message, ChatColor.GOLD, ChatColor.WHITE);
        McHelperClass.sendBigText(donorName, "gifted full Diamond Armor!", "gold", "white");

    }

    public static void spawnRandomEntityWithNametag(Player player, String donorName, int likes)
    {
        EntityType[] mobTypes =
                {
                        EntityType.BAT,
                        EntityType.BLAZE,
                        EntityType.CAT,
                        EntityType.CAVE_SPIDER,
                        EntityType.CHICKEN,
                        EntityType.COD,
                        EntityType.COW,
                        EntityType.CREEPER,
                        EntityType.DOLPHIN,
                        EntityType.DONKEY,
                        EntityType.DROWNED,
                        EntityType.ELDER_GUARDIAN,
                        EntityType.ENDERMAN,
                        EntityType.ENDERMITE,
                        EntityType.EVOKER,
                        EntityType.FOX,
                        EntityType.GHAST,
                        EntityType.GIANT,
                        EntityType.GUARDIAN,
                        EntityType.HOGLIN,
                        EntityType.HORSE,
                        EntityType.HUSK,
                        EntityType.ILLUSIONER,
                        EntityType.IRON_GOLEM,
                        EntityType.LLAMA,
                        EntityType.MAGMA_CUBE,
                        EntityType.MULE,
                        EntityType.OCELOT,
                        EntityType.PANDA,
                        EntityType.PARROT,
                        EntityType.PHANTOM,
                        EntityType.PIG,
                        EntityType.PIGLIN,
                        EntityType.PIGLIN_BRUTE,
                        EntityType.PILLAGER,
                        EntityType.POLAR_BEAR,
                        EntityType.PUFFERFISH,
                        EntityType.RABBIT,
                        EntityType.RAVAGER,
                        EntityType.SALMON,
                        EntityType.SHEEP,
                        EntityType.SHULKER,
                        EntityType.SILVERFISH,
                        EntityType.SKELETON,
                        EntityType.SKELETON_HORSE,
                        EntityType.SLIME,
                        EntityType.SNOWMAN,
                        EntityType.SPIDER,
                        EntityType.SQUID,
                        EntityType.STRAY,
                        EntityType.STRIDER,
                        EntityType.TRADER_LLAMA,
                        EntityType.TROPICAL_FISH,
                        EntityType.TURTLE,
                        EntityType.VEX,
                        EntityType.VILLAGER,
                        EntityType.VINDICATOR,
                        EntityType.WANDERING_TRADER,
                        EntityType.WITCH,
                        EntityType.WITHER,
                        EntityType.WITHER_SKELETON,
                        EntityType.WOLF,
                        EntityType.ZOGLIN,
                        EntityType.ZOMBIE,
                        EntityType.ZOMBIE_HORSE,
                        EntityType.ZOMBIE_VILLAGER,
                        EntityType.ZOMBIFIED_PIGLIN
                };


        Location spawnLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        spawnLocation.setY(spawnLocation.getY() + 2);
        EntityType randomEntityType = mobTypes[new Random().nextInt(mobTypes.length)];
        Entity spawnedEntity = player.getWorld().spawnEntity(spawnLocation, randomEntityType);

        String message = " has sent " + likes + " likes and spawned " + spawnedEntity.getName() + "!";
        McHelperClass.sayText(donorName, message, ChatColor.GREEN, ChatColor.WHITE);
        // Set custom name tag
        if (spawnedEntity instanceof LivingEntity)
        {
            LivingEntity livingEntity = (LivingEntity) spawnedEntity;
            livingEntity.setCustomNameVisible(true);
            livingEntity.setCustomName(McHelperClass.randomColor() + donorName);
        }
    }


    public static void elytraAndRockets(Player player, String donorName, int likes)
    {
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        ItemStack rockets = new ItemStack(Material.FIREWORK_ROCKET, 64);

        Location dropLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2)); // Adjust the distance as needed
        dropLocation.setY(dropLocation.getY() + 1);
        player.getWorld().dropItemNaturally(dropLocation, elytra);
        player.getWorld().dropItemNaturally(dropLocation, rockets);

        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.0F);

        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, dropLocation, 50);

        String message = " has sent " + likes + " likes and gifted Elytra with Rockets!";
        McHelperClass.sayText(donorName, message, ChatColor.GOLD, ChatColor.WHITE);
        McHelperClass.sendBigText(donorName, "gifted Elytra with Rockets!", "gold", "white");
    }


    public static void createEntityAttack(Player player, String donorName, int likes, int eventAmount, int rgb1, int rgb2, int rgb3, float size2, ChatColor normalColor, String text, boolean isSuperMessage, String superMessageColor, EntityType[] entityTypes)
    {
        double size = (double) 10;
        int positions = (int) 360 / eventAmount;
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(rgb1, rgb2, rgb3), size2);

        Random random = new Random();

        for (int i = 0; i < 360; i += positions)
        {
            double angle = (i * Math.PI / 180);
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            EntityType randomEntityType = entityTypes[random.nextInt(entityTypes.length)];

            McHelperClass.spawnEntityWithParticle(player, Particle.REDSTONE, dustOptions, randomEntityType, (int) x, (int) z);
        }

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);

        String message = " has sent " + likes + " likes and " + text;
        if (isSuperMessage)
        {
            McHelperClass.sendBigText(donorName, text, superMessageColor, "white");
        }

        McHelperClass.sayText(donorName, message, normalColor, ChatColor.WHITE);

    }

    static void startValuableItemRain(Player player, String donorName, int likes, Plugin plugin)
    {
        Location location = player.getLocation();
        location.setY(location.getY() + 5);
        McHelperClass.sayText(donorName, " has send " + likes + " likes and made it rain ores!", ChatColor.RED, ChatColor.WHITE);

        new BukkitRunnable()
        {
            final List<Material> valuableItems = Arrays.asList(
                    Material.DIAMOND,
                    Material.EMERALD,
                    Material.IRON_INGOT,
                    Material.COAL,
                    Material.GOLD_INGOT
            );
            int remainingDrops = 100; // Number of valuable items to drop
            Random random = new Random();

            World world = player.getWorld();

            @Override
            public void run()
            {
                if (remainingDrops > 0)
                {

                    Material randomItem = valuableItems.get(random.nextInt(valuableItems.size()));
                    ItemStack itemStack = new ItemStack(randomItem);
                    double xOffset = random.nextDouble() * 2 - 1; // Randomize x by +/- 1
                    double zOffset = random.nextDouble() * 2 - 1; // Randomize y by +/- 1
                    world.spawnParticle(Particle.CLOUD, location, 10, xOffset, 0, zOffset, 0);

                    world.dropItem(location, itemStack);
                    remainingDrops--;
                } else
                {
                    this.cancel(); // Stop the task when all items are dropped
                }
            }
        }.runTaskTimer(plugin, 0L, 1L); // Delay and interval between drops
    }

    public static void createSkeletonRiders(Player player, String donorName, int likes, int eventAmount, int rgb1, int rgb2, int rgb3, float size2, Plugin plugin)
    {

        if (player.getWorld().getEnvironment() == World.Environment.NETHER)
        {
            tpNetherOrOverworld(player, donorName, likes);
            return;
        }
        double size = (double) 10;
        int positions = (int) 360 / eventAmount;
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(rgb1, rgb2, rgb3), size2);

        for (int i = 0; i < 360; i += positions)
        {
            double angle = (i * Math.PI / 180);
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "summon skeleton_horse " + x + " " + player.getLocation().getY() + " " + z + " " + "{SkeletonTrap:1}");
        }

        player.getWorld().setTime(12000);
        player.getWorld().setStorm(true);
        player.getWorld().setThundering(false);
        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CAVE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CAVE, 5.0F, 0.5F);
        String message = donorName + " has sent " + likes + " likes and spawned the 4 Horsemen! Or a couple more...";
        McHelperClass.sayText(message, donorName, ChatColor.RED, ChatColor.WHITE);

        McHelperClass.sendBigText(donorName, "spawned the 4 Horsemen!", "yellow", "white");
    }

    public static void netherAttack(Player player, String donorName, int likes)
    {
        player.getWorld().setTime(12000);
        EntityType[] entityTypes = {EntityType.WITHER_SKELETON, EntityType.SKELETON, EntityType.BLAZE, EntityType.SKELETON};
        createEntityAttack(
                player,
                donorName,
                likes,
                20,
                255,
                0,
                0,
                3.0F,
                ChatColor.GOLD,
                "spawned the living HELL!",
                true,
                "gold",
                entityTypes
        );
    }


    public static void loadedCreeperAttack(Player player, String donorName, int likes)
    {
        double size = 10.0; // Using decimal to indicate a double value
        int eventAmount = 25;
        int positions = 360 / eventAmount;

        Random random = new Random();

        for (int i = 0; i < 360; i += positions)
        {
            double angle = i * Math.PI / 180.0;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location spawnLocation = player.getLocation().clone().add(x, 0, z);
            spawnLocation = McHelperClass.findNonBlockY(spawnLocation, player);
            spawnLocation.setY(spawnLocation.getY() + 4.0);


            Creeper creeper = (Creeper) player.getWorld().spawnEntity(spawnLocation, EntityType.CREEPER);

            if (random.nextBoolean())
            {
                creeper.setPowered(true);
            }

            player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, spawnLocation, 50);
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5.0F, 0.5F);

        String message = " has sent " + likes + " likes and spawned Creepers!";
        McHelperClass.sayText(donorName, message, ChatColor.RED, ChatColor.WHITE);
        McHelperClass.sendBigText(donorName, "spawned Creepers!", "yellow", "white");
    }


    public static void zombieInvasion(Player player, String donorName, int likes)
    {
        player.getWorld().setTime(12000);
        EntityType[] entityTypes = {EntityType.GIANT, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE_HORSE};
        createEntityAttack(
                player,
                donorName,
                likes,
                40,
                255,
                0,
                0,
                3.0F,
                ChatColor.GOLD,
                "spawned a Zombie Wave!",
                true,
                "gold",
                entityTypes
        );
    }

    public static void farmTime(Player player, String donorName, int likes)
    {
        EntityType[] entityTypes = {EntityType.COW, EntityType.CHICKEN, EntityType.HORSE, EntityType.PIG, EntityType.DONKEY, EntityType.PANDA, EntityType.LLAMA};
        createEntityAttack(
                player,
                donorName,
                likes,
                25,
                220,
                170,
                255,
                3.0F,
                ChatColor.LIGHT_PURPLE,
                "spawned some friendly guys!",
                false,
                "gold",
                entityTypes
        );
    }


    //A Thunder shoots in random position next to player
    public static void createThunder(Player player, String donorName)
    {
        Location location = new Location(player.getWorld(), player.getLocation().getX() + McHelperClass.generateRandomInt(0, 5), player.getLocation().getY() + McHelperClass.generateRandomInt(-10, 10), player.getLocation().getZ() + McHelperClass.generateRandomInt(0, 5));
        Location fixedYLocation = McHelperClass.findNonBlockY(location, player);
        player.getWorld().strikeLightning(fixedYLocation);
        McHelperClass.sayText(donorName, " has invited his fans and brought a thunder! ", ChatColor.GREEN, ChatColor.WHITE);
    }

    //Create a chicken companion
    public static void makeChickenCompanion(Player player, String donorName, Plugin plugin)
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
    public static void chickenPermanentFollower(Player player, Animals entity, Plugin plugin)
    {
        BukkitTask task = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!entity.isValid())
                    this.cancel();

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
    public static void givePotionEffect(Player player, String donorName, String text, ChatColor chatColor, Integer likes, PotionEffectType potionEffect, Integer duration, Integer amplifier)
    {
        player.addPotionEffect(new PotionEffect(potionEffect, duration, amplifier));
        McHelperClass.sayText(donorName, text, chatColor, ChatColor.WHITE);
    }

    //Gives slow potion effect
    public static void giveSlowPotion(Player player, String donorName, int likes)
    {
        eventHandler.givePotionEffect(player, donorName, " has send " + likes + " likes and made you gain 100 pounds", ChatColor.GREEN, likes, PotionEffectType.SLOW, 600, 2);
        player.playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 3, 10);
        player.spawnParticle(Particle.DRIPPING_HONEY, player.getLocation(), 350, 10, 10, 10, -0.0005);
    }


    //Set down to 1 heart for 60sec
    public static void oneHeart(Player player, Plugin plugin, String donorName, int likes)
    {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        attribute.setBaseValue(2.0D);
        McHelperClass.sayText(donorName, " has send " + likes + " likes and put you on 1 heart for a minute!", ChatColor.RED, ChatColor.WHITE);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                McHelperClass.sayText("Your hearts are back to normal!", "", ChatColor.RED, ChatColor.WHITE);
                AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attribute.setBaseValue(20.0D);
                this.cancel();
            }
        }.runTaskLater(plugin, 1200); // 20 ticks per second, so 60 seconds = 1200 ticks
    }

    //Gives 20 Hearts for 2 Minutes
    public static void twentyHeart(Player player, Plugin plugin, String donorName, int likes)
    {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        attribute.setBaseValue(40.0D);
        McHelperClass.sayText(donorName, " has send " + likes + " likes and gave you 20 hearts for 2 minutes", ChatColor.RED, ChatColor.WHITE);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                McHelperClass.sayText("Your hearts are back to normal!", "", ChatColor.RED, ChatColor.WHITE);
                AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attribute.setBaseValue(20.0D);
                this.cancel();
            }
        }.runTaskLater(plugin, 2400); // 20 ticks per second, so 120 seconds = 2400 ticks
    }

    //TPs to nether if< not in nether and vice versa
    public static void tpNetherOrOverworld(Player player, String donorName, int likes)
    {
        if (player.getWorld().getEnvironment() == World.Environment.NETHER)
        {
            // Player is in the Nether, teleport them to the Overworld
            tpWorld(player, donorName, likes, "world"); // Replace "world" with your actual Overworld name
            McHelperClass.sayText(donorName, " has send " + likes + " likes and sent you back to the Overworld", ChatColor.RED, ChatColor.WHITE);
        } else
        {
            // Player is in the Overworld, teleport them to the Nether
            tpWorld(player, donorName, likes, "world_nether"); // Replace "world_nether" with your actual Nether world name
            McHelperClass.sayText(donorName, " has send " + likes + " likes and sent you to Hell", ChatColor.RED, ChatColor.WHITE);

        }
    }

    public static void tpWorld(Player player, String donorName, int likes, String worldName)
    {
        Location to = player.getLocation();
        Location netherLocation = new Location(Bukkit.getWorld(worldName), to.getX(), to.getY(), to.getZ());
        player.teleport(netherLocation);
        Location fixedPosition = McHelperClass.findNonBlockY(player.getLocation(), player);
        fixedPosition.setY(fixedPosition.getY() + 2);
        player.teleport(fixedPosition);
    }

    public static void opSword(Player player, String donorName, int likes)
    {
        McHelperClass.sayText(donorName, " has send " + likes + " likes and sent you the sword of a thousand truths", ChatColor.BLUE, ChatColor.WHITE);
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName("§c§l" + donorName); // Red and bold display name
        meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true); // Sharpness 10
        meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true); // Fire Aspect 5
        meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 5, true); // Looting 5
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 5, true); // Sweeping Edge 5
        meta.addEnchant(Enchantment.KNOCKBACK, 3, true); // Knockback 3
        meta.addEnchant(Enchantment.DURABILITY, 3, true); // Unbreaking 3
        meta.addEnchant(Enchantment.MENDING, 1, true); // Mending 1

        sword.setItemMeta(meta);

        // Drop the sword two blocks in front of the player
        Location dropLocation = player.getLocation().add(player.getEyeLocation().getDirection().multiply(2));
        dropLocation.setY(dropLocation.getY() + 1);
        player.getWorld().dropItemNaturally(dropLocation, sword);

        // Add particle effects
        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, dropLocation, 100);

        // Play a fitting sound
        player.getWorld().playSound(dropLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
    }

    public static void setLava(Player player, Plugin plugin) {
        int xRange = 20;
        int yRange = 20;
        World world = player.getWorld();
        int playerX = player.getLocation().getBlockX() - 1;
        int playerY = player.getLocation().getBlockY() - 1;
        int playerZ = player.getLocation().getBlockZ() - 1;

        // Loop through the specified ranges around and below the player
        for (int x = -xRange; x <= xRange; x++) {
            for (int y = -50; y <= yRange; y++) { // 50 blocks below the player
                for (int z = -xRange; z <= xRange; z++) {
                    Block block = world.getBlockAt(playerX + x, playerY + y, playerZ + z);
                    if (block.getType() != Material.AIR) {
                        block.setType(Material.MAGMA_BLOCK); // Set to hot stone material
                    }
                }
            }
        }

        // Schedule a task to run 10 seconds later to change the blocks to lava
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = -xRange; x <= xRange; x++) {
                    for (int y = -50; y <= yRange; y++) {
                        for (int z = -xRange; z <= xRange; z++) {
                            Block block = world.getBlockAt(playerX + x, playerY + y, playerZ + z);
                            if (block.getType() != Material.AIR) {
                                block.setType(Material.LAVA); // Change to lava
                            }
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 200L); // 200 ticks = 10 seconds
    }

    //Gives health regen potion
    public static void giveRegenPotion(Player player, String donorName, int likes)
    {
        eventHandler.givePotionEffect(player, donorName, " has send " + likes + " likes and regens your health!", ChatColor.GREEN, likes, PotionEffectType.REGENERATION, 120, 2);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 3, 10);
        player.spawnParticle(Particle.CRIT_MAGIC, player.getLocation(), 350, 10, 10, 10, -0.0005);
    }

    //Creates a wolf companion
    public static void createWolfCompanion(Player player, String donorName, Plugin plugin)
    {
        Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
        wolf.setTamed(true);
        wolf.setCustomName(McHelperClass.randomColor() + donorName);
        wolf.setCollarColor(McHelperClass.randomDyeColor());
        wolf.setOwner(player);
        McHelperClass.sayText(donorName, " became a subscriber! and spawned your new best friend!", ChatColor.GOLD, ChatColor.WHITE);
    }

    //Teleport
    public static void randomTeleportPlayer(Player player, String donorName, Integer likes)
    {
        Location newPosition = new Location(player.getWorld(), player.getLocation().getX() + McHelperClass.generateRandomInt(-100, 300), player.getLocation().getY(), player.getLocation().getZ() - McHelperClass.generateRandomInt(-100, 300));
        newPosition = McHelperClass.findNonBlockYFromTop(newPosition, player);
        player.teleport(newPosition);
        McHelperClass.sayText(donorName, " has send " + likes + " likes and teleported you haha!", ChatColor.GREEN, ChatColor.WHITE);
    }

    //Make it rain anvils
    public static void anvilRain(Player player, String donorName, Plugin plugin, Integer likes)
    {
        int timeInSeconds = 10;
        int interval = 10;

        McHelperClass.sayText(donorName, " has send " + likes + " likes and made it rain anvils!", ChatColor.LIGHT_PURPLE, ChatColor.WHITE);

        int randomMax = McHelperClass.generateRandomInt(20, 30);
        for (int i = 0; i < randomMax; i++)
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    Block block = player.getWorld().getBlockAt(player.getLocation().add(McHelperClass.generateRandomInt(-3, 3),
                            McHelperClass.generateRandomInt(5, 10),
                            McHelperClass.generateRandomInt(-3, 3)));
                    block.setType(Material.ANVIL);
                }
            }.runTaskLater(plugin, McHelperClass.generateRandomInt(40, 200));
        }
    }

    public static void test(Player player, String donorName, Plugin plugin)
    {

    }
}
