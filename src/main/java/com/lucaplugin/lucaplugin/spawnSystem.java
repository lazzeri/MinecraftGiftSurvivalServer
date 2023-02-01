package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class spawnSystem
{
    ArrayList<YouNowPlayer> playersList = new ArrayList<YouNowPlayer>();
    ArrayList<YouNowPlayer> leaderList = new ArrayList<YouNowPlayer>();


    boolean checkIfPlayerInList(String usernameToCheck)
    {
        boolean containsUsername = false;

        for (YouNowPlayer player : playersList)
        {
            if (player.getUsername().equals(usernameToCheck))
            {
                //This would be if the user is in the register state and loggs out
                if (!Objects.equals(player.getBroadcasterName(), ""))
                {
                    containsUsername = true;
                    break;
                }
            }
        }
        return containsUsername;
    }

    void removePlayerFromList(Player player)
    {
        playersList.remove(player);
    }


    void addPlayerToArrayLists(YouNowPlayer player)
    {
        playersList.add(player);
        if (player.isLeader())
            leaderList.add(player);
    }

    void loginUser(String playerName)
    {


    }
}

