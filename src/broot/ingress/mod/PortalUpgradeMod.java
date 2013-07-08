package broot.ingress.mod;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.nianticproject.ingress.common.PlayerLocation;
import com.nianticproject.ingress.common.model.BasePlayerListener;
import com.nianticproject.ingress.common.model.PlayerListener;
import com.nianticproject.ingress.common.ui.FormatUtils;
import com.nianticproject.ingress.common.upgrade.PortalUpgradeUi;
import com.nianticproject.ingress.gameentity.GameEntity;
import com.nianticproject.ingress.gameentity.components.LocationE6;
import com.nianticproject.ingress.shared.location.LocationUtils;

import java.util.ArrayList;
import java.util.List;

public class PortalUpgradeMod {

    private static GameEntity portalEntity;
    private static Label distLabel;
    private static PlayerListener playerListener;

    public static void onStatsTableCreated(PortalUpgradeUi ui, Table t) {
        init(ui);

        Label.LabelStyle style = Mod.skin.get("portal-stats", Label.LabelStyle.class);
        final float den = Mod.displayMetrics.density;

        List<Cell> cells = new ArrayList<Cell>(t.getCells());
        t.clear();
        t.left();
        t.defaults().left();
        t.add((Actor) cells.get(1).getWidget()).padLeft(20 * den);
        t.add((Actor) cells.get(2).getWidget()).padLeft(8 * den);
        t.add((Actor) cells.get(3).getWidget()).padLeft(16 * den);
        t.add((Actor) cells.get(4).getWidget()).padLeft(8 * den);
        t.row();
        t.add((Actor) cells.get(7).getWidget()).padLeft(20 * den);
        t.add((Actor) cells.get(8).getWidget()).padLeft(8 * den);
        t.add(new Label("Dist.:", style)).padLeft(16 * den);
        t.add(distLabel = new Label("", style)).padLeft(8 * den);

        updateDistLabel(Mod.world.getPlayerModel().getPlayerLocation());
    }

    public static void onDispose() {
        portalEntity = null;
        distLabel = null;
        Mod.world.getPlayerModel().removeListener(playerListener);
    }

    public static int getResonatorBrowserHeight(int withoutPad) {
        return withoutPad + (Mod.displayMetrics.heightPixels < 800 ? 0 : 30);
    }

    private static void init(PortalUpgradeUi ui) {
        portalEntity = ui.activity.portalEntity;
        playerListener = new BasePlayerListener() {
            @Override
            public String getName() {
                return PortalUpgradeMod.class.getSimpleName() + ":playerListener";
            }

            @Override
            public void onLocationChanged(PlayerLocation location) {
                updateDistLabel(location);
            }
        };
        Mod.world.getPlayerModel().addListener(playerListener);
    }

    private static void updateDistLabel(PlayerLocation location) {
        double dist = LocationUtils.calculateDistance(
                Mod.world.getPlayerModel().getPlayerLocation().getLatLng(),
                ((LocationE6) portalEntity.getComponent(LocationE6.class)).getLatLng());
        distLabel.setText(FormatUtils.formatDistance((float) dist));
    }
}
