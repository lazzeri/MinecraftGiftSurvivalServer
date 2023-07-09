package com.lucaplugin.lucaplugin;

import org.bukkit.entity.Player;

import java.util.Objects;

public class YouNowPlayer
{

    private Player playerObj;
    private String username;
    private int userId;

    public YouNowPlayer(String username, Player playerObj, int userId)
    {
        this.username = username;
        this.playerObj = playerObj;
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public int getUserId() {
        return this.userId;
    }

    public Player getPlayer(){
        return this.playerObj;
    }
}

