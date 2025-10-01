package io.github.some_example_name.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.some_example_name.core.*;
import io.github.some_example_name.core.effects.SummonUnitEffect;
import io.github.some_example_name.model.*;
import io.github.some_example_name.model.status.StatusEffect;
import io.github.some_example_name.ui.effects.BuffEffectUI;
import io.github.some_example_name.ui.effects.DeBuffEffectUI;
import io.github.some_example_name.ui.elements.*;
import io.github.some_example_name.ui.panels.*;

import java.util.ArrayList;

public class BattleScreenUI extends ScreenAdapter {

  private final Stage stage;
  private final BoardUI boardUI;
  private final PlayerPanelUI playerPanelUI;
  private final StatusPanelUI statusPanelUI;
  private final GameContext context;
  private final GameEngine engine;

  private final float WORLD_WIDTH = 1280f;
  private final float WORLD_HEIGHT = 720f;

  public BattleScreenUI(GameContext context, GameEngine engine, Skin skin) {
    this.context = context;
    this.engine = engine;

    context.getPlayer().initBattle();

    stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
    Gdx.input.setInputProcessor(stage);

    boardUI = new BoardUI(context, engine, skin);
    playerPanelUI = new PlayerPanelUI(context, engine, skin, this);
    statusPanelUI = new StatusPanelUI(context.getPlayer(), skin);

    Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    root.add(boardUI).expandX().fillX().top().height(WORLD_HEIGHT * 0.7f).row();
    root.add(playerPanelUI).expandX().fillX().bottom().height(WORLD_HEIGHT * 0.25f).row();
    root.add(statusPanelUI).expandX().fillX().bottom().height(WORLD_HEIGHT * 0.05f).row();

    // -------------------- ПОДПИСКИ НА СОБЫТИЯ --------------------
    subscribeToEvents();

    // -------------------- END TURN --------------------
    statusPanelUI.getEndTurnButton().addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        handleEndTurn();
      }
    });

    context.getEventBus().on(BattleEventType.CARD_PLAYED, event -> {
      Card card = (Card) event.getPayload();

      // 1. Анимация — карта летит в отбой
      CardActor cardActor = playerPanelUI.findCardActor(card);
      if (cardActor != null) {
        cardActor.addAction(Actions.sequence(
            Actions.moveBy(0, -200, 0.3f),
            Actions.fadeOut(0.2f),
            Actions.run(cardActor::remove)));
      }

      // 2. Обновляем панель игрока (мана, отбой, рука)
      playerPanelUI.refresh();
      statusPanelUI.update();
    });
  }

  // ===================== ПОДПИСКИ НА СОБЫТИЯ =====================
  private void subscribeToEvents() {

    // Юнит призван
    context.getEventBus().on(BattleEventType.UNIT_SUMMONED, event -> {
      SummonUnitEffect.UnitSummonPayload payload = (SummonUnitEffect.UnitSummonPayload) event.getPayload();
      boardUI.addUnitToSlot(payload.unit, payload.slot);
    });

    // 🔹 Любая сущность получила урон
    context.getEventBus().on(BattleEventType.ENTITY_DAMAGED, event -> {
      Entity entity = (Entity) event.getPayload();
      EntityUI entityUI = boardUI.findEntityUI(entity);
      if (entityUI != null) {
        entityUI.playHit();
      }
      boardUI.refresh();
    });

    // 🔹 Статус эффект удалён
    context.getEventBus().on(BattleEventType.STATUS_REMOVED, event -> {
      StatusEffect effect = (StatusEffect) event.getPayload();
      // Можно добавить анимацию исчезновения эффекта
      boardUI.refresh();
    });

    // 🔹 Бафф
    context.getEventBus().on(BattleEventType.ENTITY_BUFFED, event -> {
      Entity entity = (Entity) event.getPayload();
      EntityUI entityUI = boardUI.findEntityUI(entity);
      if (entityUI != null) {
        BuffEffectUI effectUI = new BuffEffectUI(entityUI.getCenterX(), entityUI.getCenterY());
        entityUI.getParent().addActor(effectUI);
        effectUI.toFront();
      }
    });

    // 🔹 Дебафф
    context.getEventBus().on(BattleEventType.ENTITY_DEBUFFED, event -> {
      Entity entity = (Entity) event.getPayload();
      EntityUI entityUI = boardUI.findEntityUI(entity);
      if (entityUI != null) {
        DeBuffEffectUI effectUI = new DeBuffEffectUI(entityUI.getCenterX(), entityUI.getCenterY());
        entityUI.getParent().addActor(effectUI);
        effectUI.toFront();
      }
    });

  }

  // Внутри класса BattleScreenUI
  public BoardUI getBoardUI() {
    return boardUI;
  }

  // ===================== КОНЕЦ ХОДА =====================
  private void handleEndTurn() {
    statusPanelUI.getEndTurnButton().setDisabled(true);

    // Логика сама обрабатывает статус-эффекты, действия юнитов и врага
    engine.endPlayerTurn(() -> {
      // Callback вызывается после завершения всех действий
      if (engine.isBattleOver()) {
        showBattleResult(engine.getWinner());
      } else {
        engine.drawCards(context.getPlayer().getStartingHandSize());
        refreshBattleScreen();
        statusPanelUI.getEndTurnButton().setDisabled(false);
      }
    });
  }

  // ===================== CARD DROP =====================
  public void onCardDropped(CardActor cardActor, float stageX, float stageY) {
    cardActor.setHighlighted(false);
    Card card = cardActor.getCard();
    Targetable target = boardUI.findTargetAt(stageX, stageY);

    if (target != null) {
      boolean success = engine.playCardOnTarget(card, target);
      if (!success)
        cardActor.resetPosition();
    } else {
      cardActor.resetPosition();
    }
  }

  // ===================== UI ОБНОВЛЕНИЕ =====================
  public void refreshBattleScreen() {
    boardUI.refresh();
    playerPanelUI.refresh();
    statusPanelUI.update();
  }

  private void showBattleResult(String winner) {
    System.out.println("Бой окончен! Победитель: " + winner);
  }

  // ===================== RENDER =====================
  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    boardUI.act(delta);
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
}
