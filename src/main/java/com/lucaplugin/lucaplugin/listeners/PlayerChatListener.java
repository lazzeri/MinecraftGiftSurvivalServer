package com.lucaplugin.lucaplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.lucaplugin.lucaplugin.LucaPlugin;
import com.lucaplugin.lucaplugin.commands.CommandHandler;

public class PlayerChatListener implements Listener {

    private final LucaPlugin plugin;
    private final CommandHandler commandHandler;

    public PlayerChatListener(LucaPlugin plugin, CommandHandler commandHandler) {
        this.plugin = plugin;
        this.commandHandler = commandHandler;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().trim();

        // Check if message starts with "test" (case-insensitive)
        if (message.toLowerCase().startsWith("test")) {
            event.setCancelled(true);

            // Parse the command - extract everything after "test"
            String[] parts = message.split("\\s+");
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);

            // Run command on main thread (chat events are async)
            Bukkit.getScheduler().runTask(plugin, () -> {
                commandHandler.handleCommand(player, args);
            });
        }
    }
}

