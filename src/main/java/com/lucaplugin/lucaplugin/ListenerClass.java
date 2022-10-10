package com.lucaplugin.lucaplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerClass implements Listener
{
    public eventHandler theEventHandler;


    public ListenerClass(eventHandler EventHandler)
    {
        theEventHandler = EventHandler;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e)
    {
        Location loc = e.getPlayer().getLocation().clone().subtract(0, 1, 0);
        Block b = loc.getBlock();
        System.out.println(eventHandler.dirtOnFire);
        if (eventHandler.dirtOnFire)
        {
            //Whatever Material you want
            if (b.getType() == Material.DIRT || b.getType() == Material.GRASS_BLOCK)
            {
                //20 = 1 Sec
                int x = 20;
                e.getPlayer().setFireTicks(x);
            }
        }
    }
}