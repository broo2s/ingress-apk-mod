package broot.ingress.mod;

import broot.ingress.mod.util.Config;
import broot.ingress.mod.util.UiVariant;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.tablelayout.Value;
import com.nianticproject.ingress.common.scanner.render.PortalParticleRender;
import com.nianticproject.ingress.common.ui.BaseSubActivity;
import com.nianticproject.ingress.common.ui.UiLayer;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;
import com.nianticproject.ingress.common.ui.widget.MenuTopWidget;

import java.util.ArrayList;
import java.util.List;

public class AboutModActivity extends BaseSubActivity {

    private MenuTopWidget topWidget;
    private Table menuItemsTable;

    private ListItem gameplayTweaksItem;
    private ListItem tabsItem;
    private ListItem animsItem;
    private ListItem uiTweaksItem;
    private ListItem uiVariantItem;
    private ListItem uiHQItem;
    private ListItem restartItem;

    public AboutModActivity() {
        super(AboutModActivity.class.getName());

        getRenderer().addUiLayer(new UiLayer() {
            @Override
            public void createUi(Skin skin, Stage stage) {
                menuItemsTable = new Table();
                menuItemsTable.top().pad(10);

                gameplayTweaksItem = new ListItem(skin, "Gameplay tweaks", null);
                gameplayTweaksItem.addButton("Default resonator", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.deployHighest = !Config.deployHighest;
                        updateGameplayTweaksValues(true);
                    }
                });
                addItem(gameplayTweaksItem);

                tabsItem = new ListItem(skin, "Menu tabs", null);
                tabsItem.addButton("[ITEMS]", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.nextItemsTab();
                        updateTabsValues(false);
                    }
                });
                tabsItem.addButton("ITEMS", "Show", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.showOrigItemsTab = !Config.showOrigItemsTab;
                        updateTabsValues(true);
                    }
                });
                tabsItem.addButton("INTEL", "Show", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.showIntelTab = !Config.showIntelTab;
                        updateTabsValues(true);
                    }
                });
                tabsItem.addButton("MISSION", "Show", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.showMissionTab = !Config.showMissionTab;
                        updateTabsValues(true);
                    }
                });
                tabsItem.addButton("RECRUIT", "Show", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.showRecruitTab = !Config.showRecruitTab;
                        updateTabsValues(true);
                    }
                });
                tabsItem.addButton("DEVICE", "Show", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.showDeviceTab = !Config.showDeviceTab;
                        updateTabsValues(true);
                    }
                });
                addItem(tabsItem);

                animsItem = new ListItem(skin, "Animations", null);
                animsItem.addButton("Globe intro", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.skipIntro = !Config.skipIntro;
                        updateAnimsValues(true);
                    }
                });
                animsItem.addButton("Scanner zoom in", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.scannerZoomInAnimEnabled = !Config.scannerZoomInAnimEnabled;
                        updateAnimsValues(true);
                    }
                });
                animsItem.addButton("New hacking", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.newHackAnimEnabled = !Config.newHackAnimEnabled;
                        updateAnimsValues(true);
                    }
                });
                animsItem.addButton("Item animations", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.itemAnimationsEnabled = !Config.itemAnimationsEnabled;
                        updateAnimsValues(true);
                    }
                });
                addItem(animsItem);

                uiTweaksItem = new ListItem(skin, "UI tweaks", null);
                uiTweaksItem.addButton("Fullscreen", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.fullscreen = !Config.fullscreen;
                        updateUiTweaksValues(true);
                        Mod.updateFullscreenMode();
                        restartItem.descLabel.setText("Restart is recommended");
                    }
                });
                uiTweaksItem.addButton("Portal vectors", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.showPortalVectors = !Config.showPortalVectors;
                        updateUiTweaksValues(true);
                    }
                });
                uiTweaksItem.addButton("Portal particles", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.portalParticlesEnabled = !Config.portalParticlesEnabled;
                        updateUiTweaksValues(true);
                        PortalParticleRender.enabled = Config.portalParticlesEnabled;
                    }
                });
                uiTweaksItem.addButton("Scanner objects", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.scannerObjectsEnabled = !Config.scannerObjectsEnabled;
                        updateUiTweaksValues(true);
                    }
                });
                uiTweaksItem.addButton("Simplify Items", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.simplifyInventoryItems = !Config.simplifyInventoryItems;
                        updateUiTweaksValues(true);
                    }
                });
                addItem(uiTweaksItem);

                uiHQItem = new ListItem(skin, "High Quality", null);
                uiHQItem.addButton("XM", "", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.hqXM = !Config.hqXM;
                        updateUiHQValues(true);
                        restartItem.descLabel.setText("Restart is required");
                    }
                });
                addItem(uiHQItem);


                addItem(uiVariantItem = new ListItem(skin, "UI variant", "", "Toggle", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Config.nextUiVariant();
                        Mod.updateCurrUiVariant();
                        updateUiVariantValue();
                        restartItem.descLabel.setText("Restart is recommended");
                    }
                }));

                addItem(restartItem = new ListItem(skin, "Restart", "", "Restart app", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Mod.restartApp();
                    }
                }));

                addItem(new ListItem(skin, "Mod version", Mod.getFullVersion()));

                Table root = new Table();
                root.setFillParent(true);
                topWidget = new MenuTopWidget(skin, (int) stage.getWidth(), Mod.menuController, MenuTabId.MOD_ABOUT);
                topWidget.createTabs();
                root.add(topWidget);
                root.row();
                root.add(new ScrollPane(menuItemsTable)).expand().fill().pad(2);

                stage.addActor(root);
            }

            private void addItem(ListItem item) {
                menuItemsTable.add(item).expandX().fillX().padBottom(-2);
                menuItemsTable.row();
            }

            @Override
            public boolean dontDispose(float f1) {
                return true;
            }

            @Override
            public void dispose() {
            }
        });
    }

    @Override
    protected void onResume() {
        updateGameplayTweaksValues(false);
        updateTabsValues(false);
        updateAnimsValues(false);
        updateUiTweaksValues(false);
        updateUiHQValues(false);
        updateUiVariantValue();
    }

    private void updateGameplayTweaksValues(boolean save) {
        if (save) {
            Config.save();
        }
        gameplayTweaksItem.buttons.get(0).setText(Config.deployHighest ? "Highest" : "Lowest");
    }

    private void updateTabsValues(boolean save) {
        if (save) {
            Config.save();
        }
        tabsItem.buttons.get(0).setText(Config.itemsTab.desc);
        tabsItem.buttons.get(1).setText(Config.showOrigItemsTab ? "Show" : "Hide");
        tabsItem.buttons.get(2).setText(Config.showIntelTab ? "Show" : "Hide");
        tabsItem.buttons.get(3).setText(Config.showMissionTab ? "Show" : "Hide");
        tabsItem.buttons.get(4).setText(Config.showRecruitTab ? "Show" : "Hide");
        tabsItem.buttons.get(5).setText(Config.showDeviceTab ? "Show" : "Hide");
        topWidget.createTabs();
    }

    private void updateAnimsValues(boolean save) {
        if (save) {
            Config.save();
        }
        animsItem.buttons.get(0).setText(!Config.skipIntro ? "ON" : "OFF");
        animsItem.buttons.get(1).setText(Config.scannerZoomInAnimEnabled ? "ON" : "OFF");
        animsItem.buttons.get(2).setText(Config.newHackAnimEnabled ? "ON" : "OFF");
        animsItem.buttons.get(3).setText(Config.itemAnimationsEnabled ? "ON" : "OFF");
    }

    private void updateUiTweaksValues(boolean save) {
        if (save) {
            Config.save();
        }
        uiTweaksItem.buttons.get(0).setText(Config.fullscreen ? "ON" : "OFF");
        uiTweaksItem.buttons.get(1).setText(Config.showPortalVectors ? "ON" : "OFF");
        uiTweaksItem.buttons.get(2).setText(Config.portalParticlesEnabled ? "ON" : "OFF");
        uiTweaksItem.buttons.get(3).setText(Config.scannerObjectsEnabled ? "ON" : "OFF");
        uiTweaksItem.buttons.get(4).setText(Config.simplifyInventoryItems ? "ON" : "OFF");
    }

    private void updateUiHQValues(boolean save) {
        if (save) {
            Config.save();
        }
        uiHQItem.buttons.get(0).setText(Config.hqXM ? "ON" : "OFF");
    }

    private void updateUiVariantValue() {
        String text = Config.uiVariant.desc;
        if (Config.uiVariant == UiVariant.AUTO) {
            text += " (" + Mod.currUiVariant.desc + ")";
        }
        uiVariantItem.descLabel.setText(text);
    }

    @Override
    protected String getName() {
        return AboutModActivity.class.getName();
    }


    public static class ListItem extends Table {
        public Label nameLabel;
        public Label descLabel;
        public final List<TextButton> buttons = new ArrayList<TextButton>();

        private final Skin skin;
        private final Label.LabelStyle smallWhite;
        private Table buttonsTable;

        public ListItem(Skin skin, String name, String desc) {
            this.skin = skin;
            smallWhite = skin.get("small-white", Label.LabelStyle.class);

            setBackground(skin.getDrawable("nav-button-clear"));

            Table t1 = new Table();
            t1.setBackground(skin.getDrawable("nav-button"));
            t1.add(nameLabel = new Label(name, smallWhite)).pad(0, 8, 0, 8);
            add(t1).left().top().pad(8, -8, 8, 8);

            add(descLabel = new Label(desc, smallWhite)).expandX().left();
            descLabel.setWrap(true);

            row();
        }

        public ListItem(Skin skin, String name, String desc, String value, EventListener listener) {
            this(skin, name, desc);
            addButton(null, value, listener);
        }

        public TextButton addButton(String name, String value, EventListener listener) {
            if (buttonsTable == null) {
                add(buttonsTable = new Table()).colspan(2).expandX().fillX().padBottom(8);
            }

            buttonsTable.row();
            if (name == null) {
                buttonsTable.add().expandX();
            } else {
                buttonsTable.add(new Label(name, smallWhite)).expandX().right().padRight(12);
            }

            TextButton button = new TextButton(value, skin);
            if (listener != null) {
                button.addListener(listener);
            }
            buttonsTable.add(button).width(Value.percentWidth(0.5F)).right().padRight(8);
            buttons.add(button);

            return button;
        }
    }
}
