package broot.ingress.mod.util;

import broot.ingress.mod.BuildConfig;

import java.util.*;

public final class UiVariant {

    public static final UiVariant AUTO = new UiVariant("auto", "Auto");
    public static final List<UiVariant> variants;
    public static final Map<String, UiVariant> byName;

    static {
        List<String> avail = new ArrayList<String>(Arrays.asList(BuildConfig.AVAILABLE_ASSETS));
        variants = new ArrayList<UiVariant>(avail.size() + 1);
        variants.add(AUTO);

        for (UiVariant variant : new UiVariant[] {
                new UiVariant("data-xxhdpi", "Original xxhdpi", "data-xhdpi"),
                new UiVariant("data-xhdpi", "Original xhdpi", "data"),
                new UiVariant("data", "Original normal"),
                new UiVariant("data-hvga", "Mod HVGA"),
                new UiVariant("data-qvga", "Mod QVGA"),
                new UiVariant("data-ingressopt-hvga", "Ingressopt HVGA"),
                new UiVariant("data-ingressopt-qvga", "Ingressopt QVGA"),
        }) {
            if (avail.remove(variant.name)) {
                variants.add(variant);
            }
        }
        Collections.sort(avail);
        for (String name : avail) {
            variants.add(new UiVariant(name, "Custom " + name));
        }

        byName = new HashMap<String, UiVariant>(variants.size());
        for (UiVariant variant : variants) {
            byName.put(variant.name, variant);
        }
    }

    public final String name;
    public final String desc;
    public final String parent;

    private UiVariant(String name, String desc) {
        this(name, desc, null);
    }

    private UiVariant(String name, String desc, String parent) {
        this.name = name;
        this.desc = desc;
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UiVariant uiVariant = (UiVariant) o;

        if (!name.equals(uiVariant.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
