package com.nianticproject.ingress.common.model;

import com.nianticproject.ingress.common.PlayerLocation;

public interface PlayerModel {

    public PlayerLocation getPlayerLocation();
    public void addListener(PlayerListener listener);
    public void removeListener(PlayerListener listener);
}
