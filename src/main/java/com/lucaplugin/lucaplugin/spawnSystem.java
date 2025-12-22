package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class spawnSystem
{
    public ArrayList<player> getPlayersList()
    {
        return playersList;
    }

    ArrayList<player> playersList = new ArrayList<player>();

    public void emptyPlayerList()
    {
        playersList.clear();
    }

}

