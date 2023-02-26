package com.lucaplugin.lucaplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderShrinkTask extends BukkitRunnable
{
    private final WorldBorder border;
    private int timeUntilShrink;
    private static final int MAX_TIME_TO_SHRINK = 10;
    private static final int SHRINK_AMOUNT = 10;
    private static final int SHRINK_TIME = 10;

    public static boolean isIsRunning()
    {
        return isRunning;
    }

    private static boolean isRunning = false;
    private BossBar bossBar;

    public BorderShrinkTask(int timeTillShrink)
    {
        this.border = McHelperClass.getWorld().getWorldBorder();
        this.timeUntilShrink = timeTillShrink;
    }

    @Override
    public void run()
    {
        // Decrement the time until the next shrink every second
        if (timeUntilShrink > 0)
        {
            setBossBar(timeUntilShrink);
            timeUntilShrink--;
        } else
        {
            setBossBar(timeUntilShrink);
            border.setSize(border.getSize() - SHRINK_AMOUNT, SHRINK_TIME);
            System.out.println("Canceled second step");
        }
    }

    public void setBossBar(int timeUntilShrink)
    {
        double progress;
        String message;

        if (timeUntilShrink == 0)
        {
            message = ChatColor.RED + "The world border is shrinking!";
            progress = 1;
        } else
        {
            message = ChatColor.GREEN + "The world border is shrinking in " + timeUntilShrink + " seconds!";
            progress = (double) timeUntilShrink / MAX_TIME_TO_SHRINK;
        }

        if (bossBar != null)
        {
            bossBar.setProgress(progress);
            bossBar.setTitle(message);
        } else
        {
            bossBar = Bukkit.createBossBar(message, BarColor.RED, BarStyle.SOLID);
            bossBar.setProgress(progress);
            bossBar.setVisible(true);
            for (Player player : Bukkit.getOnlinePlayers())
            {
                bossBar.addPlayer(player);
            }
        }
    }
}
