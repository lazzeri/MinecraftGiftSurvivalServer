package com.lucaplugin.lucaplugin.game.spawn;

import java.util.ArrayList;

public class SpawnSystem {
    
    private final ArrayList<PlayerWrapper> playersList = new ArrayList<>();

    public ArrayList<PlayerWrapper> getPlayersList() {
        return playersList;
    }

    public void emptyPlayerList() {
        playersList.clear();
    }
}

