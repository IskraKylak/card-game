package io.github.some_example_name.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

import io.github.some_example_name.model.*;

public class GameEngine {

  private GameContext context;
  private Random random;

  public GameEngine(GameContext context) {
    this.context = context;
    this.random = new Random();
    startBattle();
  }

  // --- Инициализация боя ---
  private void startBattle() {
    context.getPlayer().initBattle();
    context.getPlayer().restoreMana(context.getPlayer().getMaxMana());

    // Очищаем слоты на доске
    for (Slot slot : context.getPlayer().getSlots()) {
      slot.removeUnit();
    }
  }

  private void removeDeadUnits() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    // Удаляем мёртвых юнитов с игрока
    for (Slot slot : player.getSlots()) {
      Unit u = slot.getUnit();
      if (u != null && !u.isAlive()) {
        System.out.println(u.getName() + " погибает и покидает слот " + slot.getId());
        slot.removeUnit();
      }
    }

    // Мёртвые юниты врага удаляем, если у него будет список
    // Пока этого не делаем
  }

  // --- Разыграть карту игрока на цель ---
  // --- Разыграть карту игрока на цель ---
  public boolean playCardOnTarget(Card card, Targetable target) {
    Player player = context.getPlayer();

    // Проверка маны
    if (player.getMana() < card.getCost()) {
      System.out.println("Недостаточно маны для розыгрыша карты: " + card.getName());
      return false;
    }

    // Проверяем, можно ли карту применить на эту цель
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

    // Применяем эффект карты
    boolean applied = card.getEffect().apply(context, target);

    if (applied) {
      player.setMana(player.getMana() - card.getCost());
      player.playCard(card);
      System.out.println("Карта " + card.getName() + " успешно разыграна.");
    } else {
      System.out.println("Не удалось применить карту " + card.getName() + " на цель.");
    }

    return applied;
  }

  // --- Игрок разыгрывает карты ---
  // public void playPlayerHand() {
  // Player player = context.getPlayer();
  // drawCards(4);
  // player.restoreMana(player.getMaxMana());

  // System.out.println("Начало хода игрока: Мана=" + player.getMana() + ", Карты
  // в руке=" + player.getHand().size()
  // + ", Карты в отбое=" + player.getDiscard().size() + ", Карты в колоде=" +
  // player.getBattleDeck().size());

  // for (Card card : new ArrayList<>(player.getHand())) {
  // if (player.getMana() >= card.getCost()) {
  // System.out.println("\nИгрок играет карту: " + card.getName());

  // // --- эффект карты ---
  // Slot targetSlot = player.getFirstFreeSlot(); // для SummonUnitEffect
  // card.play(context, targetSlot);

  // // Списываем ману и отправляем карту в отбой
  // player.setMana(player.getMana() - card.getCost());
  // player.playCard(card);

  // removeDeadUnits();

  // System.out.println("После розыгрыша: Мана=" + player.getMana() + ", Карты в
  // руке=" + player.getHand().size()
  // + ", Карты в отбое=" + player.getDiscard().size() + ", Карты в колоде=" +
  // player.getBattleDeck().size());
  // } else {
  // System.out.println("\nИгрок не может сыграть карту: " + card.getName() + "
  // (Мана=" + player.getMana() + ")");
  // player.playCard(card);
  // }
  // }

  // System.out.println("Конец хода игрока: Мана=" + player.getMana() + ", Карты в
  // руке=" + player.getHand().size()
  // + ", Карты в отбое=" + player.getDiscard().size());
  // }

  // Файл: GameEngine.java
  public void unitAttack(Unit unit, Enemy enemy) {
    if (!unit.isAlive() || !enemy.isAlive())
      return;

    // Юнит наносит урон врагу
    enemy.takeDamage(unit.getAttackPower());
  }

  public void counterAttack(Unit unit, Enemy enemy) {
    if (!unit.isAlive() || !enemy.isAlive())
      return;

    // Враг контратакует юнита
    unit.takeDamage(enemy.getAttackPower());
  }

  // Выносим логику "что сделает враг" — НЕ применяет изменения, только решает
  public EnemyAction planEnemyAction() {
    Enemy enemy = context.getEnemy();
    Player player = context.getPlayer();

    if (!enemy.isAlive()) {
      return EnemyAction.none();
    }

    String action = enemy.takeTurn(); // оставляем выбор внутри Enemy (как у тебя было)
    switch (action) {
    case "ATTACK":
      List<Slot> playerSlots = player.getSlots();
      List<Unit> aliveUnits = new ArrayList<>();
      for (Slot slot : playerSlots) {
        if (slot.getUnit() != null && slot.getUnit().isAlive()) {
          aliveUnits.add(slot.getUnit());
        }
      }
      if (!aliveUnits.isEmpty()) {
        Unit target = aliveUnits.get(random.nextInt(aliveUnits.size()));
        return new EnemyAction(EnemyAction.Type.ATTACK, target, enemy.getAttackPower());
      } else {
        // цель — сам игрок (targetUnit == null)
        return new EnemyAction(EnemyAction.Type.ATTACK, null, enemy.getAttackPower());
      }

    case "BUFF":
      int heal = 3; // как у тебя
      return new EnemyAction(EnemyAction.Type.BUFF, null, heal);

    default:
      return EnemyAction.none();
    }
  }

  // Применяет заранее спланированное действие к моделям
  public void executeEnemyAction(EnemyAction action) {
    Enemy enemy = context.getEnemy();
    Player player = context.getPlayer();

    switch (action.getType()) {
    case ATTACK:
      if (action.getTargetUnit() != null) {
        Unit target = action.getTargetUnit();
        target.takeDamage(action.getAmount());
        System.out.println("Враг атакует " + target.getName() + " на " + action.getAmount() + " урона.");
        // если у тебя была логика контратаки — выполните её, но лучше вынести в
        // отдельный момент
      } else {
        player.takeDamage(action.getAmount());
        System.out.println("Враг атакует игрока на " + action.getAmount() + " урона.");
      }
      break;

    case BUFF:
      enemy.heal(action.getAmount());
      System.out.println("Враг делает BUFF и восстанавливает " + action.getAmount() + " HP.");
      break;

    case NONE:
    default:
      // ничего
      break;
    }

    removeDeadUnits();
  }

  // --- Ход врага ---
  public void processEnemyTurn() {
    EnemyAction action = planEnemyAction();
    executeEnemyAction(action);
  }

  // --- Работа с картами ---
  public void drawCards(int count) {
    Player player = context.getPlayer();
    int cardsNeeded = count;

    while (cardsNeeded > 0 && player.getHand().size() < player.getMaxHand()) {
      // Если колода пуста, но есть отбой — перемешиваем
      if (player.getBattleDeck().isEmpty() && !player.getDiscard().isEmpty()) {
        player.getBattleDeck().addAll(player.getDiscard());
        player.getDiscard().clear();
        // Collections.shuffle(player.getBattleDeck());
        // System.out.println("Колода пуста. Отбой перемещён в колоду и перемешан.");
        // System.out.println("Теперь в колоде: " + player.getBattleDeck().size() + "
        // карт");
        // System.out.println("Теперь в отбое: " + player.getDiscard().size() + "
        // карт");
        // System.out.println("Карты в руке игрока: " + player.getHand().size());
      }

      // Если колода пуста и отбой пуст — прекращаем
      if (player.getBattleDeck().isEmpty())
        break;

      // Берём карту из колоды

      // System.out.println("Рука игрока: " + player.getHand().size() +
      // ", Колода: " + player.getBattleDeck().size() +
      // ", Отбой: " + player.getDiscard().size() +
      // ", Макс. рука: " + player.getMaxHand());
      player.getHand().add(player.getBattleDeck().remove(0));
      cardsNeeded--;
    }

    player.restoreMana(player.getMaxMana());
  }

  // --- Проверка конца боя ---
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

  // --- Вывод состояния ---
  public void printState() {
    Player player = context.getPlayer();
    // Enemy enemy = context.getEnemy();

    // System.out.println("\nСостояние после хода:");
    // System.out.println("Враг HP: " + enemy.getHealth());
    // System.out.println("Игрок HP: " + player.getHealth());

    for (Slot slot : player.getSlots()) {
      Unit u = slot.getUnit();
      if (u != null) {
        // System.out.println("- Слот " + slot.getId() + ": " + u.getName() + " HP: " +
        // u.getHealth() +
        // (u.isAlive() ? "" : " (мертв)"));
      }
    }
  }
}
