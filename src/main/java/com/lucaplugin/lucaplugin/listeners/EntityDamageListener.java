package com.lucaplugin.lucaplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player player = (Player) event.getEntity();
            System.out.println(damager.getName() + player.getName());
            
            // Check if both players are on the same team
            System.out.println(damager.getScoreboard().getEntryTeam(damager.getName()));
            System.out.println(damager.getScoreboard().getEntryTeam(player.getName()));

            if (damager.getScoreboard().getEntryTeam(damager.getName())
                    .equals(damager.getScoreboard().getEntryTeam(player.getName()))) {
                event.setCancelled(true); // Cancel the event to prevent team damage
            }
        }
    }
}

