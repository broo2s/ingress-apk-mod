package broot.ingress.sim;

import broot.ingress.mod.Mod;
import broot.ingress.mod.util.Config;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nianticproject.ingress.common.ui.BaseSubActivity;
import com.nianticproject.ingress.common.ui.UiLayer;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimUi implements ApplicationListener {

    public static Screen sc;
    public static String assetDir;
    public static ActivityId initialActivity;

    public static SimUi obj;

    public Stage stage;

    public BaseSubActivity activity;
    public UiLayer uiLayer;

    public static void main(String[] args) {
        sc = args.length >= 1 ? Screen.valueOf(args[0].toUpperCase()) : Screen.WVGA;
        assetDir = args.length >= 2 ? args[1] : sc.defaultAssetDir;
        initialActivity = args.length >= 3 ? ActivityId.valueOf(args[2].toUpperCase()) : ActivityId.MOD_ABOUT;

        Config.load();

        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.width = sc.width;
        cfg.height = sc.height - sc.statusBarHeight;

        obj = new SimUi();
        new LwjglApplication(obj, cfg);
    }

    @Override
    public void create() {
        Path orig = Paths.get("app/assets/common/" + assetDir + "/nemesis.json");
        Path mod = orig.resolveSibling("nemesis-mod.json");
        removeMissingClassesFromSkin(orig, mod);

        Mod.skin = new Skin(
                new FileHandle(mod.toFile()),
                new TextureAtlas(new FileHandle("app/assets/packed/" + assetDir + "/common.atlas")));

        try {
            Files.delete(mod);
        } catch (IOException e) {
            throw new RuntimeException();
        }

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        showActivity(initialActivity);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void pause() {
        activity.pause();
    }

    @Override
    public void resume() {
        activity.resume();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void selectTab(MenuTabId tabId) {
        switch (tabId) {
            case MOD_ABOUT:
                showActivity(ActivityId.MOD_ABOUT);
                break;
            case MOD_ITEMS:
                showActivity(ActivityId.MOD_ITEMS);
                break;
        }
    }

    public void showActivity(ActivityId id) {
        try {
            this.activity = id.class_.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        stage.clear();
        uiLayer.createUi(Mod.skin, stage);
        activity.resume();
    }

    private static void removeMissingClassesFromSkin(Path orig, Path mod) {
        try {
            byte[] encoded = Files.readAllBytes(orig);
            String s = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();

            Matcher m = Pattern.compile("^  (.+?): \\{$.*?^  \\},?$", Pattern.MULTILINE | Pattern.DOTALL).matcher(s);
            StringBuffer s2 = new StringBuffer();
            while (m.find()) {
                String repl;
                try {
                    Class.forName(m.group(1), false, SimUi.class.getClassLoader());
                    repl = "$0";
                } catch (ClassNotFoundException e) {
                    repl = "";
                }
                m.appendReplacement(s2, repl);
            }
            m.appendTail(s2);
            Files.write(mod, s2.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
