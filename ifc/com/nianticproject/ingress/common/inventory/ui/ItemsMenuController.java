package com.nianticproject.ingress.common.inventory.ui;

import com.nianticproject.ingress.common.ui.widget.MenuController;
import com.nianticproject.ingress.gameentity.GameEntity;

public interface ItemsMenuController extends MenuController {

    public void showItemDetails(GameEntity entity);
}
