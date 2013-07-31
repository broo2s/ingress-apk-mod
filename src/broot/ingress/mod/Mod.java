package broot.ingress.mod;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import broot.ingress.mod.util.Config;
import broot.ingress.mod.util.UiVariant;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nianticproject.ingress.NemesisActivity;
import com.nianticproject.ingress.common.app.NemesisMemoryCache;
import com.nianticproject.ingress.common.app.NemesisWorld;
import com.nianticproject.ingress.common.assets.AssetFinder;
import com.nianticproject.ingress.common.inventory.MenuControllerImpl;
import com.nianticproject.ingress.common.scanner.visuals.EnergyGlobVisuals;
import com.nianticproject.ingress.common.scanner.visuals.PortalParticleRender;
import com.nianticproject.ingress.common.ui.elements.PortalInfoDialog;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;

import java.util.ArrayList;
import java.util.List;

public class Mod {

    public static Application app;
    public static NemesisActivity nemesisActivity;
    public static NemesisWorld world;
    public static NemesisMemoryCache cache;
    public static MenuControllerImpl menuController;
    public static AssetFinder assetFinder;
    public static Skin skin;

    public static PortalInfoDialog portalInfoDialog;

    public static DisplayMetrics displayMetrics;
    public static UiVariant currUiVariant;

    public static PowerManager.WakeLock ksoWakeLock;

    public static void init() {
//        Debug.waitForDebugger();
    }

    public static void onConfigLoaded() {
        PortalParticleRender.enabled = Config.portalParticlesEnabled;
//        EnergyGlobVisuals.initEnabled = Config.xmGlobsEnabled;
    }

    public static void updateFullscreenMode() {
        nemesisActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WindowManager.LayoutParams attrs = nemesisActivity.getWindow().getAttributes();
                if (Config.fullscreen) {
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                } else {
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                }
                nemesisActivity.getWindow().setAttributes(attrs);
            }
        });
    }

    public static void updateKeepScreenOn() {
        nemesisActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Config.keepScreenOn) {
                    if (!ksoWakeLock.isHeld()) {
                        ksoWakeLock.acquire();
                    }
                } else {
                    if (ksoWakeLock.isHeld()) {
                        ksoWakeLock.release();
                    }
                }
            }
        });
    }

    public static void restartApp() {
        Context ctx = Mod.app;
        Intent i = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ctx, 0, i, 0));

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void updateCurrUiVariant() {
        currUiVariant = Config.uiVariant;
        if (currUiVariant != UiVariant.AUTO) {
            currUiVariant = Config.uiVariant;
            return;
        }

        List<String> names = new ArrayList<String>();
        switch (assetFinder.screenDensity) {
            case XXHIGH:
                names.add("data-xxhdpi");
            case XHIGH:
                names.add("data-xhdpi");
                break;
            case HIGH:
                break;
            case MEDIUM:
            case LOW:
                int w = Mod.displayMetrics.widthPixels;
                if (w < 320) {
                    names.add("data-qvga");
                    names.add("data-ingressopt-qvga");
                } else if (w < 480) {
                    names.add("data-hvga");
                    names.add("data-ingressopt-hvga");
                }
                break;
        }
        names.add("data");
        for (String name : names) {
            currUiVariant = UiVariant.byName.get(name);
            if (currUiVariant != null) {
                return;
            }
        }
        currUiVariant = UiVariant.variants.get(1);
    }

    public static String getFullVersion() {
        try {
            return "v" + app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName + "-broot-" + BuildConfig.MOD_VERSION;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
