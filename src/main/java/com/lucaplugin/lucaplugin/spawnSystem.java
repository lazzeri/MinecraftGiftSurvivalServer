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

    public void removePlayerFromList(String username)
    {
        for (Iterator<YouNowPlayer> iterator = playersList.iterator(); iterator.hasNext(); )
        {
            YouNowPlayer player = iterator.next();
            if (player.getUsername().equals(username))
            {
                iterator.remove(); // Remove the player from the list
                break; // Stop searching after the first match
            }
        }
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

