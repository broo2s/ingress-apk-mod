package com.nianticproject.ingress.common.ui.widget;

import broot.ingress.mod.Entry;
import broot.ingress.sim.SimUi;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.tablelayout.Value;
import com.nianticproject.ingress.common.inventory.MenuControllerImpl;
import com.nianticproject.ingress.common.ui.widget.AudibleTwoTextButton;

public class MenuTopWidget extends Table {

    private final com.nianticproject.ingress.common.ui.widget.MenuTabId currTabId;
    private final Skin skin;
    private final ScrollPane scroll;
    private final Table tabsTable;
    private Table scrollIndicatorLeft;
    private Table scrollIndicatorRight;

    public MenuTopWidget(Skin skin, int width, MenuControllerImpl controller, com.nianticproject.ingress.common.ui.widget.MenuTabId currTabId) {
        this.skin = skin;
        this.currTabId = currTabId;
        setWidth(width);

        Table titleTable = new Table();
        titleTable.setBackground(skin.getDrawable("ops-title-background"));
        Label opsLabel = new Label("OPS", skin, "ops-title");
        opsLabel.setAlignment(Align.right, Align.right);
        titleTable.add(opsLabel).expandX().fillX().right().padRight(Value.percentWidth(0.02F));

        this.tabsTable = new Table();
        this.scroll = new ScrollPane(this.tabsTable, new ScrollPane.ScrollPaneStyle()) {
            @Override
            public float getMinHeight() {
                return super.getPrefHeight();
            }
        };
        this.scroll.setWidth(width);
        this.scroll.setScrollingDisabled(false, true);
        this.scroll.setSmoothScrolling(false);

        Table indicatorssTable = new Table();
        this.scrollIndicatorLeft = new Table();
        this.scrollIndicatorLeft.setBackground(skin.getDrawable("ops-scroll-indicator-left"));
        indicatorssTable.add(this.scrollIndicatorLeft).expand().fill().left().width(Value.percentWidth(0.1F));
        this.scrollIndicatorRight = new Table();
        this.scrollIndicatorRight.setBackground(skin.getDrawable("ops-scroll-indicator-right"));
        indicatorssTable.add(this.scrollIndicatorRight).expand().fill().right().width(Value.percentWidth(0.1F));

        Table rootTable = new Table();
        rootTable.add(titleTable).expandX().fillX();
        rootTable.row();
        rootTable.stack(this.scroll, indicatorssTable);

        Table closeBtnTable = new Table();
        closeBtnTable.setWidth(getWidth());
        closeBtnTable.setHeight(getHeight());
        Button closeBtn = new Button(skin, "ops-close");
        closeBtnTable.add(closeBtn).expand().left().top();

        stack(rootTable, closeBtnTable);
    }

    public void createTabs() {
        this.tabsTable.reset();
        this.tabsTable.add();

        AudibleTwoTextButton.TwoTextButtonStyle uncheckedStyle = this.skin.get("tab-bar-unchecked", AudibleTwoTextButton.TwoTextButtonStyle.class);
        AudibleTwoTextButton.TwoTextButtonStyle checkedStyle = this.skin.get("tab-bar-checked", AudibleTwoTextButton.TwoTextButtonStyle.class);

        float f1 = 0.02F * this.scroll.getWidth();
        float f2 = 0.035F * this.scroll.getWidth();
        float f3 = 0.08F * this.scroll.getWidth();

        AudibleTwoTextButton currTabBtn = null;
        for (final com.nianticproject.ingress.common.ui.widget.MenuTabId tabId : Entry.MenuTopWidget_getTabs()) {
            AudibleTwoTextButton btn;
            if (this.currTabId == tabId) {
                btn = new AudibleTwoTextButton(tabId.toString(), tabId.getText2(), checkedStyle);
                btn.pad(f1, f3, f2, f3);
                currTabBtn = btn;
            } else {
                btn = new AudibleTwoTextButton(tabId.toString(), tabId.getText2(), uncheckedStyle);
                btn.pad(f1, f3, f1, f3);
                btn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        SimUi.obj.selectTab(tabId);
                    }
                });
            }
            this.tabsTable.add(btn).expandY().top();
        }
        this.scroll.layout();
        this.scroll.setScrollX(-getWidth() / 2.0F + currTabBtn.getX() + currTabBtn.getWidth() / 2.0F);
    }

    public final void act(float delta) {
        super.act(delta);
        float pos = this.scroll.getScrollPercentX();
        if (pos == 0.0F) {
            this.scrollIndicatorLeft.setVisible(false);
            this.scrollIndicatorRight.setVisible(true);
            return;
        }
        if (pos == 1.0F) {
            this.scrollIndicatorLeft.setVisible(true);
            this.scrollIndicatorRight.setVisible(false);
            return;
        }
        this.scrollIndicatorLeft.setVisible(true);
        this.scrollIndicatorRight.setVisible(true);
    }
}
