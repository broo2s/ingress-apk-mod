package com.nianticproject.ingress.common.ui;

import broot.ingress.sim.SimUi;

public abstract class BaseSubActivity implements SubActivity {

    public BaseSubActivity(String s) {
    }

    public void resume() {
        onResume();
    }

    public void pause() {
        onPause();
    }

    protected UiRenderer getRenderer() {
        return new UiRenderer() {
            @Override
            public void addUiLayer(UiLayer layer) {
                SimUi.obj.uiLayer = layer;
            }
        };
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    abstract protected String getName();
}
