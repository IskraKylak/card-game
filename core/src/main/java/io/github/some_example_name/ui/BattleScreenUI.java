package io.github.some_example_name.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.core.EnemyAction;
import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.core.GameEngine;
import io.github.some_example_name.model.Card;
import io.github.some_example_name.ui.elements.CardActor;
import io.github.some_example_name.ui.elements.EnemyUI;
import io.github.some_example_name.ui.elements.UnitUI;
import io.github.some_example_name.ui.panels.BoardUI;
import io.github.some_example_name.ui.panels.PlayerPanelUI;
import io.github.some_example_name.ui.panels.StatusPanelUI;

public class BattleScreenUI extends ScreenAdapter {

  private Stage stage;
  private BoardUI boardUI;
  private PlayerPanelUI playerPanelUI;
  private StatusPanelUI statusPanelUI;
  private final GameContext context;
  private final GameEngine engine;
  EnemyAction action;

  private final float WORLD_WIDTH = 1280f;
  private final float WORLD_HEIGHT = 720f;

  public BattleScreenUI(GameContext context, GameEngine engine, Skin skin) {
    this.context = context;
    this.engine = engine;

    context.getPlayer().initBattle();

    // Используем FitViewport вместо ScreenViewport
    stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
    Gdx.input.setInputProcessor(stage);

    boardUI = new BoardUI(context, engine, skin);
    playerPanelUI = new PlayerPanelUI(context, engine, skin, this);
    statusPanelUI = new StatusPanelUI(context.getPlayer(), skin);

    Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    // Привязываем размеры к WORLD_HEIGHT
    root.add(boardUI).expandX().fillX().top().height(WORLD_HEIGHT * 0.7f).row();
    root.add(playerPanelUI).expandX().fillX().bottom().height(WORLD_HEIGHT * 0.25f).row();
    root.add(statusPanelUI).expandX().fillX().bottom().height(WORLD_HEIGHT * 0.05f).row();

    action = engine.planEnemyAction();

    // Кнопка End Turn
    statusPanelUI.getEndTurnButton().addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        handleEndTurn();
      }
    });
  }

  private void handleEndTurn() {
    statusPanelUI.getEndTurnButton().setDisabled(true);

    // 1️⃣ Ход игрока
    animatePlayerUnitsTurn(() -> {
      // 2️⃣ Ход врага
      animateEnemyTurn(() -> {
        if (engine.isBattleOver()) {
          showBattleResult(engine.getWinner());
        } else {
          engine.drawCards(context.getPlayer().getStartingHandSize());
          refreshBattleScreen();
          statusPanelUI.getEndTurnButton().setDisabled(false);
          action = engine.planEnemyAction();
        }
      });
    });
  }

  // ===================== АНИМАЦИИ =====================

  /**
   * Проходит по всем юнитам игрока
   */
  private void animatePlayerUnitsTurn(Runnable onComplete) {
    ArrayList<UnitUI> units = boardUI.getPlayerUnitUIs();

    if (units.isEmpty()) {
      if (onComplete != null) {
        onComplete.run();
      }
      return;
    }

    animateUnitRecursive(units, 0, onComplete);
  }

  /**
   * Рекурсивно перебираем юнитов
   */
  private void animateUnitRecursive(ArrayList<UnitUI> units, int index, Runnable onComplete) {

    if (index >= units.size()) {
      if (onComplete != null) {
        onComplete.run();
      }
      return;
    }

    UnitUI unitUI = units.get(index);

    if (!unitUI.getUnit().isAlive()) {
      animateUnitRecursive(units, index + 1, onComplete);
      return;
    }

    animateSingleUnitTurn(unitUI, () -> {
      animateUnitRecursive(units, index + 1, onComplete);
    });
  }

  /**
   * Анимация действий одного юнита
   */
  private void animateSingleUnitTurn(UnitUI unitUI, Runnable onComplete) {
    float startX = unitUI.getX();
    float startY = unitUI.getY();
    EnemyUI enemyUI = boardUI.getEnemyUI();
    Actor parent = unitUI.getParent(); // parent, в чьей системе moveTo работает

    // Центр врага в локальных координатах enemyUI
    Vector2 enemyCenterLocal = new Vector2(enemyUI.getWidth() * 0.5f, enemyUI.getHeight() * 0.5f);

    // Переводим в Stage-координаты
    Vector2 enemyCenterStage = enemyUI.localToStageCoordinates(enemyCenterLocal);

    // Переводим в локальные координаты parent
    Vector2 targetInParent = parent.stageToLocalCoordinates(enemyCenterStage);

    // Сдвигаем на половину юнита
    targetInParent.x -= unitUI.getWidth() * 0.5f;
    targetInParent.y -= unitUI.getHeight() * 0.5f;

    Runnable attackLogic = () -> {
      engine.unitAttack(unitUI.getUnit(), context.getEnemy());
    };

    ArrayList<com.badlogic.gdx.scenes.scene2d.Action> sequence = new ArrayList<>();

    sequence.add(Actions.run(() -> {
      unitUI.playAttack();
      if (action.getType() == EnemyAction.Type.ATTACK) {
        engine.counterAttack(unitUI.getUnit(), context.getEnemy());
        enemyUI.playAttack();
      }
    }));

    sequence.add(Actions.moveTo(targetInParent.x, targetInParent.y, 0.48f));

    sequence.add(Actions.run(() -> {

      attackLogic.run();

      enemyUI.refresh();
    }));

    sequence.add(Actions.moveTo(startX, startY, 0.48f));

    sequence.add(Actions.run(() -> {
      if (!unitUI.getUnit().isAlive())
        unitUI.playDead();
      else
        unitUI.playIdle();
    }));

    // Финал
    sequence.add(Actions.run(() -> {
      onComplete.run();
    }));

    unitUI.addAction(Actions.sequence(sequence.toArray(new com.badlogic.gdx.scenes.scene2d.Action[0])));
  }

  /**
   * Анимация хода врага
   */
  private void animateEnemyTurn(Runnable onComplete) {
    EnemyUI enemyUI = boardUI.getEnemyUI();

    if (action.getTargetUnit() != null && !action.getTargetUnit().isAlive()) {
      action = engine.planEnemyAction(); // перепланируем, если цель умерла
    }

    if (action.getType() == EnemyAction.Type.ATTACK) {
      Actor targetActor = null;
      if (action.getTargetUnit() != null) {
        targetActor = boardUI.findUnitUI(action.getTargetUnit());
      } else {
        targetActor = boardUI.getPlayerActor();
      }

      if (targetActor == null) {
        // если цели нет в UI — просто выполнить мгновенно
        engine.executeEnemyAction(action);
        if (onComplete != null)
          onComplete.run();
        return;
      }

      // координаты цели в локальных coords родителя enemyUI
      Vector2 targetInParent = centerOfActorInParent(targetActor, enemyUI.getParent());
      float targetX = targetInParent.x - enemyUI.getWidth() / 2f; // центрируем по середине изображения
      float targetY = targetInParent.y - enemyUI.getHeight() / 2f;

      float startX = enemyUI.getX();
      float startY = enemyUI.getY();

      // логика — выполняем в момент контакта
      Runnable applyLogic = () -> {
        engine.executeEnemyAction(action);
      };
      enemyUI.playAttack();
      SequenceAction seq = Actions.sequence(
          Actions.moveTo(targetX, targetY, 0.98f),
          Actions.run(() -> {
            applyLogic.run();
          }),
          Actions.moveTo(startX, startY, 0.98f),
          Actions.run(new Runnable() {
            @Override
            public void run() {
              enemyUI.playIdle();
              if (onComplete != null)
                onComplete.run();
            }
          }));

      enemyUI.addAction(seq);
      return;
    }

    if (action.getType() == EnemyAction.Type.BUFF) {
      // простая визуализация баффа: вспышка + apply
      // enemyUI.playAttack(); // или playBuff если добавишь
      enemyUI.addAction(Actions.sequence(
          Actions.delay(0.4f),
          Actions.run(() -> {
            engine.executeEnemyAction(action);
          }),
          Actions.run(() -> {
            enemyUI.playIdle();
            if (onComplete != null)
              onComplete.run();
          })));
      return;
    }

    // NONE или неизвестно
    engine.executeEnemyAction(action);
    boardUI.refresh();
    if (onComplete != null)
      onComplete.run();
  }

  // Центр actor'а в координатах parent'а
  private Vector2 centerOfActorInParent(Actor actor, Group parent) {
    Vector2 center = new Vector2(
        actor.getWidth() / 2f,
        actor.getHeight() / 2f);
    return actor.localToActorCoordinates(parent, center);
  }

  public BoardUI getBoardUI() {
    return boardUI;
  }

  // ===================== RENDER =====================

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // Обновляем stage
    stage.act(delta);
    boardUI.act(delta); // если BoardUI нужен для фона
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  // ===================== ВСПОМОГАТЕЛЬНЫЕ =====================

  public void refreshBattleScreen() {
    boardUI.refresh();
    playerPanelUI.refresh();
    statusPanelUI.update();
  }

  public void onCardDropped(CardActor cardActor, float stageX, float stageY) {
    cardActor.setHighlighted(false);

    Card card = cardActor.getCard();
    Object target = boardUI.findTargetAt(stageX, stageY);

    if (target != null) {
      boolean success = engine.playCardOnTarget(card, target);
      if (success) {
        refreshBattleScreen();
      } else {
        cardActor.resetPosition();
      }
    } else {
      cardActor.resetPosition();
    }
  }

  private void showBattleResult(String winner) {
    System.out.println("Бой окончен! Победитель: " + winner);
  }
}
