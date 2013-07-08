package com.nianticproject.ingress.common.model;

import com.nianticproject.ingress.common.PlayerLocation;

public abstract class BasePlayerListener implements PlayerListener {

    @Override
    public void onLocationChanged(PlayerLocation location) {
    }
}
