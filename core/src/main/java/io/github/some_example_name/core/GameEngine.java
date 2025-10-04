package io.github.some_example_name.core;

import java.util.ArrayList;
import java.util.Random;

import io.github.some_example_name.model.*;
import io.github.some_example_name.model.payload.UnitAttackPayload;
import io.github.some_example_name.model.status.StatusEffect;

public class GameEngine {

  private final GameContext context;
  private final Random random;

  public GameEngine(GameContext context) {
    this.context = context;
    this.random = new Random();
    startBattle();

    context.getEventBus().on(BattleEventType.UNIT_ATTACK_LOGIC, evt -> {
      UnitAttackPayload p = (UnitAttackPayload) evt.getPayload();
      p.getAttacker().performAttack(p.getTarget());
      context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED, p.getTarget()));
      p.getOnComplete().run();
    });
  }

  // -------------------- ИНИЦИАЛИЗАЦИЯ БОЯ --------------------
  private void startBattle() {
    Player player = context.getPlayer();
    player.initBattle();
    player.restoreMana(player.getMaxMana());

    // Очистка слотов игрока
    for (Slot slot : player.getSlots()) {
      slot.removeUnit();
    }

    // Планирование хода врага на первый раунд
    Enemy enemy = context.getEnemy();
    enemy.planActions(player, enemy);

    // Если в слотах уже есть юниты (на случай предзагруженного состояния),
    // планируем их действия
    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit != null) {
        unit.planActions(player, enemy);
      }
    }
  }

  // -------------------- КАРТЫ --------------------
  public boolean playCardOnTarget(Card card, Targetable target) {
    Player player = context.getPlayer();

    if (player.getMana() < card.getCost()) {
      System.out.println("Недостаточно маны для розыгрыша карты: " + card.getName());
      return false;
    }

    boolean validTarget;
    switch (card.getType()) {
      case UNIT:
        validTarget = target instanceof Slot;
        break;
      case ATTACK:
        validTarget = target instanceof Enemy || target instanceof Player;
        break;
      case BUFF:
        validTarget = target instanceof Unit;
        break;
      case DEBUFF:
        validTarget = target instanceof Enemy;
        break;
      default:
        validTarget = false;
    }

    if (!validTarget) {
      System.out.println("Карта " + card.getName() + " не может быть применена на эту цель.");
      return false;
    }

    boolean applied = card.getEffect().apply(context, target);

    if (applied) {
      player.setMana(player.getMana() - card.getCost());
      player.playCard(card);
      context.getEventBus().emit(BattleEvent.of(BattleEventType.CARD_PLAYED, card));
    }

    return applied;
  }

  // -------------------- УДАЛЕНИЕ МЕРТВЫХ ЮНИТОВ --------------------
  private void removeDeadUnits() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    for (Slot slot : player.getSlots()) {
      Unit u = slot.getUnit();
      if (u != null && !u.isAlive()) {
        slot.removeUnit();
      }
    }

    // Для врага можно добавить аналогично, если будет список юнитов
  }

  // -------------------- ПОИСК СЛОТА ПО ЮНИТУ --------------------
  private Slot findSlotForUnit(Unit unit, Player player) {
    if (unit == null || player == null || player.getSlots() == null)
      return null;

    for (Slot slot : player.getSlots()) {
      if (unit.equals(slot.getUnit())) {
        return slot;
      }
    }

    return null;
  }

  // -------------------- END TURN --------------------
  public void endPlayerTurn(Runnable onTurnEnd) {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    TurnProcessor turnProcessor = new TurnProcessor();
    turnProcessor.setOnTurnEnd(onTurnEnd);

    // 1️⃣ Ход юнитов игрока
    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit == null || !unit.isAlive())
        continue;

      // Эффекты юнита
      for (StatusEffect effect : new ArrayList<>(unit.getStatusEffects())) {
        turnProcessor.addAction(() -> {
          effect.onTurnStart(unit);
          if (!effect.tick(unit)) {
            effect.onRemove(unit);
            unit.removeStatusEffect(effect);
          }
          turnProcessor.runNext();
        });
      }

      // Действия юнита
      for (ActionPlan.Action action : new ArrayList<>(unit.getActionPlan().getActions())) {
        turnProcessor.addAction(() -> {
          Targetable target = action.getTarget();

          // Перепланируем если цель уже мертва
          if (target instanceof Unit targetUnit && !targetUnit.isAlive()) {
            unit.planActions(player, enemy);
            target = unit.getActionPlan().getActions().get(0).getTarget();
          } else if (target instanceof Enemy targetEnemy && targetEnemy.getHealth() <= 0) {
            unit.planActions(player, enemy);
            target = unit.getActionPlan().getActions().get(0).getTarget();
          }

          switch (action.getType()) {
            case ATTACK -> {
              Unit finalUnit = unit;
              Targetable finalTarget = target;
              context.getEventBus().emit(BattleEvent.of(
                  BattleEventType.UNIT_ATTACK,
                  new UnitAttackPayload(finalUnit, finalTarget, () -> {
                    // finalUnit.performAttack(finalTarget);

                    // 🔹 Эмитим урон
                    // context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED,
                    // finalTarget));

                    // 🔹 Проверяем смерть
                    if (finalTarget instanceof Unit deadUnit && !deadUnit.isAlive()) {
                      context.getEventBus().emit(BattleEvent.of(BattleEventType.UNIT_DIED, deadUnit));

                      Slot deadUnitSlot = findSlotForUnit(deadUnit, player); // функция ищет слот по юниту
                      if (deadUnitSlot != null) {
                        deadUnitSlot.removeUnit();
                      }
                    } else if (finalTarget instanceof Enemy deadEnemy && deadEnemy.getHealth() <= 0) {
                      context.getEventBus().emit(BattleEvent.of(BattleEventType.UNIT_DIED, deadEnemy));
                    } else if (finalTarget instanceof Player playerTarget && playerTarget.getHealth() <= 0) {
                      context.getEventBus().emit(BattleEvent.of(BattleEventType.PLAYER_DIED, playerTarget));
                    }

                    turnProcessor.runNext();
                  })));
            }
            case CAST_SPELL -> {
              StatusEffect effect = action.getEffect();
              if (effect != null && target instanceof Entity entityTarget) {
                effect.onApply(entityTarget);
                entityTarget.addStatusEffect(effect);
              }
              turnProcessor.runNext();
            }
          }
        });
      }

      unit.getActionPlan().clear();
    }

    // 2️⃣ Ход врага
    if (enemy != null && enemy.getHealth() > 0) {
      // Эффекты врага
      for (StatusEffect effect : new ArrayList<>(enemy.getStatusEffects())) {
        turnProcessor.addAction(() -> {
          effect.onTurnStart(enemy);
          if (!effect.tick(enemy)) {
            effect.onRemove(enemy);
            enemy.removeStatusEffect(effect);
          }
          turnProcessor.runNext();
        });
      }

      // Планирование врага
      if (enemy.getActionPlan().getActions().isEmpty()) {
        enemy.planActions(player, enemy);
      }

      for (ActionPlan.Action action : new ArrayList<>(enemy.getActionPlan().getActions())) {
        turnProcessor.addAction(() -> {
          Targetable target = action.getTarget();

          if (target instanceof Unit targetUnit && !targetUnit.isAlive()) {
            enemy.planActions(player, enemy);
            target = enemy.getActionPlan().getActions().get(0).getTarget();
          }

          switch (action.getType()) {
            case ATTACK -> {
              Enemy finalEnemy = enemy;
              Targetable finalTarget = target;
              context.getEventBus().emit(BattleEvent.of(
                  BattleEventType.UNIT_ATTACK,
                  new UnitAttackPayload(finalEnemy, finalTarget, () -> {
                    // finalEnemy.performAttack(finalTarget);

                    context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED, finalTarget));

                    if (finalTarget instanceof Unit deadUnit && !deadUnit.isAlive()) {
                      context.getEventBus().emit(BattleEvent.of(BattleEventType.UNIT_DIED, deadUnit));

                      Slot deadUnitSlot = findSlotForUnit(deadUnit, player); // функция ищет слот по юниту
                      if (deadUnitSlot != null) {
                        deadUnitSlot.removeUnit();
                      }

                    } else if (finalTarget instanceof Enemy deadEnemy && deadEnemy.getHealth() <= 0) {
                      context.getEventBus().emit(BattleEvent.of(BattleEventType.UNIT_DIED, deadEnemy));
                    } else if (finalTarget instanceof Player playerTarget && playerTarget.getHealth() <= 0) {
                      context.getEventBus().emit(BattleEvent.of(BattleEventType.PLAYER_DIED, playerTarget));
                    }

                    turnProcessor.runNext();
                  })));
            }
            case CAST_SPELL -> {
              StatusEffect effect = action.getEffect();
              if (effect != null && target instanceof Entity entityTarget) {
                effect.onApply(entityTarget);
                entityTarget.addStatusEffect(effect);
              }
              turnProcessor.runNext();
            }
          }
        });
      }

      enemy.getActionPlan().clear();
    }

    // 🚀 Старт очереди
    turnProcessor.runNext();
  }

  // -------------------- КОЛОДА --------------------
  public void drawCards(int count) {
    Player player = context.getPlayer();

    int cardsNeeded = count;
    while (cardsNeeded > 0 && player.getHand().size() < player.getMaxHand()) {
      if (player.getBattleDeck().isEmpty() && !player.getDiscard().isEmpty()) {
        player.getBattleDeck().addAll(player.getDiscard());
        player.getDiscard().clear();
      }
      if (player.getBattleDeck().isEmpty())
        break;

      player.getHand().add(player.getBattleDeck().remove(0));
      cardsNeeded--;
    }

    player.restoreMana(player.getMaxMana());
  }

  // -------------------- ПРОВЕРКА КОНЦА БОЯ --------------------
  public boolean isBattleOver() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();
    return player.getHealth() <= 0 || enemy.getHealth() <= 0;
  }

  public String getWinner() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();
    if (player.getHealth() <= 0)
      return "Враг";
    if (enemy.getHealth() <= 0)
      return "Игрок";
    return "Ничья";
  }

  public void startPlayerTurn() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    // 1️⃣ Обновление карт, маны и эффектов
    player.restoreMana(player.getMaxMana());
    drawCards(player.getStartingHandSize());

    // 2️⃣ Планируем действия юнитов игрока
    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit != null) {
        System.out.println("В слоте есть юнит: " + unit.getName());
        unit.planActions(player, enemy);
      } else {
        System.out.println("В слоте нет юнита.");
      }
    }

    // 3️⃣ Планируем действия врага
    enemy.planActions(player, enemy);
  }

  public boolean summonUnit(Unit unit, Slot targetSlot) {
    if (targetSlot.getUnit() != null) {
      System.out.println("Слот уже занят, нельзя призвать юнита!");
      return false;
    }

    targetSlot.setUnit(unit);
    System.out.println("Призван юнит " + unit.getName() + " в слот " + targetSlot);

    // Планируем действия новопоявившегося юнита
    unit.planActions(context.getPlayer(), context.getEnemy());

    // Отправляем событие для UI
    context.getEventBus().emit(BattleEvent.of(BattleEventType.UNIT_SUMMONED, unit));
    return true;
  }
}
