package com.nianticproject.ingress.common.model;

import com.nianticproject.ingress.common.PlayerLocation;

public interface PlayerListener {

    public String getName();
    public void onLocationChanged(PlayerLocation location);
}
