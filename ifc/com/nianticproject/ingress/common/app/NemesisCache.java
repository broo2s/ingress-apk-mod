package com.nianticproject.ingress.common.app;

import com.nianticproject.ingress.common.rpc.InventoryListener;
import com.nianticproject.ingress.gameentity.GameEntity;

import java.util.Collection;

public interface NemesisCache {

    public Collection<GameEntity> getInventory();

    public int getInventorySize();

    public void addInventoryListener(InventoryListener listener);

    public void removeInventoryListener(InventoryListener listener);
}
