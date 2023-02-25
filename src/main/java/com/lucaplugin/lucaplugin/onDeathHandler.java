package com.lucaplugin.lucaplugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class onDeathHandler implements Listener {

    private  Plugin plugin;

    public onDeathHandler(Plugin plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Check if the player is banned from rejoining the server
        if (Bukkit.getBanList(org.bukkit.BanList.Type.NAME).isBanned(player.getName())) {
            event.setJoinMessage(null);
            player.kickPlayer(ChatColor.RED + "You died like a hero... hope you had fun!");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        // Kick the player from the server with a message
        String deathMessage = event.getDeathMessage(); // Get the default death message
        player.kickPlayer(ChatColor.RED + "You died! Because: " + deathMessage + ". You fought brave!");
        // Ban the player from rejoining the server
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(player.getName(), "You died! Because: " + deathMessage + ". You fought brave!", null, null);

        Player killer = player.getKiller();

        if (killer == null)
        {
            return;
        }

        killer.setMaxHealth(killer.getMaxHealth() + 2);
        Bukkit.getScheduler().runTask(plugin, new Runnable()
        {
            public void run()
            {
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
            }
        });
    }
}

