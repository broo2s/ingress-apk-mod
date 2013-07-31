package broot.ingress.mod.util;

import android.content.SharedPreferences;
import broot.ingress.mod.Mod;

import java.util.List;

public class Config {

    public static DeployBehavior deployBehavior;
    public static boolean swapTouchMenuButtons;

    public static ItemsTab itemsTab;
    public static boolean showOrigItemsTab;
    public static boolean showIntelTab;
    public static boolean showMissionTab;
    public static boolean showRecruitTab;
    public static boolean showDeviceTab;

    public static boolean skipIntro;
    public static boolean scannerZoomInAnimEnabled;
    public static boolean newHackAnimEnabled;
    public static boolean rotateInventoryItemsEnabled;
    public static boolean recycleAnimationsEnabled;

    public static boolean fullscreen;
    public static boolean showPortalVectors;
    public static boolean portalParticlesEnabled;
    public static boolean xmGlobsEnabled;
    public static boolean scannerObjectsEnabled;
    public static boolean simplifyInventoryItems;
    public static int chatTimeFormat;
    public static boolean vibration;
    public static boolean keepScreenOn;

    public static UiVariant uiVariant;

    public static void load() {
        SharedPreferences prefs = Mod.app.getSharedPreferences("mod", 0);

        deployBehavior = DeployBehavior.valueOf(prefs.getString("deployBehavior", "MANUAL"));
        swapTouchMenuButtons = prefs.getBoolean("swapTouchMenuButtons", false);

        itemsTab = ItemsTab.valueOf(prefs.getString("itemsTab", "HIDDEN"));
        showOrigItemsTab = prefs.getBoolean("showOrigItemsTab", true);
        showIntelTab = prefs.getBoolean("showIntelTab", true);
        showMissionTab = prefs.getBoolean("showMissionTab", true);
        showRecruitTab = prefs.getBoolean("showRecruitTab", true);
        showDeviceTab = prefs.getBoolean("showDeviceTab", true);

        skipIntro = prefs.getBoolean("skipIntro", false);
        scannerZoomInAnimEnabled = prefs.getBoolean("scannerZoomInAnimEnabled", true);
        newHackAnimEnabled = prefs.getBoolean("newHackAnimEnabled", true);
        rotateInventoryItemsEnabled = prefs.getBoolean("rotateInventoryItemsEnabled", true);
        recycleAnimationsEnabled = prefs.getBoolean("recycleAnimationsEnabled", true);

        fullscreen = prefs.getBoolean("fullscreen", false);
        showPortalVectors = prefs.getBoolean("showPortalVectors", true);
        portalParticlesEnabled = prefs.getBoolean("portalParticlesEnabled", true);
        xmGlobsEnabled = prefs.getBoolean("xmGlobsEnabled", true);
        scannerObjectsEnabled = prefs.getBoolean("scannerObjectsEnabled", true);
        simplifyInventoryItems = prefs.getBoolean("simplifyInventoryItems", false);
        chatTimeFormat = prefs.getInt("chatTimeFormat", 0);
        vibration = prefs.getBoolean("vibration", true);
        keepScreenOn = prefs.getBoolean("keepScreenOn", false);

        uiVariant = UiVariant.byName.get(prefs.getString("uiVariant", "auto"));
        if (uiVariant == null) {
            uiVariant = UiVariant.AUTO;
        }

        Mod.onConfigLoaded();
    }

    public static void save() {
        SharedPreferences.Editor e = Mod.app.getSharedPreferences("mod", 0).edit();

        e.putString("deployBehavior", deployBehavior.toString());
        e.putBoolean("swapTouchMenuButtons", swapTouchMenuButtons);

        e.putString("itemsTab", itemsTab.toString());
        e.putBoolean("showOrigItemsTab", showOrigItemsTab);
        e.putBoolean("showIntelTab", showIntelTab);
        e.putBoolean("showMissionTab", showMissionTab);
        e.putBoolean("showRecruitTab", showRecruitTab);
        e.putBoolean("showDeviceTab", showDeviceTab);

        e.putBoolean("skipIntro", skipIntro);
        e.putBoolean("scannerZoomInAnimEnabled", scannerZoomInAnimEnabled);
        e.putBoolean("newHackAnimEnabled", newHackAnimEnabled);
        e.putBoolean("rotateInventoryItemsEnabled", rotateInventoryItemsEnabled);
        e.putBoolean("recycleAnimationsEnabled", recycleAnimationsEnabled);

        e.putBoolean("fullscreen", fullscreen);
        e.putBoolean("showPortalVectors", showPortalVectors);
        e.putBoolean("portalParticlesEnabled", portalParticlesEnabled);
        e.putBoolean("xmGlobsEnabled", xmGlobsEnabled);
        e.putBoolean("scannerObjectsEnabled", scannerObjectsEnabled);
        e.putBoolean("simplifyInventoryItems", simplifyInventoryItems);
        e.putInt("chatTimeFormat", chatTimeFormat);
        e.putBoolean("vibration", vibration);
        e.putBoolean("keepScreenOn", keepScreenOn);

        e.putString("uiVariant", uiVariant.name);

        e.commit();
    }

    public static void nextItemsTab() {
        itemsTab = ItemsTab.values()[(itemsTab.ordinal() + 1) % ItemsTab.values().length];
        save();
    }
    
    public static void nextDeployBehavior() {
        deployBehavior = DeployBehavior.values()[(deployBehavior.ordinal() + 1) % DeployBehavior.values().length];
        save();
    }

    public static void nextUiVariant() {
        List<UiVariant> variants = UiVariant.variants;
        uiVariant = variants.get((variants.indexOf(uiVariant) + 1) % variants.size());
        save();
    }

    public static enum ItemsTab {
        HIDDEN("Hide"),
        AT_END("Last"),
        AT_START("First"),
        ;

        public final String desc;

        private ItemsTab(String desc) {
            this.desc = desc;
        }
    }
    
    public static enum DeployBehavior {
        MANUAL("Manual"),
        HIGHEST("Highest"),
        LOWEST("Lowest"),
        ;

        public final String desc;

        private DeployBehavior(String desc) {
            this.desc = desc;
        }
    }
}
