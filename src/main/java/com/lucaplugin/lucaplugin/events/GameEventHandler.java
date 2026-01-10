package com.lucaplugin.lucaplugin.events;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.lucaplugin.lucaplugin.util.McUtils;

import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.NoteColor;

public class GameEventHandler {

    public static boolean dirtOnFire = false;

    public static Location[] teamSpawnPoints = new Location[] {
            new Location(McUtils.getWorld(), 100, 100, 100),
            new Location(McUtils.getWorld(), 200, 200, 200),
            // ... and so on for each team
    };

    public static void spawnWithers(Player player, String donorName) {
        World world = player.getWorld();
        Random random = new Random();

        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize();

        for (int i = 0; i < 3; i++) {
            double xOffset = direction.getX() * (i + 1) * 3;
            double yOffset = 1.5;
            double zOffset = direction.getZ() * (i + 1) * 3;

            Location spawnLocation = playerLocation.clone().add(xOffset, yOffset, zOffset);
            Wither wither = (Wither) world.spawnEntity(spawnLocation, EntityType.WITHER);

            world.strikeLightningEffect(spawnLocation);

            ChatColor randomColor = ChatColor.values()[random.nextInt(ChatColor.values().length)];
            wither.setCustomName(randomColor + donorName);
            wither.setCustomNameVisible(true);
        }

        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CAVE, 5.0F, 0.5F);

    }

    public static void spawnZombieCircle(String playerName, Plugin plugin) {
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            System.out.println("Player " + playerName + " not found or not online");
            return;
        }

        Location playerLocation = targetPlayer.getLocation();
        World world = targetPlayer.getWorld();

        int zombieCount = 10;
        double radius = 5.0;

        for (int i = 0; i < zombieCount; i++) {
            double angle = 2 * Math.PI * i / zombieCount;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location spawnLocation = playerLocation.clone().add(x, 0, z);
            spawnLocation = McUtils.findNonBlockY(spawnLocation, targetPlayer);

            Zombie zombie = (Zombie) world.spawnEntity(spawnLocation, EntityType.ZOMBIE);
            zombie.setTarget(targetPlayer);
            zombie.setCustomName(ChatColor.RED + "Zombie Mode");
            zombie.setCustomNameVisible(true);

            world.spawnParticle(Particle.SMOKE_NORMAL, spawnLocation, 20, 0.5, 1, 0.5, 0.05);
        }

        world.playSound(playerLocation, Sound.ENTITY_ZOMBIE_AMBIENT, 2.0F, 0.5F);
        world.playSound(playerLocation, Sound.ENTITY_ZOMBIE_AMBIENT, 2.0F, 0.5F);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 3 || !targetPlayer.isOnline()) {
                    this.cancel();
                    return;
                }

                new ParticleBuilder(ParticleEffect.SMOKE_LARGE, targetPlayer.getLocation().add(0, 1, 0))
                        .setOffset(new Vector(1.5, 0.5, 1.5))
                        .setAmount(40)
                        .display();

                count++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public static void createRaid(Player player, String donorName) {
        EntityType[] entityTypes = { EntityType.PILLAGER, EntityType.RAVAGER, EntityType.EVOKER,
                EntityType.VINDICATOR };

        createEntityAttack(player, donorName, 25, 153, 11, 163, 3.0F, entityTypes);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
    }

    public static void adrenalinRush(Player player, String donorName) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

        switch (McUtils.generateRandomInt(0, 2)) {
            case 0:
                McUtils.playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 1);
                givePotionEffect(player, donorName, PotionEffectType.JUMP, 1200, 2);
                break;
            case 1:
                McUtils.playSoundXTimes(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5F, 8);
                givePotionEffect(player, donorName, PotionEffectType.SPEED, 1200, 2);
                break;
            case 2:
                McUtils.playSoundXTimes(player, Sound.BLOCK_SAND_HIT, 5F, 15);
                givePotionEffect(player, donorName, PotionEffectType.BLINDNESS, 300, 2);
                break;
        }
    }

    public static void magicNotes(Player player, String donorName, Plugin plugin, int interval) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    new ParticleBuilder(ParticleEffect.NOTE, player.getLocation())
                            .setParticleData(new NoteColor(1))
                            .setOffset(new Vector(5, 5, 5))
                            .setAmount(40)
                            .display();
                }
            }
        }.runTaskTimer(plugin, 0L, interval);
    }

    public static void tntRain(Player player, String donorName, Plugin plugin) {
        McUtils.playSoundXTimes(player, Sound.ENTITY_CREEPER_PRIMED, 10F, 20);
        int randomMax = McUtils.generateRandomInt(40, 50);
        for (int i = 0; i < randomMax; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(
                            player.getLocation().add(McUtils.generateRandomInt(-20, 20),
                                    McUtils.generateRandomInt(5, 15), McUtils.generateRandomInt(-20, 20)),
                            EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(McUtils.generateRandomInt(50, 220));
                    McUtils.wait(50);
                }
            }.runTask(plugin);
        }
    }

    public static void itemSnack(Player player, String donorName) {
        Inventory inventory = player.getInventory();
        ItemStack[] toolbarItems = new ItemStack[9];

        for (int i = 0; i < 9; i++) {
            toolbarItems[i] = inventory.getItem(i);
        }

        for (int i = 0; i < 9; i++) {
            if (toolbarItems[i] != null && toolbarItems[i].getType() != Material.AIR) {
                inventory.setItem(i, new ItemStack(Material.AIR));
                return;
            }
        }

        inventory.setItem(0, new ItemStack(Material.COOKIE));
    }

    public static void throwExpBottles(Player player, String donorName) {
        Location loc = player.getLocation();
        for (int i = 0; i < 10; i++) {
            int setNewSum = McUtils.generateRandomInt(1, 3);
            player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3, 10);
            player.giveExp(setNewSum);
            player.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 350, 10, 10, 10, -0.0005);
            McUtils.wait(McUtils.generateRandomInt(50, 150));
        }
    }

    public static void createEntityAttack(Player player, String donorName, int eventAmount,
            int rgb1, int rgb2, int rgb3, float size2, EntityType[] entityTypes) {
        double size = 10;
        int positions = 360 / eventAmount;
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(rgb1, rgb2, rgb3), size2);
        Random random = new Random();

        for (int i = 0; i < 360; i += positions) {
            double angle = (i * Math.PI / 180);
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            EntityType randomEntityType = entityTypes[random.nextInt(entityTypes.length)];
            McUtils.spawnEntityWithParticle(player, Particle.REDSTONE, dustOptions, randomEntityType, (int) x, (int) z);
        }

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 5.0F, 0.5F);
    }

    public static void givePotionEffect(Player player, String donorName, PotionEffectType potionEffect,
            Integer duration, Integer amplifier) {
        player.addPotionEffect(new PotionEffect(potionEffect, duration, amplifier));
    }

    public static void spawnEnchantedDiamondArmorStandInFrontOfPlayer(Player player, String donorName) {
        Location playerLocation = player.getLocation();
        Location spawnLocation = playerLocation.add(playerLocation.getDirection().multiply(2));
        spawnLocation.setY(spawnLocation.getY() + 1);

        ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation,
                EntityType.ARMOR_STAND);
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
    }

    public static void elytraAndRockets(Player player, String donorName) {
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        ItemStack rockets = new ItemStack(Material.FIREWORK_ROCKET, 64);

        Location dropLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        dropLocation.setY(dropLocation.getY() + 1);
        player.getWorld().dropItemNaturally(dropLocation, elytra);
        player.getWorld().dropItemNaturally(dropLocation, rockets);

        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.0F);
        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, dropLocation, 50);
    }

    static void startValuableItemRain(Player player, String donorName, Plugin plugin) {
        Location location = player.getLocation();
        location.setY(location.getY() + 5);

        new BukkitRunnable() {
            final List<Material> valuableItems = Arrays.asList(
                    Material.DIAMOND, Material.EMERALD, Material.IRON_INGOT, Material.COAL, Material.GOLD_INGOT);
            int remainingDrops = 100;
            Random random = new Random();
            World world = player.getWorld();

            @Override
            public void run() {
                if (remainingDrops > 0) {
                    Material randomItem = valuableItems.get(random.nextInt(valuableItems.size()));
                    ItemStack itemStack = new ItemStack(randomItem);
                    double xOffset = random.nextDouble() * 2 - 1;
                    double zOffset = random.nextDouble() * 2 - 1;
                    world.spawnParticle(Particle.CLOUD, location, 10, xOffset, 0, zOffset, 0);
                    world.dropItem(location, itemStack);
                    remainingDrops--;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static void netherAttack(Player player, String donorName) {
        player.getWorld().setTime(12000);
        EntityType[] entityTypes = { EntityType.WITHER_SKELETON, EntityType.SKELETON, EntityType.BLAZE,
                EntityType.SKELETON };
        createEntityAttack(player, donorName, 20, 255, 0, 0, 3.0F, entityTypes);
    }

    public static void loadedCreeperAttack(Player player, String donorName) {
        double size = 10.0;
        int eventAmount = 25;
        int positions = 360 / eventAmount;
        Random random = new Random();

        for (int i = 0; i < 360; i += positions) {
            double angle = i * Math.PI / 180.0;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);

            Location spawnLocation = player.getLocation().clone().add(x, 0, z);
            spawnLocation = McUtils.findNonBlockY(spawnLocation, player);
            spawnLocation.setY(spawnLocation.getY() + 4.0);

            Creeper creeper = (Creeper) player.getWorld().spawnEntity(spawnLocation, EntityType.CREEPER);
            if (random.nextBoolean()) {
                creeper.setPowered(true);
            }
            player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, spawnLocation, 50);
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5.0F, 0.5F);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5.0F, 0.5F);

    }

    public static void zombieInvasion(Player player, String donorName) {
        player.getWorld().setTime(12000);
        EntityType[] entityTypes = { EntityType.GIANT, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER,
                EntityType.ZOMBIE_HORSE };
        createEntityAttack(player, donorName, 40, 255, 0, 0, 3.0F, entityTypes);
    }

    public static void farmTime(Player player, String donorName) {
        EntityType[] entityTypes = { EntityType.COW, EntityType.CHICKEN, EntityType.HORSE, EntityType.PIG,
                EntityType.DONKEY, EntityType.PANDA, EntityType.LLAMA };
        createEntityAttack(player, donorName, 25, 220, 170, 255, 3.0F, entityTypes);
    }

    public static void createThunder(Player player, String donorName) {
        Location location = new Location(player.getWorld(),
                player.getLocation().getX() + McUtils.generateRandomInt(0, 5),
                player.getLocation().getY() + McUtils.generateRandomInt(-10, 10),
                player.getLocation().getZ() + McUtils.generateRandomInt(0, 5));
        Location fixedYLocation = McUtils.findNonBlockY(location, player);
        player.getWorld().strikeLightning(fixedYLocation);

    }

    public static void giveSlowPotion(Player player, String donorName) {
        givePotionEffect(player, donorName, PotionEffectType.SLOW, 600, 2);
        player.playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 3, 10);
        player.spawnParticle(Particle.DRIPPING_HONEY, player.getLocation(), 350, 10, 10, 10, -0.0005);
    }

    public static void giveRegenPotion(Player player, String donorName) {
        givePotionEffect(player, donorName, PotionEffectType.REGENERATION, 120, 2);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 3, 10);
        player.spawnParticle(Particle.CRIT_MAGIC, player.getLocation(), 350, 10, 10, 10, -0.0005);
    }

    public static void randomTeleportPlayer(Player player, String donorName) {
        Location newPosition = new Location(player.getWorld(),
                player.getLocation().getX() + McUtils.generateRandomInt(-100, 300),
                player.getLocation().getY(),
                player.getLocation().getZ() - McUtils.generateRandomInt(-100, 300));
        newPosition = McUtils.findNonBlockYFromTop(newPosition, player);
        player.teleport(newPosition);

    }

    public static void anvilRain(Player player, String donorName, Plugin plugin) {
        int randomMax = McUtils.generateRandomInt(20, 30);
        for (int i = 0; i < randomMax; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    org.bukkit.block.Block block = player.getWorld().getBlockAt(
                            player.getLocation().add(McUtils.generateRandomInt(-3, 3),
                                    McUtils.generateRandomInt(5, 10),
                                    McUtils.generateRandomInt(-3, 3)));
                    block.setType(Material.ANVIL);
                }
            }.runTaskLater(plugin, McUtils.generateRandomInt(40, 200));
        }
    }

    public static void opSword(Player player, String donorName) {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName("§c§l" + donorName);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
        meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 5, true);
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 5, true);
        meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        sword.setItemMeta(meta);

        Location dropLocation = player.getLocation().add(player.getEyeLocation().getDirection().multiply(2));
        dropLocation.setY(dropLocation.getY() + 1);
        player.getWorld().dropItemNaturally(dropLocation, sword);
        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, dropLocation, 100);
        player.getWorld().playSound(dropLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
    }

    public static void tpNetherOrOverworld(Player player, String donorName) {
        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            tpWorld(player, donorName, "world");
        } else {
            tpWorld(player, donorName, "world_nether");
        }
    }

    public static void tpWorld(Player player, String donorName, String worldName) {
        Location to = player.getLocation();
        Location netherLocation = new Location(Bukkit.getWorld(worldName), to.getX(), to.getY(), to.getZ());
        player.teleport(netherLocation);
        Location fixedPosition = McUtils.findNonBlockY(player.getLocation(), player);
        fixedPosition.setY(fixedPosition.getY() + 2);
        player.teleport(fixedPosition);
    }

    public static void test(Player player, String donorName, Plugin plugin) {
        // Test method placeholder
    }

    public static int counter = 0;

    public static void startLava(Player player, long seed, Plugin plugin, int minTimeInMin, int addTimeInMin) {

        counter = 0;
        setLava(player, plugin, seed, minTimeInMin, addTimeInMin);
    }

    public static void setLava(Player player, Plugin plugin, long seed, int minTimeInMin, int addTimeInMin) {
        int xRange = 20;
        int yRange = 20;
        World world = player.getWorld();
        int playerX = player.getLocation().getBlockX() - 1;
        int playerY = player.getLocation().getBlockY() - 1;
        int playerZ = player.getLocation().getBlockZ() - 1;
        counter++;

        for (int x = -xRange; x <= xRange; x++) {
            for (int y = -50; y <= yRange; y++) {
                for (int z = -xRange; z <= xRange; z++) {
                    org.bukkit.block.Block block = world.getBlockAt(playerX + x, playerY + y, playerZ + z);
                    if (block.getType() != Material.AIR) {
                        block.setType(Material.MAGMA_BLOCK);
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = -xRange; x <= xRange; x++) {
                    for (int y = -50; y <= yRange; y++) {
                        for (int z = -xRange; z <= xRange; z++) {
                            org.bukkit.block.Block block = world.getBlockAt(playerX + x, playerY + y, playerZ + z);
                            if (block.getType() != Material.AIR) {
                                block.setType(Material.LAVA);
                            }
                        }
                    }
                }
                if (counter < 5)
                    scheduleNextLavaSet(player, plugin, seed, minTimeInMin, addTimeInMin);
                else
                    System.out.println("Did 5 Rounds, we stop");
            }
        }.runTaskLater(plugin, 100L);
    }

    private static void scheduleNextLavaSet(Player player, Plugin plugin, long seed, int minTimeInMin,
            int addTimeInMin) {
        Random random = new Random(seed);
        long second = 20L;
        long minute = second * 60;
        long minimumTimeInMinutes = minute * minTimeInMin;
        long maxTimeToAddInMinutes = minute * random.nextInt(addTimeInMin);
        long actualDelay = minimumTimeInMinutes + maxTimeToAddInMinutes;
        System.out.println("New delay " + actualDelay);

        new BukkitRunnable() {
            @Override
            public void run() {
                setLava(player, plugin, seed, minTimeInMin, addTimeInMin);
            }
        }.runTaskLater(plugin, actualDelay);
    }

    public static void spawnRandomEntityWithNametag(Player player, String donorName) {
        EntityType[] mobTypes = {
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CREEPER,
                EntityType.ENDERMAN, EntityType.WITCH, EntityType.BLAZE, EntityType.GHAST,
                EntityType.WITHER_SKELETON, EntityType.STRAY, EntityType.HUSK, EntityType.PHANTOM,
                EntityType.DROWNED, EntityType.VEX, EntityType.EVOKER, EntityType.VINDICATOR,
                EntityType.PILLAGER, EntityType.RAVAGER, EntityType.HOGLIN, EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE, EntityType.ZOGLIN, EntityType.ZOMBIE_HORSE,
                EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN
        };

        Location spawnLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        spawnLocation.setY(spawnLocation.getY() + 2);
        EntityType randomEntityType = mobTypes[new Random().nextInt(mobTypes.length)];
        org.bukkit.entity.Entity spawnedEntity = player.getWorld().spawnEntity(spawnLocation, randomEntityType);

        if (spawnedEntity instanceof org.bukkit.entity.LivingEntity) {
            org.bukkit.entity.LivingEntity livingEntity = (org.bukkit.entity.LivingEntity) spawnedEntity;
            livingEntity.setCustomNameVisible(true);
            livingEntity.setCustomName(McUtils.randomColor() + donorName);
        }
    }

    public static void createSkeletonRiders(Player player, String donorName, int eventAmount,
            int rgb1, int rgb2, int rgb3, float size2, Plugin plugin) {
        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            tpNetherOrOverworld(player, donorName);
            return;
        }
        double size = 10.0;
        int positions = 360 / eventAmount;

        for (int i = 0; i < 360; i += positions) {
            double angle = (i * Math.PI / 180);
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "summon skeleton_horse " + x + " " + player.getLocation().getY() + " " + z + " "
                            + "{SkeletonTrap:1}");
        }

        player.getWorld().setTime(12000);
        player.getWorld().setStorm(true);
        player.getWorld().setThundering(false);
        player.getWorld().playSound(player.getLocation(), Sound.AMBIENT_CAVE, 5.0F, 0.5F);
    }

    public static void makeChickenCompanion(Player player, String donorName, Plugin plugin) {
        Location chickenPosition = McUtils.findNonBlockY(player.getLocation().add(2, 3, 1), player);
        chickenPosition.add(0, 3, 0);

        org.bukkit.entity.Chicken chicken = (org.bukkit.entity.Chicken) player.getWorld().spawnEntity(chickenPosition,
                EntityType.CHICKEN);
        chicken.setCustomName(McUtils.randomColor() + donorName);
        chicken.setBaby();
        player.getWorld().playEffect(chicken.getLocation(), org.bukkit.Effect.ANVIL_BREAK, 20);

        chickenPermanentFollower(player, chicken, plugin);
    }

    public static void chickenPermanentFollower(Player player, org.bukkit.entity.Animals entity, Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isValid())
                    this.cancel();
                entity.teleport(player.getLocation());
                entity.setTarget(player);
            }
        }.runTaskTimer(plugin, 10 * 12, 20 * 12);
    }

    public static void oneHeart(Player player, Plugin plugin, String donorName) {
        org.bukkit.attribute.AttributeInstance attribute = player
                .getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
        attribute.setBaseValue(2.0D);

        new BukkitRunnable() {
            @Override
            public void run() {

                org.bukkit.attribute.AttributeInstance attribute = player
                        .getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
                attribute.setBaseValue(20.0D);
                this.cancel();
            }
        }.runTaskLater(plugin, 1200);
    }

    public static void twentyHeart(Player player, Plugin plugin, String donorName) {
        org.bukkit.attribute.AttributeInstance attribute = player
                .getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
        attribute.setBaseValue(40.0D);

        new BukkitRunnable() {
            @Override
            public void run() {

                org.bukkit.attribute.AttributeInstance attribute = player
                        .getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
                attribute.setBaseValue(20.0D);
                this.cancel();
            }
        }.runTaskLater(plugin, 2400);
    }

    public static void createWolfCompanion(Player player, String donorName, Plugin plugin) {
        org.bukkit.entity.Wolf wolf = (org.bukkit.entity.Wolf) player.getWorld().spawnEntity(player.getLocation(),
                EntityType.WOLF);
        wolf.setTamed(true);
        wolf.setCustomName(McUtils.randomColor() + donorName);
        wolf.setCollarColor(McUtils.randomDyeColor());
        wolf.setOwner(player);

    }

    public static void spawnTemporaryWither(Player player, String donorName, Plugin plugin) {
        System.out.println("Wither spawn method started for player: " + player.getName());
        World world = player.getWorld();
        Location spawnLocation = player.getLocation().add(0, 10, 0);

        Wither wither = (Wither) world.spawnEntity(spawnLocation, EntityType.WITHER);
        System.out.println("Wither spawned successfully at: " + spawnLocation.toString());

        wither.setCustomName(ChatColor.DARK_PURPLE + donorName + "'s Wither");
        wither.setCustomNameVisible(true);

        world.strikeLightningEffect(spawnLocation);
        world.playSound(spawnLocation, Sound.ENTITY_WITHER_SPAWN, 3.0F, 0.5F);
        world.spawnParticle(Particle.EXPLOSION_HUGE, spawnLocation, 100, 3, 3, 3, 0.1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (wither != null && !wither.isDead()) {
                    System.out.println("Removing wither after 15 seconds");
                    world.spawnParticle(Particle.EXPLOSION_HUGE, wither.getLocation(), 20, 3, 3, 3, 0.1);
                    world.playSound(wither.getLocation(), Sound.ENTITY_WITHER_DEATH, 3.0F, 1.0F);
                    wither.remove();

                }
            }
        }.runTaskLater(plugin, 20L * 15);
    }

    public static void spawnZombieArmy(Player player, String donorName, Plugin plugin) {
        System.out.println("Zombie Army spawn method started for player: " + player.getName());
        World world = player.getWorld();
        List<org.bukkit.entity.Entity> zombieArmy = new java.util.ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Location zombieLocation = player.getLocation().add(
                    (Math.random() - 0.5) * 10, 2, (Math.random() - 0.5) * 10);

            Zombie bigZombie = (Zombie) world.spawnEntity(zombieLocation, EntityType.ZOMBIE);
            bigZombie.setCustomName(ChatColor.DARK_RED + donorName + "'s Giant Zombie");
            bigZombie.setCustomNameVisible(true);
            bigZombie.setBaby(false);
            bigZombie.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
            bigZombie.setHealth(40.0);
            bigZombie.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);
            zombieArmy.add(bigZombie);
            world.strikeLightningEffect(zombieLocation);
        }

        double radius = 15.0;
        for (int i = 0; i < 50; i++) {
            double angle = (2 * Math.PI * i) / 50;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location zombieLocation = player.getLocation().add(x, 1, z);
            Zombie zombie = (Zombie) world.spawnEntity(zombieLocation, EntityType.ZOMBIE);
            zombie.setCustomName(ChatColor.RED + donorName + "'s Minion");
            zombie.setCustomNameVisible(true);
            zombie.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
            zombie.getAttribute(org.bukkit.attribute.Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(40.0);
            zombieArmy.add(zombie);
        }

        world.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 3.0F, 0.5F);
        world.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 3.0F, 0.5F);
        world.spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 500, 10, 1, 10, 0.1);

        new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("Removing zombie army after 30 seconds");
                for (org.bukkit.entity.Entity zombie : zombieArmy) {
                    if (zombie != null && !zombie.isDead()) {
                        Location loc = zombie.getLocation();
                        world.spawnParticle(Particle.SMOKE_LARGE, loc, 20, 0.5, 1, 0.5, 0.1);
                        world.playSound(loc, Sound.ENTITY_ZOMBIE_DEATH, 1.0F, 1.0F);
                        zombie.remove();
                    }
                }

            }
        }.runTaskLater(plugin, 20L * 30);
    }

    public static void resetWorld(Player player, String donorName, Plugin plugin, long seed) {
        String tempWorldName = "temp_world";
        String mainWorldName = "world";

        // Step 1: Get the main world and teleport all players there
        World mainWorld = Bukkit.getWorld(mainWorldName);
        if (mainWorld == null) {
            player.sendMessage(ChatColor.RED + "Main world not found!");
            return;
        }

        // Teleport all players to main world
        Location mainSpawn = mainWorld.getSpawnLocation().add(0, 1, 0);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setInvulnerable(true);
            onlinePlayer.teleport(mainSpawn);
            onlinePlayer.sendMessage(ChatColor.YELLOW + "World reset in progress... Please wait.");
        }

        // Wait a bit for teleports to complete before unloading/deleting temp_world
        new BukkitRunnable() {
            @Override
            public void run() {
                // Step 2: Unload and delete "temp_world" and its dimensions (nether, end)
                String[] worldsToDelete = {
                    tempWorldName, tempWorldName + "_nether", tempWorldName + "_the_end",
                    mainWorldName + "_nether", mainWorldName + "_the_end"
                };

                for (String worldName : worldsToDelete) {
                    World oldWorld = Bukkit.getWorld(worldName);
                    if (oldWorld != null) {
                        System.out.println("[ResetWorld] Unloading " + worldName + "...");
                        boolean unloaded = Bukkit.unloadWorld(oldWorld, false);
                        if (!unloaded) {
                            System.out.println("[ResetWorld] ERROR: Failed to unload " + worldName + "!");
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayer.setInvulnerable(false);
                                onlinePlayer.sendMessage(ChatColor.RED + "Failed to unload " + worldName + " for reset!");
                            }
                            return;
                        }
                        System.out.println("[ResetWorld] " + worldName + " unloaded successfully.");
                    }

                    File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
                    if (worldFolder.exists()) {
                        System.out.println("[ResetWorld] Deleting " + worldName + " folder: " + worldFolder.getAbsolutePath());
                        boolean deleted = deleteDirectory(worldFolder);
                        if (!deleted || worldFolder.exists()) {
                            System.out.println("[ResetWorld] ERROR: Failed to delete " + worldName + " folder! Still exists: " + worldFolder.exists());
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayer.setInvulnerable(false);
                                onlinePlayer.sendMessage(ChatColor.RED + "Failed to delete " + worldName + " folder!");
                            }
                            return;
                        }
                        System.out.println("[ResetWorld] " + worldName + " folder deleted successfully.");
                    }
                }

                // Step 3: Create new temp_world with seed
                System.out.println("[ResetWorld] Creating new temp_world with seed: " + seed);
                World newTempWorld = Bukkit.createWorld(WorldCreator.name(tempWorldName).seed(seed));

                if (newTempWorld == null) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.setInvulnerable(false);
                        onlinePlayer.sendMessage(ChatColor.RED + "Failed to create new temp_world!");
                    }
                    return;
                }

                // Step 3b: Regenerate main world's nether and end
                System.out.println("[ResetWorld] Creating new world_nether with seed: " + seed);
                World newNether = Bukkit.createWorld(WorldCreator.name(mainWorldName + "_nether")
                        .environment(World.Environment.NETHER)
                        .seed(seed));
                if (newNether == null) {
                    System.out.println("[ResetWorld] WARNING: Failed to create world_nether!");
                }

                System.out.println("[ResetWorld] Creating new world_the_end with seed: " + seed);
                World newEnd = Bukkit.createWorld(WorldCreator.name(mainWorldName + "_the_end")
                        .environment(World.Environment.THE_END)
                        .seed(seed));
                if (newEnd == null) {
                    System.out.println("[ResetWorld] WARNING: Failed to create world_the_end!");
                }

                // Step 4: Teleport all players to the new temp_world
                Location newSpawn = newTempWorld.getSpawnLocation().add(0, 1, 0);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.teleport(newSpawn);
                    onlinePlayer.setInvulnerable(false);
                    onlinePlayer.sendMessage(ChatColor.GREEN + "World has been reset by " + donorName + " with seed: " + seed);
                }

                // Play effects
                newTempWorld.playSound(newSpawn, Sound.ENTITY_ENDER_DRAGON_GROWL, 5.0F, 1.0F);
                newTempWorld.spawnParticle(Particle.EXPLOSION_HUGE, newSpawn, 100, 10, 10, 10, 0.1);

                System.out.println("[ResetWorld] temp_world reset completed successfully!");
            }
        }.runTaskLater(plugin, 40L); // Wait 2 seconds for teleports to complete
    }

    // Helper method to recursively delete a directory
    private static boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return true;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!deleteDirectory(file)) {
                        System.out.println("[ResetWorld] Failed to delete subdirectory: " + file.getAbsolutePath());
                        return false;
                    }
                } else {
                    if (!file.delete()) {
                        System.out.println("[ResetWorld] Failed to delete file: " + file.getAbsolutePath());
                        // Try to force garbage collection and retry
                        System.gc();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // Ignore
                        }
                        if (!file.delete()) {
                            System.out.println("[ResetWorld] Retry failed for file: " + file.getAbsolutePath());
                            return false;
                        }
                    }
                }
            }
        }

        boolean deleted = directory.delete();
        if (!deleted) {
            System.out.println("[ResetWorld] Failed to delete directory: " + directory.getAbsolutePath());
        }
        return deleted;
    }
}
