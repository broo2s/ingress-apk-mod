package broot.ingress.mod;

import android.content.SharedPreferences;
import broot.ingress.mod.util.UiVariant;
import broot.ingress.sim.FakePrefs;
import broot.ingress.sim.Screen;
import broot.ingress.sim.SimUi;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nianticproject.ingress.common.inventory.MenuControllerImpl;
import com.nianticproject.ingress.gameentity.GameEntity;

import java.util.ArrayList;
import java.util.Collection;

public class Mod {
    public static UiVariant currUiVariant = UiVariant.byName.get(SimUi.assetDir);
    public static MenuControllerImpl menuController;
    public static Screen displayMetrics = SimUi.sc;
    public static Skin skin;
    public static FakeApplication app = new FakeApplication();
    public static FakeNemesisCache cache = new FakeNemesisCache();

    public static void onConfigLoaded() {
    }

    public static String getFullVersion() {
        return "";
    }

    public static void updateFullscreenMode() {
    }

    public static void updateCurrUiVariant() {
    }

    public static void updateMenuTabs() {
    }

    public static void restartApp() {
    }

    public static class FakeApplication {

        public SharedPreferences getSharedPreferences(String s, int i) {
            return new FakePrefs();
        }
    }

    public static class FakeNemesisCache {

        public Collection<GameEntity> getInventory() {
            return new ArrayList<GameEntity>();
        }
    }
}
