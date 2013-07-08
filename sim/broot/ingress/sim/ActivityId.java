package broot.ingress.sim;

import broot.ingress.mod.AboutModActivity;
import broot.ingress.mod.ModItemsActivity;
import com.nianticproject.ingress.common.ui.BaseSubActivity;

public enum ActivityId {
    MOD_ABOUT(AboutModActivity.class),
    MOD_ITEMS(ModItemsActivity.class),
    ;

    public final Class<? extends BaseSubActivity> class_;

    private ActivityId(Class<? extends BaseSubActivity> class_) {
        this.class_ = class_;
    }
}
