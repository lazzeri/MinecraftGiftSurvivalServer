package com.lucaplugin.lucaplugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BorderIterator extends BukkitRunnable
{
    private final Plugin plugin;
    private BukkitTask bukkitTask;

    public BorderIterator(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        int MIN_BORDER_SIZE = 10;
        if (McHelperClass.getWorld().getWorldBorder().getSize() == MIN_BORDER_SIZE)
        { // Check if condition is met
            System.out.println("Found minimimum Size so stop");
            this.cancel(); // Stop running task
            return;
        }

        if (bukkitTask != null)
        {
            bukkitTask.cancel();
            System.out.println("Canceled" + bukkitTask);
        }

        bukkitTask = new BorderShrinkTask(10).runTaskTimer(plugin, 0L, 20L);
    }
}

