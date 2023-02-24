package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class spawnSystem
{
    ArrayList<YouNowPlayer> playersList = new ArrayList<YouNowPlayer>();

    public void emptyPlayerList()
    {
        playersList.clear();
    }

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

    void removePlayerFromList(Player player)
    {
        playersList.remove(player);
    }


    void addPlayerToArrayLists(String userName, String teamName)
    {
        YouNowPlayer newPlayer = new YouNowPlayer(userName, teamName);
        playersList.add(newPlayer);
    }

    void setTeamName(String username, String teamName)
    {
        for (YouNowPlayer player : playersList)
        {
            if (player.getUsername().equals(username))
            {
                player.setBroadcasterName(teamName);
            }
        }
    }
}

