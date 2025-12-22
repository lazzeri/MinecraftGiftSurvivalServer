package com.lucaplugin.lucaplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.lucaplugin.lucaplugin.LucaPlugin;
import com.lucaplugin.lucaplugin.events.GameEventHandler;
import com.lucaplugin.lucaplugin.game.spawn.PlayerWrapper;
import com.lucaplugin.lucaplugin.util.McUtils;

public class CommandHandler {

    private final Plugin plugin;
    private final PlayerWrapper selectedUser;
    private final GameEventHandler eventHandlerObj;

    public CommandHandler(Plugin plugin, PlayerWrapper selectedUser, GameEventHandler eventHandlerObj) {
        this.plugin = plugin;
        this.selectedUser = selectedUser;
        this.eventHandlerObj = eventHandlerObj;
    }

    public boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("startgame")) {
            return handleStartGame(sender);
        }

        if (label.equalsIgnoreCase("cancelTasks")) {
            return handleCancelTasks();
        }

        if (label.equalsIgnoreCase("test")) {
            return handleTest(sender);
        }

        if (label.equalsIgnoreCase("raid")) {
            return handleRaid(sender);
        }

        if (label.equalsIgnoreCase("tntRain")) {
            return handleTntRain(sender);
        }

        return false;
    }

    private boolean handleStartGame(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage("Starting Game!");
            selectedUser.setPlayer(player);
            LucaPlugin.gameStarted = true;
        }
        return true;
    }

    private boolean handleCancelTasks() {
        McUtils.stopTasks(plugin);
        return true;
    }

    private boolean handleTest(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Give full Netherite armor
            ItemStack netheriteHelmet = new ItemStack(Material.NETHERITE_HELMET);
            ItemStack netheriteChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
            ItemStack netheriteLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);
            ItemStack netheriteBoots = new ItemStack(Material.NETHERITE_BOOTS);
            ItemStack elytra = new ItemStack(Material.ELYTRA);

            player.getInventory().setHelmet(netheriteHelmet);
            player.getInventory().setChestplate(elytra);
            player.getInventory().setLeggings(netheriteLeggings);
            player.getInventory().setBoots(netheriteBoots);

            // Enchant a Netherite sword
            ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD);
            ItemMeta swordMeta = netheriteSword.getItemMeta();
            swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
            netheriteSword.setItemMeta(swordMeta);

            player.getInventory().addItem(netheriteSword);

            // Give Elytra and rockets
            ItemStack rockets = new ItemStack(Material.FIREWORK_ROCKET, 64);
            player.getInventory().addItem(rockets);

            // Give steak
            ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
            player.getInventory().addItem(steak);
            player.getInventory().addItem(steak);
            
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            attribute.setBaseValue(40.0D);
        }
        return true;
    }

    private boolean handleRaid(CommandSender sender) {
        long seed = System.currentTimeMillis();
        // Raid logic commented out in original
        return true;
    }

    private boolean handleTntRain(CommandSender sender) {
        long seed = System.currentTimeMillis();

        if (sender instanceof Player) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                GameEventHandler.startLava(player, seed, plugin, 1, 2);
            }
        }
        return true;
    }
}

