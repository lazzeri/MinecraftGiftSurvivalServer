package com.lucaplugin.lucaplugin.game.spawn;

import org.bukkit.entity.Player;

public class PlayerWrapper {
    
    private Player playerObj;

    public void setPlayer(Player playerObj) {
        this.playerObj = playerObj;
    }

    public Player getPlayer() {
        return this.playerObj;
    }
}

