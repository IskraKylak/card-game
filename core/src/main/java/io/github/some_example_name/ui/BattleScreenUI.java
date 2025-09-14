package io.github.some_example_name.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.core.GameEngine;
import io.github.some_example_name.model.Card;
import io.github.some_example_name.model.Slot;
import io.github.some_example_name.ui.elements.CardActor;
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

  public BattleScreenUI(GameContext context, GameEngine engine, Skin skin) {
    this.context = context;
    this.engine = engine;

    context.getPlayer().initBattle();

    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);

    boardUI = new BoardUI(context, engine, skin);
    playerPanelUI = new PlayerPanelUI(context, engine, skin, this);
    statusPanelUI = new StatusPanelUI(context.getPlayer(), skin);

    Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    root.add(boardUI).expandX().fillX().top().height(Gdx.graphics.getHeight() * 0.7f).row();
    root.add(playerPanelUI).expandX().fillX().bottom().height(Gdx.graphics.getHeight() * 0.25f).row();
    root.add(statusPanelUI).expandX().fillX().bottom().height(Gdx.graphics.getHeight() * 0.05f).row();

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

    // 1️⃣ Анимация ходов юнитов
    animatePlayerUnitsTurn(() -> {
      // 2️⃣ Ход врага
      animateEnemyTurn(() -> {
        if (engine.isBattleOver()) {
          showBattleResult(engine.getWinner());
        } else {
          statusPanelUI.getEndTurnButton().setDisabled(false);
        }
      });
    });
  }

  private void animatePlayerUnitsTurn(Runnable onComplete) {
    ArrayList<UnitUI> units = boardUI.getPlayerUnitUIs();
    animateUnitList(units, 0, onComplete);
  }

  private void animateUnitList(ArrayList<UnitUI> units, int index, Runnable onComplete) {
    if (index >= units.size()) {
      if (onComplete != null)
        onComplete.run();
      return;
    }

    UnitUI unitUI = units.get(index);
    float startX = unitUI.getX();
    float startY = unitUI.getY();
    float targetX = boardUI.getEnemyUI().getX();
    float targetY = boardUI.getEnemyUI().getY();

    unitUI.addAction(Actions.sequence(
        Actions.moveTo(targetX, targetY, 0.3f),
        Actions.run(() -> {
          engine.unitAttack(unitUI.getUnit(), context.getEnemy());
          boardUI.refresh();

          unitUI.addAction(Actions.sequence(
              Actions.moveTo(startX, startY, 0.3f),
              Actions.run(() -> animateUnitList(units, index + 1, onComplete))));
        })));
  }

  private void animateEnemyTurn(Runnable onComplete) {
    engine.processEnemyTurn();
    boardUI.refresh();
    if (onComplete != null)
      onComplete.run();
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  public void refreshBattleScreen() {
    boardUI.refresh();
    playerPanelUI.refresh();
    statusPanelUI.update();
  }

  public void onCardDropped(CardActor cardActor, float stageX, float stageY) {
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
