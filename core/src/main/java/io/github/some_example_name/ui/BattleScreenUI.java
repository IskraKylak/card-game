package io.github.some_example_name.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.some_example_name.core.*;
import io.github.some_example_name.core.effects.SummonUnitEffect;
import io.github.some_example_name.model.*;
import io.github.some_example_name.model.data.DataEnemy;
import io.github.some_example_name.model.data.DataPlayers;
import io.github.some_example_name.model.payload.StatusEffectPayload;
import io.github.some_example_name.model.payload.UnitAttackPayload;
import io.github.some_example_name.model.payload.UnitSpellPayload;
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
    // Юнит кастует заклинание
    context.getEventBus().on(BattleEventType.UNIT_CAST_SPELL, evt -> {
      UnitSpellPayload payload = (UnitSpellPayload) evt.getPayload();

      Entity caster = payload.getCaster();
      Runnable onComplete = payload.getOnComplete();

      EntityUI<?> casterUI = boardUI.findEntityUI(caster);

      if (casterUI == null) {
        System.out.println("Ошибка: юнит не найден на доске!");
        onComplete.run();
        return;
      }

      System.out.println("Юнит кастует заклинание: " + casterUI.getEntity().getName());
      casterUI.addAction(Actions.sequence(
          Actions.run(() -> casterUI.playMagic()),
          Actions.delay(3.0f), // ⏳ подожди три секунды (или длительность твоей маг-анимации)
          Actions.run(() -> casterUI.playIdle()),
          Actions.run(() -> boardUI.refresh()),
          Actions.run(onComplete)));
    });

    // Юнит или враг умер
    context.getEventBus().on(BattleEventType.UNIT_DIED, event -> {
      Object payload = event.getPayload();

      if (payload instanceof Unit unit) {
        EntityUI unitUI = boardUI.findEntityUI(unit);
        if (unitUI != null) {
          unitUI.playDead();
          unitUI.addAction(Actions.sequence(
              Actions.delay(1.0f),
              Actions.run(unitUI::remove)));
        }
      } else if (payload instanceof Enemy enemy) {
        EntityUI enemyUI = boardUI.findEntityUI(enemy);
        if (enemyUI != null) {
          enemyUI.playDead();
          enemyUI.addAction(Actions.sequence(
              Actions.delay(1.0f),
              Actions.run(enemyUI::remove)));
        }

        Skin newSkin = new Skin(Gdx.files.internal("uiskin.json"));
        TextButton restartButton = new TextButton("Restart Game", newSkin);
        restartButton.setPosition(
            stage.getWidth() / 2f - restartButton.getWidth() / 2f,
            stage.getHeight() / 2f - restartButton.getHeight() / 2f);

        restartButton.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            restartButton.remove();

            Player newPlayer = DataPlayers.getPlayerByFaction(Faction.LIFE);
            newPlayer.buildDefaultDeckFromFaction();
            newPlayer.buildBattleDeck();
            newPlayer.initBattle();

            Enemy newEnemy = DataEnemy.getEnemyById(2);

            GameContext newContext = new GameContext(newPlayer, newEnemy);
            GameEngine newEngine = new GameEngine(newContext);

            Skin newSkin = new Skin(Gdx.files.internal("uiskin.json"));
            BattleScreenUI newScreen = new BattleScreenUI(newContext, newEngine, newSkin);
            ((Game) Gdx.app.getApplicationListener()).setScreen(newScreen);
          }
        });

        stage.addActor(restartButton);
      }
    });

    // 🔹 Юнит или враг атакует
    context.getEventBus().on(BattleEventType.UNIT_ATTACK, event -> {
      UnitAttackPayload payload = (UnitAttackPayload) event.getPayload();
      CombatEntity attacker = payload.getAttacker();
      Targetable target = payload.getTarget();

      EntityUI attackerUI = boardUI.findEntityUI(attacker);
      if (attackerUI == null) {
        payload.getOnComplete().run();
        return;
      }

      Actor parent = attackerUI.getParent();
      float startX = attackerUI.getX();
      float startY = attackerUI.getY();

      Vector2 targetLocal;
      Runnable onHit;

      // 🎯 Если атакуем игрока
      if (target instanceof Player) {
        PlayerUI playerUI = boardUI.getPlayerUI();
        Vector2 hitPoint = new Vector2(playerUI.getWidth() / 2f, playerUI.getHeight() * 0.2f); // в ноги
        targetLocal = parent.stageToLocalCoordinates(playerUI.localToStageCoordinates(hitPoint));
        onHit = () -> {
          playerUI.playHit();
          context.getEventBus().emit(BattleEvent.of(
              BattleEventType.UNIT_ATTACK_LOGIC,
              new UnitAttackPayload(attacker, target, payload.getOnComplete())));
        };
      }

      // 🎯 Если атакуем врага / юнита
      else if (target instanceof Entity entity) {
        EntityUI targetUI = boardUI.findEntityUI(entity);
        if (targetUI == null) {
          payload.getOnComplete().run();
          return;
        }

        Vector2 hitPoint = new Vector2(targetUI.getWidth() / 2f, targetUI.getHeight() * 0.2f); // в ноги
        targetLocal = parent.stageToLocalCoordinates(targetUI.localToStageCoordinates(hitPoint));

        onHit = () -> {
          targetUI.playHit();
          attacker.performAttack(target);
          context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED, target));
        };
      }

      else {
        payload.getOnComplete().run();
        return;
      }

      // Центрируем
      targetLocal.x -= attackerUI.getWidth() / 2f;
      targetLocal.y -= attackerUI.getHeight() * 0.25f;

      // 💾 Сохраняем старый Z-индекс
      int originalZ = attackerUI.getZIndex();

      // ⬆️ Поднимаем атакующего на передний план
      attackerUI.setZIndex(attackerUI.getParent().getChildren().size - 1);

      // 🌀 Последовательность действий
      attackerUI.addAction(Actions.sequence(
          Actions.moveTo(targetLocal.x, targetLocal.y, 0.5f), // подлетает
          Actions.run(attackerUI::playAttack), // анимация атаки
          Actions.delay(1.0f), // ждём завершения анимации удара
          Actions.run(onHit), // урон и событие
          Actions.moveTo(startX, startY, 0.25f), // возвращается
          Actions.run(attackerUI::playIdle), // ставим в idle
          Actions.run(() -> attackerUI.setZIndex(originalZ)), // 👈 возвращаем в исходный слой
          Actions.run(payload.getOnComplete()) // продолжаем ход
      ));
    });

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
    context.getEventBus().on(BattleEventType.STATUS_EFFECT_APPLIED, event -> {
      boardUI.refresh();
    });

    // 🔹 Статус эффект удалён
    context.getEventBus().on(BattleEventType.STATUS_REMOVED, event -> {
      // StatusEffect effect = (StatusEffect) event.getPayload();
      // Можно добавить анимацию исчезновения эффекта
      boardUI.refresh();
    });

    // 🔹 Статус эффект применён (бафф или дебафф)
    context.getEventBus().on(BattleEventType.STATUS_EFFECT_TRIGGERED, evt -> {
      StatusEffectPayload payload = (StatusEffectPayload) evt.getPayload();
      Entity target = payload.getTarget();
      StatusEffect effect = payload.getEffect();
      Runnable onComplete = payload.getOnComplete();

      EntityUI<?> targetUI = boardUI.findEntityUI(target);
      if (targetUI == null) {
        onComplete.run();
        return;
      }

      boolean isDebuff = effect.isNegative();

      // Эффект будет добавляться внутрь самой модельки
      Actor effectActor;
      float centerX = targetUI.getWidth() / 2f; // теперь локальные координаты внутри Table
      float centerY = targetUI.getHeight() * 0.35f;

      if (isDebuff) {
        effectActor = new DeBuffEffectUI(centerX, centerY, onComplete);
      } else {
        effectActor = new BuffEffectUI(centerX, centerY, onComplete);
      }

      // Добавляем внутрь самой UI-модельки, а не на сцену
      targetUI.addActor(effectActor);
    });

    // 🔹 Бафф
    context.getEventBus().on(BattleEventType.ENTITY_BUFFED, event -> {
      // Entity entity = (Entity) event.getPayload();
      // EntityUI entityUI = boardUI.findEntityUI(entity);
      // if (entityUI != null) {
      // BuffEffectUI effectUI = new BuffEffectUI(entityUI.getCenterX(),
      // entityUI.getCenterY());
      // entityUI.getParent().addActor(effectUI);
      // effectUI.toFront();
      // }
      boardUI.refresh();
    });

    // 🔹 Дебафф
    context.getEventBus().on(BattleEventType.ENTITY_DEBUFFED, event -> {
      // Entity entity = (Entity) event.getPayload();
      // EntityUI entityUI = boardUI.findEntityUI(entity);
      // if (entityUI != null) {
      // DeBuffEffectUI effectUI = new DeBuffEffectUI(entityUI.getCenterX(),
      // entityUI.getCenterY());
      // entityUI.getParent().addActor(effectUI);
      // effectUI.toFront();
      // }
      boardUI.refresh();
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
        engine.startPlayerTurn();
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
    // boardUI.refresh();
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
