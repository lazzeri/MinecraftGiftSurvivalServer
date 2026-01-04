package com.lucaplugin.lucaplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PortalListener implements Listener {

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        World fromWorld = event.getFrom().getWorld();
        if (fromWorld == null) {
            return;
        }

        String fromWorldName = fromWorld.getName();
        TeleportCause cause = event.getCause();

        // Handle Nether portal from world_nether -> temp_world (instead of world)
        if (cause == TeleportCause.NETHER_PORTAL) {
            if (fromWorldName.equals("world_nether")) {
                // Player is leaving nether, redirect to temp_world instead of world
                World tempWorld = Bukkit.getWorld("temp_world");
                if (tempWorld != null) {
                    Location from = event.getFrom();
                    // Nether to overworld: multiply coordinates by 8
                    Location newTo = new Location(tempWorld, from.getX() * 8, from.getY(), from.getZ() * 8);
                    event.setTo(newTo);
                    System.out.println("[PortalListener] Redirected player from world_nether to temp_world");
                }
            } else if (fromWorldName.equals("temp_world")) {
                // Player is entering nether from temp_world, go to world_nether
                World netherWorld = Bukkit.getWorld("world_nether");
                if (netherWorld != null) {
                    Location from = event.getFrom();
                    // Overworld to nether: divide coordinates by 8
                    Location newTo = new Location(netherWorld, from.getX() / 8, from.getY(), from.getZ() / 8);
                    event.setTo(newTo);
                    System.out.println("[PortalListener] Redirected player from temp_world to world_nether");
                }
            }
        }

        // Handle End portal from world_the_end -> temp_world (instead of world)
        if (cause == TeleportCause.END_PORTAL) {
            if (fromWorldName.equals("world_the_end")) {
                // Player is leaving the end, redirect to temp_world instead of world
                World tempWorld = Bukkit.getWorld("temp_world");
                if (tempWorld != null) {
                    Location spawn = tempWorld.getSpawnLocation();
                    event.setTo(spawn);
                    System.out.println("[PortalListener] Redirected player from world_the_end to temp_world");
                }
            } else if (fromWorldName.equals("temp_world")) {
                // Player is entering the end from temp_world, go to world_the_end
                World endWorld = Bukkit.getWorld("world_the_end");
                if (endWorld != null) {
                    // End spawn platform location
                    Location endSpawn = new Location(endWorld, 100, 49, 0);
                    event.setTo(endSpawn);
                    System.out.println("[PortalListener] Redirected player from temp_world to world_the_end");
                }
            }
        }
    }
}

