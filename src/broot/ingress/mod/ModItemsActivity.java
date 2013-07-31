package broot.ingress.mod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nianticproject.ingress.common.inventory.ui.IndistinguishableItems;
import com.nianticproject.ingress.common.ui.BaseSubActivity;
import com.nianticproject.ingress.common.ui.FormatUtils;
import com.nianticproject.ingress.common.ui.UiLayer;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;
import com.nianticproject.ingress.common.ui.widget.MenuTopWidget;
import com.nianticproject.ingress.gameentity.GameEntity;
import com.nianticproject.ingress.gameentity.components.FlipCard;
import com.nianticproject.ingress.gameentity.components.FlipCardType;
import com.nianticproject.ingress.gameentity.components.ItemRarity;
import com.nianticproject.ingress.shared.ItemType;

import java.util.*;
import java.util.List;

public class ModItemsActivity extends BaseSubActivity {


    private TextButton.TextButtonStyle defaultClearStyle;

    private MenuTopWidget topWidget;

    private Label sumLabel;
    private Label keysLabel;

    private Map<ItemType, List<Button>> buttonsByLvl = new HashMap<ItemType, List<Button>>();
    private Map<Object, Map<ItemRarity, Button>> buttonsByRarity = new HashMap<Object, Map<ItemRarity, Button>>();

    final float den = Mod.displayMetrics.density;

