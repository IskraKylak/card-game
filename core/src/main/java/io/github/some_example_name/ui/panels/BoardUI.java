package io.github.some_example_name.ui.panels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.core.GameEngine;
import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.Entity;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Slot;
import io.github.some_example_name.model.Targetable;
import io.github.some_example_name.model.Unit;
import io.github.some_example_name.ui.elements.EnemyUI;
import io.github.some_example_name.ui.elements.EntityUI;
import io.github.some_example_name.ui.elements.PlayerUI;
import io.github.some_example_name.ui.elements.SlotUI;
import io.github.some_example_name.ui.elements.UnitUI;

public class BoardUI extends Table {

  private final GameContext context;
  private final GameEngine engine;
  private final Skin skin;

  private EnemyUI enemyUI;
  private Table slotsRow;
  private final PlayerUI playerUI;

  // Статический фон
  private Image backgroundImage;

  public BoardUI(GameContext context, GameEngine engine, Skin skin) {
    this.context = context;
    this.engine = engine;
    this.skin = skin;

    // Фон
    Texture backgroundTexture = new Texture("game/board/Board.png");
    Image backgroundImage = new Image(backgroundTexture);
    backgroundImage.setScaling(Scaling.fill);
    backgroundImage.setFillParent(true);
    this.addActor(backgroundImage);

    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    // Enemy сверху
    enemyUI = new EnemyUI(enemy, skin);
    this.add(enemyUI).expandX().top().center().padTop(10).row();

    // Пустое пространство
    this.add().expand().row();

    // Слоты игрока
    slotsRow = new Table();
    for (Slot slot : player.getSlots()) {
      SlotUI slotUI = new SlotUI(slot, skin);
      slotsRow.add(slotUI).size(40, 20).pad(40);
    }
    this.add(slotsRow).expandX().top().center().padBottom(10).row();

    // Пустое пространство
    this.add().expand().row();

    // 🔹 Добавляем PlayerUI как отдельный актёр
    float spriteWidth = 100;
    float spriteHeight = 90;
    playerUI = new PlayerUI(player, skin, "game/player", spriteWidth, spriteHeight);
    // this.addActor(playerUI);

    this.add(playerUI).expandX().bottom().center().padBottom(20).row();
  }

  @Override
  public void layout() {
    super.layout();

    if (playerUI != null) {
      float spriteWidth = playerUI.getWidth();
      float spriteHeight = playerUI.getHeight();

      float x = (getWidth() - spriteWidth) / 2f;
      float y = 20; // отступ от низа
      playerUI.setPosition(x, y);
    }
  }

  /**
   * Находит цель по stage-координатам. Теперь ищем попадание по SlotUI, а также
   * по UnitUI внутри SlotUI (если он есть).
   */
  public Targetable findTargetAt(float stageX, float stageY) {
    // 1) Проверяем слоты (сам контейнер SlotUI) — для розыгрыша UNIT
    for (Actor slotActor : slotsRow.getChildren()) {
      if (slotActor instanceof SlotUI) {
        SlotUI slotUI = (SlotUI) slotActor;
        Vector2 pos = slotUI.localToStageCoordinates(new Vector2(0, 0));
        if (stageX >= pos.x && stageX <= pos.x + slotUI.getWidth() && stageY >= pos.y
            && stageY <= pos.y + slotUI.getHeight()) {
          // Вернём Slot (модель) — Summon ожидает Slot
          return slotUI.getSlot();
        }
      }
    }

    // 2) Проверяем UnitUI внутри каждого SlotUI — для буффов/атаки по юниту
    for (Actor slotActor : slotsRow.getChildren()) {
      if (slotActor instanceof SlotUI) {
        SlotUI slotUI = (SlotUI) slotActor;
        UnitUI unitUI = slotUI.getUnitUI();
        if (unitUI != null) {
          Vector2 pos = unitUI.localToStageCoordinates(new Vector2(0, 0));
          if (stageX >= pos.x && stageX <= pos.x + unitUI.getWidth() && stageY >= pos.y
              && stageY <= pos.y + unitUI.getHeight()) {
            return unitUI.getEntity(); // возвращаем модель Unit
          }
        }
      }
    }

    // 3) Проверка попадания во врага
    Vector2 enemyPos = enemyUI.localToStageCoordinates(new Vector2(0, 0));
    if (stageX >= enemyPos.x && stageX <= enemyPos.x + enemyUI.getWidth() && stageY >= enemyPos.y
        && stageY <= enemyPos.y + enemyUI.getHeight()) {
      return context.getEnemy(); // <- возвращаем Enemy как цель
    }

    return null;
  }

  /**
   * Обновляет только данные (HP / ATK / имя) — не пересоздаёт UI-элементы, не
   * трогает порядок дочерних акторов.
   */
  public void refresh() {
    refreshRecursive(this);
  }

  public void addUnitToSlot(Unit unit, Slot slot) {
    for (Actor actor : slotsRow.getChildren()) {
      if (actor instanceof SlotUI slotUI && slotUI.getSlot() == slot) {
        slotUI.setUnit(unit); // используем твой существующий метод
        return;
      }
    }
  }

  private void refreshRecursive(Actor actor) {
    if (actor instanceof EntityUI<?> entityUI) {
      entityUI.refresh();
    }
    if (actor instanceof Table table) {
      for (Actor child : table.getChildren()) {
        refreshRecursive(child);
      }
    }
  }

  /**
   * Возвращает список UnitUI (в правильном порядке) для анимаций и ходов. Проход
   * по SlotUI и вытаскивание вложенных UnitUI.
   */
  public ArrayList<UnitUI> getPlayerUnitUIs() {
    ArrayList<UnitUI> result = new ArrayList<UnitUI>();
    for (Actor a : slotsRow.getChildren()) {
      if (a instanceof SlotUI) {
        SlotUI s = (SlotUI) a;
        UnitUI uui = s.getUnitUI();
        if (uui != null && uui.getEntity().isAlive()) {
          result.add(uui);
        }
      }
    }
    return result;
  }

  public PlayerUI getPlayerUI() {
    return playerUI;
  }

  public EntityUI<?> findEntityUI(Entity entity) {
    // 1) Проверяем юниты игрока
    for (Actor a : slotsRow.getChildren()) {
      if (a instanceof SlotUI s) {
        UnitUI uui = s.getUnitUI();
        if (uui != null && uui.getEntity() == entity) {
          return uui;
        }
      }
    }

    // 2) Проверяем врага
    if (enemyUI != null && enemyUI.getEntity() == entity) {
      return enemyUI;
    }

    return null; // ничего не найдено
  }
}
