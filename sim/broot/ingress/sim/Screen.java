package broot.ingress.sim;

public enum Screen {
    HD(720, 1280, 2f, 38, 111, "data-xhdpi"),
    WVGA(480, 800, 1.5f, 38, 111, "data"),
    HVGA(320, 480, 1, 25, 82, "data-hvga"),
    QVGA(240, 320, .75f, 19, 81, "data-qvga");

    public final int width;
    public final int height;
    public final float density;
    public final int statusBarHeight;
    public final int topWidgetHeight;
    public final String defaultAssetDir;

    private Screen(int width, int height, float density, int statusBarHeight, int topWidgetHeight, String defaultAssetDir) {
        this.width = width;
        this.height = height;
        this.density = density;
        this.statusBarHeight = statusBarHeight;
        this.topWidgetHeight = topWidgetHeight;
        this.defaultAssetDir = defaultAssetDir;
    }
}
