package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class spawnSystem
{
    public ArrayList<YouNowPlayer> getPlayersList()
    {
        return playersList;
    }

    ArrayList<YouNowPlayer> playersList = new ArrayList<YouNowPlayer>();

    public void emptyPlayerList()
    {
        playersList.clear();
    }

}

