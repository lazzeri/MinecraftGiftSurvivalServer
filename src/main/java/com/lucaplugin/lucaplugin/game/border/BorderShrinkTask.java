package com.lucaplugin.lucaplugin.game.border;

import com.lucaplugin.lucaplugin.util.McUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderShrinkTask extends BukkitRunnable {
    
    private static final int MAX_TIME_TO_SHRINK = 10;
    private static final int SHRINK_AMOUNT = 10;
    private static final int SHRINK_TIME = 10;
    private static BossBar bossBar;
    
    private final WorldBorder border;
    private int timeUntilShrink;

    public BorderShrinkTask(int timeTillShrink) {
        this.border = McUtils.getWorld().getWorldBorder();
        this.timeUntilShrink = timeTillShrink;
    }

    public static BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public void run() {
        if (timeUntilShrink > 0) {
            setBossBar(timeUntilShrink);
            timeUntilShrink--;
        } else {
            System.out.println("Finished Shrinked");
            setBossBar(timeUntilShrink);
            border.setSize(border.getSize() - SHRINK_AMOUNT, SHRINK_TIME);
            this.cancel();
        }
    }

    public void setBossBar(int timeUntilShrink) {
        double progress;
        String message;

        if (timeUntilShrink == 0) {
            message = ChatColor.RED + "The world border is shrinking!";
            progress = 1;
        } else {
            message = ChatColor.GREEN + "The world border is shrinking in " + timeUntilShrink + " seconds!";
            progress = (double) timeUntilShrink / MAX_TIME_TO_SHRINK;
        }

        if (bossBar != null) {
            bossBar.setProgress(progress);
            bossBar.setTitle(message);
        } else {
            bossBar = Bukkit.createBossBar(message, BarColor.RED, BarStyle.SOLID);
            bossBar.setProgress(progress);
            bossBar.setVisible(true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                bossBar.addPlayer(player);
            }
        }
    }
}

