package com.lucaplugin.lucaplugin.listeners;

import com.lucaplugin.lucaplugin.events.GameEventHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMoveListener implements Listener {
    
    private final GameEventHandler eventHandler;
    private final Plugin plugin;

    public PlayerMoveListener(GameEventHandler eventHandler, Plugin plugin) {
        this.eventHandler = eventHandler;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        // Empty handler for login events
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.isAsynchronous()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    // runCodeExample(e);
                }
            }.runTask(plugin);
        } else {
            runCodeExample(e);
        }
    }

    public void runCodeExample(PlayerMoveEvent e) {
        Location loc = e.getPlayer().getLocation().clone().subtract(0, 1, 0);
        Block b = loc.getBlock();
        if (GameEventHandler.dirtOnFire) {
            // Whatever Material you want
            if (b.getType() == Material.DIRT || b.getType() == Material.GRASS_BLOCK) {
                // 20 = 1 Sec
                int x = 20;
                e.getPlayer().setFireTicks(x);
            }
        }
    }
}

