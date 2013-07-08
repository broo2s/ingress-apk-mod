package broot.ingress.mod;

import broot.ingress.mod.util.Config;
import broot.ingress.mod.util.MenuUtils;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;

import java.util.ArrayList;
import java.util.List;

public class Entry {

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
}
