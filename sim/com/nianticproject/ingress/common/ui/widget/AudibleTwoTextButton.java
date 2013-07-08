package com.nianticproject.ingress.common.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class AudibleTwoTextButton extends Button {

    public AudibleTwoTextButton(String text1, String text2, TwoTextButtonStyle style) {
        super(style);
        Label label1 = new Label(text1, new Label.LabelStyle(style.font, style.fontColor));
        Label label2 = new Label(text2, new Label.LabelStyle(style.font2, style.fontColor2));
        label1.setAlignment(1);
        label2.setAlignment(1);

        Table t = new Table();
        t.add(label1);
        t.add(label2);
        add(t).center().expand().fill();
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public static class TwoTextButtonStyle extends ButtonStyle {
        public Color checkedFontColor;
        public Color checkedFontColor2;
        public Color disabledFontColor;
        public Color disabledFontColor2;
        public Color downFontColor;
        public Color downFontColor2;
        public BitmapFont font;
        public BitmapFont font2;
        public Color fontColor;
        public Color fontColor2;
        public Color overFontColor;
        public Color overFontColor2;
    }
}