    public ModItemsActivity() {
        super(ModItemsActivity.class.getName());

        getRenderer().addUiLayer(new UiLayer() {
            @Override
            public void createUi(Skin skin, Stage stage) {
                defaultClearStyle = new TextButton.TextButtonStyle();
                defaultClearStyle.down = skin.getDrawable("nav-button-clear-down");
                defaultClearStyle.up = skin.getDrawable("nav-button-clear");
                defaultClearStyle.font = skin.getFont("default-font");

                Table t = new Table(skin);
                t.top().pad(6.6f * den);

                t.row();
                t.add(sumLabel = new Label("", skin));
                addItemsTable(skin, t);

                Table root = new Table();
                root.setFillParent(true);
                topWidget = new MenuTopWidget(skin, (int) stage.getWidth(), Mod.menuController, MenuTabId.MOD_ITEMS);
                topWidget.createTabs();
                root.add(topWidget);
                root.row();
                root.add(new ScrollPane(t)).expand().fill().pad(2);

                stage.addActor(root);
            }

            private void addItemsTable(Skin skin, Table parent) {
                Table t = new Table();

                t.row();
                t.add();
                t.add(new Label("R", skin));
                t.add(new Label("X", skin));
                t.add(new Label("C", skin));
                t.add(new Label("M", skin));

                for (int lvl = 0; lvl < 8; lvl++) {
                    Color color = FormatUtils.getColorForLevel(skin, lvl + 1);

                    t.row().height(40 * den);

                    Label label1 = new Label("L" + (lvl + 1), skin);
                    label1.setColor(color);
                    t.add(label1).pad(-1, 0, -1, 16 * den);

                    for (ItemType type : new ItemType[]{ItemType.EMITTER_A, ItemType.EMP_BURSTER, ItemType.POWER_CUBE,
                            ItemType.MEDIA}) {
                        List<Button> buttons = buttonsByLvl.get(type);
                        if (buttons == null) {
                            buttons = new ArrayList<Button>(8);
                            buttonsByLvl.put(type, buttons);
                        }
                        Button btn = new Button();
                        buttons.add(btn);
                        btn.button.getLabel().setColor(color);
                        t.add(btn.button).width(0).expandX().fill().pad(-1, -1, -1, -1);
                    }
                }

                t.row();
                t.add().height(16 * den);

                addRarityRows(t, skin,
                        new String[]{"Shields", "Heat Sink", "Multi-hack"},
                        new Object[]{ItemType.RES_SHIELD, ItemType.HEATSINK, ItemType.MULTIHACK},
                        new ItemRarity[]{ItemRarity.COMMON, ItemRarity.RARE, ItemRarity.VERY_RARE});
                addRarityRows(t, skin,
                        new String[]{"Force Amp", "Link Amp", "Turret"},
                        new Object[]{ItemType.FORCE_AMP, ItemType.LINK_AMPLIFIER, ItemType.TURRET},
                        new ItemRarity[]{ItemRarity.RARE});
                addRarityRows(t, skin,
                        new String[]{"ADA Refactor", "JARVIS Virus"},
                        new Object[]{FlipCardType.ADA, FlipCardType.JARVIS},
                        new ItemRarity[]{ItemRarity.VERY_RARE});

                t.row();
                keysLabel = new Label("", skin);
                t.add(keysLabel).colspan(2).width(0).pad(-1, 0, -1, 0).left();

                parent.row();
                parent.add(t).expandX().fillX();
            }

            private void addRarityRows(Table t, Skin skin, String[] names, Object[] types, ItemRarity[] rarities) {
                for (int i = 0; i < types.length; i++) {
                    t.row().height(40 * den);
                    t.add(new Label(names[i], skin)).colspan(2).width(0).pad(-1, 0, -1, 0).left();

                    Map<ItemRarity, Button> buttons = new HashMap<ItemRarity, Button>(3);
                    buttonsByRarity.put(types[i], buttons);
                    for (ItemRarity rarity : new ItemRarity[]{ItemRarity.COMMON, ItemRarity.RARE, ItemRarity.VERY_RARE}) {
                        if (Arrays.binarySearch(rarities, rarity) < 0) {
                            t.add().pad(-1, -1, -1, -1);
                            continue;
                        }
                        Button btn = new Button();
                        btn.button.getLabel().setColor(FormatUtils.getColorForRarity(skin, rarity));
                        t.add(btn.button).width(0).fill().pad(-1, -1, -1, -1);
                        buttons.put(rarity, btn);
                    }
                }
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
        updateLabels();
        topWidget.createTabs();
    }

    private void updateLabels() {
        for (List<Button> buttons : buttonsByLvl.values()) {
            for (Button btn : buttons) {
                btn.button.setText("-");
            }
        }
        for (Map<ItemRarity, Button> buttons : buttonsByRarity.values()) {
            for (Button btn : buttons.values()) {
                btn.button.setText("-");
            }
        }

        int sum = 0;
        int keysNumber = 0;
        int media[] = new int[8];
        for (IndistinguishableItems items : IndistinguishableItems.fromItemsByPlayerInfo(null, Mod.cache.getInventory())) {
            sum += items.getCount();
            int lvl = items.getLevel() - 1;

            ItemType type = items.getType();
            Button btn;
            switch (type) {
                case MEDIA:
                    int curr = media[lvl];
                    if (curr == 0) {
                        buttonsByLvl.get(type).get(lvl).entity = items.getEntity();
                    }
                    media[lvl] += items.getCount();
                    continue;
                case EMITTER_A:
                case EMP_BURSTER:
                case POWER_CUBE:
                    btn = buttonsByLvl.get(type).get(lvl);
                    break;
                case PORTAL_LINK_KEY:
                    keysNumber += items.getCount();
                    continue;
                case RES_SHIELD:
                case FORCE_AMP:
                case HEATSINK:
                case LINK_AMPLIFIER:
                case MULTIHACK:
                case TURRET:
                    btn = buttonsByRarity.get(type).get(items.getRarity());
                    break;
                case FLIP_CARD:
                    btn = buttonsByRarity
                            .get(((FlipCard) items.getEntity().getComponent(FlipCard.class)).getFlipCardType())
                            .get(items.getRarity());
                    break;
                default:
                    continue;
            }

            if (btn != null) {
                btn.button.setText(String.valueOf(items.getCount()));
                btn.entity = items.getEntity();
            }
        }
        for (int lvl = 0; lvl < 8; lvl++) {
            buttonsByLvl.get(ItemType.MEDIA).get(lvl).button.setText(media[lvl]==0?"-":String.valueOf(media[lvl]));
        }
        sumLabel.setText("Number of all items: " + sum + " / 2000");
        keysLabel.setText("Keys:  " + keysNumber);
    }

    @Override
    protected String getName() {
        return ModItemsActivity.class.getName();
    }

    private class Button {

        public final TextButton button;
        public GameEntity entity;

        private Button() {
            button = new TextButton("", defaultClearStyle);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (entity == null) {
                        return;
                    }
                    Mod.menuController.showItemDetails(entity);
                }
            });
        }
    }
}
