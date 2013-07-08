package com.nianticproject.ingress.common.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nianticproject.ingress.gameentity.components.ItemRarity;

public class FormatUtils {

    public static Color getColorForLevel(Skin skin, int level) {
        return skin.getColor("level_" + level);
    }

    public static Color getColorForRarity(Skin skin, ItemRarity rarity) {
        return skin.getColor("rarity_" + rarity.ordinal());
    }

    public static String formatDistance(float dist) {
        return null;
    }
}
