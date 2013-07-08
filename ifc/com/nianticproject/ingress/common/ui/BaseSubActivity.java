package com.nianticproject.ingress.common.ui;

public abstract class BaseSubActivity implements SubActivity {

    public BaseSubActivity(String s) {
    }

    protected UiRenderer getRenderer() {
        return null;
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected abstract String getName();
}
