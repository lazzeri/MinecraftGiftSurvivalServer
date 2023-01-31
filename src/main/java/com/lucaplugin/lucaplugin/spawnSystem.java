package com.lucaplugin.lucaplugin;

import java.util.ArrayList;

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
                containsUsername = true;
                break;
            }
        }
        return containsUsername;
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

