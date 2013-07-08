package com.nianticproject.ingress.common.inventory.ui;

import com.nianticproject.ingress.common.model.PlayerModel;
import com.nianticproject.ingress.gameentity.GameEntity;
import com.nianticproject.ingress.gameentity.components.ItemRarity;
import com.nianticproject.ingress.shared.ItemType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndistinguishableItems {

    public ItemType getType() {
        return null;
    }

    public int getLevel() {
        return 0;
    }

    public ItemRarity getRarity() {
        return null;
    }

    public int getCount() {
        return 0;
    }

    public GameEntity getEntity() {
        return null;
    }

    public static List<com.nianticproject.ingress.common.inventory.ui.IndistinguishableItems> fromItemsByPlayerInfo(PlayerModel playerModel, Collection items) {
        return new ArrayList<com.nianticproject.ingress.common.inventory.ui.IndistinguishableItems>();
    }
}
