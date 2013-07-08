package com.nianticproject.ingress.common.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface UiLayer {

    public void createUi(Skin skin, Stage stage);

    public boolean dontDispose(float f1);

    public void dispose();
}
