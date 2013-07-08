package broot.ingress.mod.util;

import broot.ingress.mod.Mod;
import com.nianticproject.ingress.gameentity.GameEntity;
import com.nianticproject.ingress.gameentity.components.Portal;
import com.nianticproject.ingress.gameentity.components.PortalCoupler;
import com.nianticproject.ingress.gameentity.components.Resource;
import com.nianticproject.ingress.shared.ItemType;

public class InventoryUtils {

    public static int getNumberOfPortalKeys(Portal portal) {
        String portalGuid = portal.getEntityGuid();
        int keysNumber = 0;
        for (GameEntity e : Mod.cache.getInventory()) {
            Resource res = (Resource) e.getComponent(Resource.class);
            if (res == null) {
                continue;
            }
            if (res.getResourceType() != ItemType.PORTAL_LINK_KEY) {
                continue;
            }
            PortalCoupler coupler = (PortalCoupler) e.getComponent(PortalCoupler.class);
            if (coupler == null) {
                continue;
            }
            if (portalGuid.equals(coupler.getPortalGuid())) {
                keysNumber++;
            }
        }
        return keysNumber;
    }
}
