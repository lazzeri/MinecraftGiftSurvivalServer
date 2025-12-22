package com.lucaplugin.lucaplugin.game.border;

import com.lucaplugin.lucaplugin.util.McUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BorderIterator extends BukkitRunnable {
    
    private static final int MIN_BORDER_SIZE = 100;
    private final Plugin plugin;
    private BukkitTask bukkitTask;

    public BorderIterator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (McUtils.getWorld().getWorldBorder().getSize() == MIN_BORDER_SIZE) {
            System.out.println("Found minimum Size so stop");
            BorderShrinkTask.getBossBar().setTitle("Border Complete. Time to fight!");
            this.cancel();
            return;
        }

        if (bukkitTask != null) {
            bukkitTask.cancel();
            System.out.println("Canceled" + bukkitTask);
        }

        bukkitTask = new BorderShrinkTask(10).runTaskTimer(plugin, 0L, 20L);
    }
}

