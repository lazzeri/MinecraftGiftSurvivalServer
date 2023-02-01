package com.lucaplugin.lucaplugin;

import java.util.Objects;

public class YouNowPlayer
{

    private String username;
    private String broadcasterName;
    private String leaderName;

    public YouNowPlayer(String username, String broadcasterName,String leaderName)
    {
        this.username = username;
        this.broadcasterName = broadcasterName;
        this.leaderName = leaderName;
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
        return broadcasterName;
    }

    public void setBroadcasterName(String broadcasterName)
    {
        this.broadcasterName = broadcasterName;
    }

    public boolean isLeader(){
        return Objects.equals(this.username, this.leaderName);
    }
}

