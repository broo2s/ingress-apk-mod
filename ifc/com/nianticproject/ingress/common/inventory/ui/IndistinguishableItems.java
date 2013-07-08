package com.nianticproject.ingress.common.inventory.ui;

import com.nianticproject.ingress.common.model.PlayerModel;
import com.nianticproject.ingress.gameentity.GameEntity;
import com.nianticproject.ingress.gameentity.components.ItemRarity;
import com.nianticproject.ingress.shared.ItemType;

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

    // playerModel jest chyba wykorzystywany do sortowania kluczy po odległości do bieżącej lokalizacji. Może być nullem
    public static List<IndistinguishableItems> fromItemsByPlayerInfo(PlayerModel playerModel, Collection items) {
        return null;
    }
}
