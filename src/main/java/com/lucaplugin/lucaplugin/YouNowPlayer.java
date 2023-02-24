package com.lucaplugin.lucaplugin;

import java.util.Objects;

public class YouNowPlayer
{

    private String username;
    private String teamName;

    public YouNowPlayer(String username, String teamName)
    {
        this.username = username;
        this.teamName = teamName;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getBroadcasterName()
    {
        return teamName;
    }

    public void setBroadcasterName(String teamName)
    {
        this.teamName = teamName;
    }

}

