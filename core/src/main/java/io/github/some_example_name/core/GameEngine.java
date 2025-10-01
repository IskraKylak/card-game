package io.github.some_example_name.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.some_example_name.core.effects.SummonUnitEffect;
import io.github.some_example_name.model.*;
import io.github.some_example_name.model.status.StatusEffect;

public class GameEngine {

  private final GameContext context;
  private final Random random;

  public GameEngine(GameContext context) {
    this.context = context;
    this.random = new Random();
    startBattle();
  }

  // -------------------- ИНИЦИАЛИЗАЦИЯ БОЯ --------------------
  private void startBattle() {
    Player player = context.getPlayer();
    player.initBattle();
    player.restoreMana(player.getMaxMana());

    for (Slot slot : player.getSlots()) {
      slot.removeUnit();
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

  public void executeEnemyAction(EnemyAction action) {
    Enemy enemy = context.getEnemy();
    Player player = context.getPlayer();

    switch (action.getType()) {
      case ATTACK:
        if (action.getTargetUnit() != null) {
          Unit target = action.getTargetUnit();
          target.takeDamage(action.getAmount());
          context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED, target));
        } else {
          player.takeDamage(action.getAmount());
          context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED, player));
        }
        break;

      case BUFF:
        enemy.heal(action.getAmount());
        context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_BUFFED, enemy));
        break;

      case NONE:
      default:
        break;
    }

    removeDeadUnits();
  }

  // -------------------- END TURN --------------------
  public void endPlayerTurn(Runnable callback) {

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

    // 1️⃣ Планирование юнитов
    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit != null) {
      }
    }

  }

  public void executeTurn() {
    Player player = context.getPlayer();

    // 1️⃣ Юниты выполняют свои действия
    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit != null) {

      }
    }

    // 3️⃣ Начало нового хода игрока: сброс карт, новые карты, манна
    player.getDiscard().addAll(player.getHand());
    player.getHand().clear();
    drawCards(player.getStartingHandSize());
    player.restoreMana(player.getMaxMana());
  }
}
