package io.github.some_example_name.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.core.GameEngine;
import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Slot;
import io.github.some_example_name.ui.elements.EnemyUI;
import io.github.some_example_name.ui.elements.SlotUI;
import io.github.some_example_name.ui.elements.UnitUI;

public class BoardUI extends Table {

  private final GameContext context;
  private final GameEngine engine;
  private final Skin skin;

  private EnemyUI enemyUI;
  private Table slotsRow;
  private Label playerLabel;

  public BoardUI(GameContext context, GameEngine engine, Skin skin) {
    this.context = context;
    this.engine = engine;
    this.skin = skin;

    // фон
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.BLUE);
    pixmap.fill();
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    this.setBackground(new TextureRegionDrawable(texture));

    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    // Enemy сверху
    enemyUI = new EnemyUI(enemy, skin);
    this.add(enemyUI).expandX().top().center().padTop(10).row();

    // пустое пространство
    this.add().expand().row();

    // Слоты игрока
    slotsRow = new Table();
    for (Slot slot : player.getSlots()) {
      SlotUI slotUI = new SlotUI(slot, skin);
      slotsRow.add(slotUI).size(40, 30).pad(40);
    }

    // Лейбл игрока
    playerLabel = new Label(
        "Player HP: " + player.getHealth() + "/" + player.getMaxHealth() +
            " Mana: " + player.getMana() + "/" + player.getMaxMana(),
        skin);

    this.add(slotsRow).expandX().top().center().padBottom(10).row();
    this.add(playerLabel).expandX().top().center().padBottom(10).row();
  }

  public Object findTargetAt(float stageX, float stageY) {
    for (Actor slotActor : slotsRow.getChildren()) {
      if (slotActor instanceof SlotUI) {
        SlotUI slotUI = (SlotUI) slotActor;
        Vector2 pos = slotUI.localToStageCoordinates(new Vector2(0, 0));
        if (stageX >= pos.x && stageX <= pos.x + slotUI.getWidth()
            && stageY >= pos.y && stageY <= pos.y + slotUI.getHeight()) {
          return slotUI.getSlot();
        }
      }
    }
    return null;
  }

  public void refresh() {
    Player player = context.getPlayer();

    // обновляем enemy UI
    this.getCell(enemyUI).setActor(new EnemyUI(context.getEnemy(), skin));
    enemyUI = (EnemyUI) this.getCells().get(0).getActor();

    // обновляем слоты
    slotsRow.clearChildren();
    for (Slot slot : player.getSlots()) {
      if (slot.getUnit() != null) {
        UnitUI unitUI = new UnitUI(slot.getUnit(), skin);
        slotsRow.add(unitUI).size(80, 120).pad(10);
      } else {
        SlotUI slotUI = new SlotUI(slot, skin);
        slotsRow.add(slotUI).size(40, 30).pad(40);
      }
    }

    // обновляем лейбл игрока
    playerLabel.setText(
        "Player HP: " + player.getHealth() + "/" + player.getMaxHealth() +
            " Mana: " + player.getMana() + "/" + player.getMaxMana());
  }

  // Получить EnemyUI
  public EnemyUI getEnemyUI() {
    return enemyUI;
  }

  // Получить список живых UnitUI
  public ArrayList<UnitUI> getPlayerUnitUIs() {
    ArrayList<UnitUI> result = new ArrayList<>();
    for (Actor child : slotsRow.getChildren()) {
      if (child instanceof UnitUI) {
        UnitUI unitUI = (UnitUI) child;
        if (unitUI.getUnit().isAlive())
          result.add(unitUI);
      }
    }
    return result;
  }
}
