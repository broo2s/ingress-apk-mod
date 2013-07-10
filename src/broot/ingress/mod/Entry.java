package broot.ingress.mod;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import broot.ingress.mod.util.Config;
import broot.ingress.mod.util.InventoryUtils;
import broot.ingress.mod.util.MenuUtils;
import broot.ingress.mod.util.ShaderFactory;
import broot.ingress.mod.util.UiVariant;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.nianticproject.ingress.NemesisActivity;
import com.nianticproject.ingress.common.app.NemesisMemoryCache;
import com.nianticproject.ingress.common.app.NemesisMemoryCacheFactory;
import com.nianticproject.ingress.common.app.NemesisWorld;
import com.nianticproject.ingress.common.assets.AssetFinder;
import com.nianticproject.ingress.common.inventory.MenuControllerImpl;
import com.nianticproject.ingress.common.ui.BaseSubActivity;
import com.nianticproject.ingress.common.ui.FormatUtils;
import com.nianticproject.ingress.common.ui.elements.PortalInfoDialog;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;
import com.nianticproject.ingress.common.upgrade.PortalUpgradeUi;
import com.nianticproject.ingress.gameentity.components.LocationE6;
import com.nianticproject.ingress.shared.ClientType;
import com.nianticproject.ingress.shared.location.LocationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Entry {

  private static Label portalInfoDistLabel;
  private static boolean uiHQ_XM_value = true;

  static {
    Mod.init();
  }

  public static void NemesisApp_onOnCreate(Application app) {
    Mod.app = app;
    Config.load();
    Mod.displayMetrics = new DisplayMetrics();
    ((WindowManager) app.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(Mod.displayMetrics);
    uiHQ_XM_value = Config.hqXM;
  }

  public static void NemesisActivity_onOnCreate(NemesisActivity activity) {
    Mod.nemesisActivity = activity;
    Mod.updateFullscreenMode();
  }

  public static void NemesisWorld_onInit(NemesisWorld world) {
    Mod.world = world;
  }

  public static void SubActivityManager_onInit(List<BaseSubActivity> activities) {
    activities.add(new AboutModActivity());
    activities.add(new ModItemsActivity());
  }

  public static void MenuController_onInit(MenuControllerImpl menuController) {
    Mod.menuController = menuController;
  }

  public static void AssetFinder_onInit(AssetFinder assetFinder) {
    Mod.assetFinder = assetFinder;
    Mod.updateCurrUiVariant();
  }

  // At this point most stuff should be already initialized
  public static void SubActiityApplicationLisener_onCreated() {
    Mod.cache = (NemesisMemoryCache) NemesisMemoryCacheFactory.getCache();
    Mod.skin = Mod.world.getSubActivityManager().skin;
  }

  public static Class<?> MenuShowBtn_onClick() {
    return MenuUtils.getActivityClassForMenuTabId(MenuUtils.getTabs()[0]);
  }

  public static MenuTabId[] MenuTopWidget_getTabs() {
    return MenuUtils.getTabs();
  }

  public static String MenuTabId_onToString(MenuTabId tab) {
    switch (tab) {
      case MOD_ABOUT:
        return "[MOD]";
      case MOD_ITEMS:
        return "[ITEMS]";
    }
    return null;
  }

  public static void MenuControllerImpl_onSelectTab(MenuTabId tabId) {
    Mod.world.getSubActivityManager().replaceForegroundActivity(MenuUtils.getActivityClassForMenuTabId(tabId));
  }

  public static FileHandle AssetFinder_onGetAssetPath(String in) {
    if (!in.startsWith("{data:")) {
      return null;
    }
    int pos1 = in.indexOf("/data/", 6);
    int pos2 = in.indexOf(",", pos1 + 6);
    String pre = in.substring(6, pos1) + "/";
    String post = "/" + in.substring(pos1 + 6, pos2);

    UiVariant variant = Mod.currUiVariant;
    while (variant != null) {
      FileHandle file = Gdx.files.internal(pre + variant.name + post);
      if (file.exists()) {
        return file;
      }
      variant = UiVariant.byName.get(variant.parent);
    }
    return null;
  }

  public static void PortalInfoDialog_onStatsTableCreated(PortalInfoDialog dialog, Table t) {
    Mod.portalInfoDialog = dialog;

    Label.LabelStyle style = Mod.skin.get("portal-stats", Label.LabelStyle.class);

    List<Cell> cells = new ArrayList<Cell>(t.getCells());
    t.clear();
    t.add((Actor) cells.get(0).getWidget()).left();
    t.add((Actor) cells.get(1).getWidget()).left().expandX();
    t.row();
    t.add((Actor) cells.get(3).getWidget()).left();
    t.add((Actor) cells.get(4).getWidget()).left().expandX();
    t.row();
    t.add(new Label("Keys:", style)).left();
    t.add(new Label(String.valueOf(InventoryUtils.getNumberOfPortalKeys(dialog.portalComponent)), style)).left().expandX();
    t.row();
    t.add(new Label("Dist.:", style)).left();
    t.add(portalInfoDistLabel = new Label("", style)).left().expandX();
  }

  public static void PortalInfoDialog_onPlayerLocationChanged() {
    double dist = LocationUtils.calculateDistance(
            Mod.world.getPlayerModel().getPlayerLocation().getLatLng(),
            ((LocationE6) Mod.portalInfoDialog.portalComponent.getEntity().getComponent(LocationE6.class)).getLatLng());
    portalInfoDistLabel.setText(FormatUtils.formatDistance((float) dist));
  }

  public static void PortalUpgrade_onStatsTableCreated(PortalUpgradeUi ui, Table t) {
    PortalUpgradeMod.onStatsTableCreated(ui, t);
  }

  public static void PortalUpgrade_onDispose() {
    PortalUpgradeMod.onDispose();
  }

  public static int PortalUpgrade_getResonatorBrowserHeight(int withoutPad) {
    return PortalUpgradeMod.getResonatorBrowserHeight(withoutPad);
  }

  public static boolean ScannerStateManager_onEnablePortalVectors() {
    return Config.showPortalVectors;
  }

  public static Map PlayerModelUtils_onGetDefaultResonatorToDeploy(TreeMap map) {
    return Config.deployHighest ? map.descendingMap() : map;
  }

  public static boolean ZoomInMode_shouldZoomIn() {
    return Config.scannerZoomInAnimEnabled;
  }

  public static float PortalInfoDialog_getOpenDelay(float orig) {
    return Config.scannerZoomInAnimEnabled ? orig : 0;
  }

  public static boolean ClientFeatureKnobBundle_getEnableNewHackAnimations(boolean orig) {
    return orig && Config.newHackAnimEnabled;
  }

  public static boolean InventoryItemRenderer_simplifyItems() {
    return Config.simplifyInventoryItems;
  }

  public static boolean ItemActionHandler_itemAnimationsEnabled() {
    return Config.itemAnimationsEnabled;
  }

  public static boolean uiHQ_XM() {
    return uiHQ_XM_value;
  }

  public static ClientType getClientType() {
    return ClientType.DEVELOPMENT;
  }

  public static ClientType getClientTypeForJackson() {
    return ClientType.PRODUCTION;
  }

  public static boolean shouldSkipIntro() {
    return Config.skipIntro;
  }

  public static boolean shouldDrawScannerObject() {
    return Config.scannerObjectsEnabled;
  }

  public static ShaderProgram ShaderUtils_compileShader(String vertex, String frag, String name) {

    if (!Entry.uiHQ_XM() && name.equals("shaders/particle_xm.glsl.vert")) {
      vertex = ShaderFactory.start(vertex)
              .findLine(".*varying float v_alpha;.*").removeLine()
              .findLine(".*float tModulus =.*").removeLine()
              .findLine(".*float timeOffset =.*").removeLine()
              .findLine(".*float elapsedTime =.*").removeLine()
              .findLine(".*float spread =.*").removeLine()
              .findLine(".*float hoover =.*").removeLine()
              .findLine(".*float tm =.*").removeLine()
              .findLine(".*float t =.*").removeLine()
              .findLine(".*v_alpha =.*").removeLine()
              .findLine(".*v_alpha =.*").removeLine()
              .findLine(".*v_alpha \\*=.*").removeLine()
              .findLine(".*float normIndex =.*").removeLine()
              .findLine(".*vec3 dynamics =.*").removeLine()
              .findLine(".*vec3 hooverDynamics =.*").removeLine()
              .findLine(".*v_texCoord0 =.*").removeLine()
              .findLine("^\\s*void main.*")
              .findLineDown("^\\s*vec2 scales.*").replace("a_scale", "0.5 * a_scale")
              .findLineDown("^\\s*vec4\\(position.*").replace("+ dynamics + hooverDynamics", "")
              .commit();
      frag = ShaderFactory.start(frag)
              .findLine(".*varying float v_alpha;.*").removeLine()
              .findLine("^\\s*void main.*")
              .findLineDown("^\\s*vec4 texture.*").removeLine()
              .findLineDown("^\\s*gl_FragColor.*").replace(" texture.w * v_alpha)", "0.15);")
              .nextLine().removeLine()
              .commit();
    }
    return new ShaderProgram(vertex, frag);
  }
}
