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
    context.getPlayer().initBattleDeck();
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
  public boolean playCardOnTarget(Card card, Object target) {
    Player player = context.getPlayer();

    // Проверяем ману
    if (player.getMana() < card.getCost()) {
      System.out.println("Недостаточно маны для розыгрыша карты: " + card.getName());
      return false;
    }

    // --- Цель: слот ---
    if (target instanceof Slot) {
      Slot slot = (Slot) target;

      // Если карта — юнит
      if (card.getType() == CardType.UNIT) {
        if (!slot.isOccupied()) {
          System.out.println("Игрок разыгрывает карту-юнита: " + card.getName() + " в слот " + slot.getId());

          // Запускаем эффект карты
          card.play(context, slot);

          // Списываем ману
          player.setMana(player.getMana() - card.getCost());

          // Отправляем карту в отбой
          player.playCard(card);

          return true;
        } else {
          System.out.println("Слот " + slot.getId() + " занят, нельзя разыграть юнита.");
          return false;
        }
      }

      // Если карта НЕ юнит → пока ничего не делаем
      System.out.println("Карта " + card.getName() + " не может быть разыграна на слот.");
      return false;
    }

    // --- Цель неизвестна ---
    System.out.println("Неверная цель для карты: " + card.getName());
    return false;
  }

  // --- Игрок разыгрывает карты ---
  public void playPlayerHand() {
    Player player = context.getPlayer();
    drawCards(4);
    player.restoreMana(player.getMaxMana());

    System.out.println("Начало хода игрока: Мана=" + player.getMana() + ", Карты в руке=" + player.getHand().size() +
        ", Карты в отбое=" + player.getDiscard().size() + ", Карты в колоде=" + player.getBattleDeck().size());

    for (Card card : new ArrayList<>(player.getHand())) {
      if (player.getMana() >= card.getCost()) {
        System.out.println("\nИгрок играет карту: " + card.getName());

        // --- эффект карты ---
        Slot targetSlot = player.getFirstFreeSlot(); // для SummonUnitEffect
        card.play(context, targetSlot);

        // Списываем ману и отправляем карту в отбой
        player.setMana(player.getMana() - card.getCost());
        player.playCard(card);

        removeDeadUnits();

        System.out.println("После розыгрыша: Мана=" + player.getMana() + ", Карты в руке=" + player.getHand().size() +
            ", Карты в отбое=" + player.getDiscard().size() + ", Карты в колоде=" + player.getBattleDeck().size());
      } else {
        System.out.println("\nИгрок не может сыграть карту: " + card.getName() +
            " (Мана=" + player.getMana() + ")");
        player.discardCard(card);
      }
    }

    System.out.println("Конец хода игрока: Мана=" + player.getMana() + ", Карты в руке=" + player.getHand().size() +
        ", Карты в отбое=" + player.getDiscard().size());
  }

  // --- Ход юнитов игрока ---
  public void processPlayerUnitsTurn() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit == null || !unit.isAlive())
        continue;

      // Атака врага
      enemy.takeDamage(unit.getAttack());
      System.out.println(unit.getName() + " атакует врага на " + unit.getAttack() +
          " урона. Враг HP: " + enemy.getHealth());

      // Контратака врага
      if (enemy.isAlive()) {
        unit.takeDamage(enemy.getAttackPower());
        System.out.println("Враг контратакует " + unit.getName() + " на " +
            enemy.getAttackPower() + " урона. " + unit.getName() +
            " HP: " + unit.getHealth());
      }
    }

    removeDeadUnits();
  }

  // Файл: GameEngine.java
  public void unitAttack(Unit unit, Enemy enemy) {
    if (!unit.isAlive() || !enemy.isAlive())
      return;

    // Юнит наносит урон врагу
    enemy.takeDamage(unit.getAttack());

    // Враг контратакует
    if (enemy.isAlive()) {
      unit.takeDamage(enemy.getAttackPower());
    }
  }

  // --- Ход врага ---
  public void processEnemyTurn() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    if (!enemy.isAlive())
      return;

    String action = enemy.takeTurn();
    System.out.println("Враг делает " + action);

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
          target.takeDamage(enemy.getAttackPower());
          System.out.println("Враг атакует " + target.getName() + " на " +
              enemy.getAttackPower() + " урона.");
          if (target.isAlive()) {
            target.takeDamage(enemy.getAttackPower()); // контратака
          }
        } else {
          player.takeDamage(enemy.getAttackPower());
          System.out.println("Враг атакует игрока на " + enemy.getAttackPower() + " урона.");
        }
        break;

      case "BUFF":
        int heal = 3;
        enemy.heal(heal);
        System.out.println("Враг делает BUFF и восстанавливает " + heal + " HP. Враг HP: " + enemy.getHealth());
        break;
    }

    removeDeadUnits();
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
        System.out.println("Колода пуста. Отбой перемещён в колоду и перемешан.");
        System.out.println("Теперь в колоде: " + player.getBattleDeck().size() + " карт");
        System.out.println("Теперь в отбое: " + player.getDiscard().size() + " карт");
        System.out.println("Карты в руке игрока: " + player.getHand().size());
      }

      // Если колода пуста и отбой пуст — прекращаем
      if (player.getBattleDeck().isEmpty())
        break;

      // Берём карту из колоды
      player.getHand().add(player.getBattleDeck().remove(0));
      cardsNeeded--;
    }
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
    Enemy enemy = context.getEnemy();

    System.out.println("\nСостояние после хода:");
    System.out.println("Враг HP: " + enemy.getHealth());
    System.out.println("Игрок HP: " + player.getHealth());

    for (Slot slot : player.getSlots()) {
      Unit u = slot.getUnit();
      if (u != null) {
        System.out.println("- Слот " + slot.getId() + ": " + u.getName() + " HP: " + u.getHealth() +
            (u.isAlive() ? "" : " (мертв)"));
      }
    }
  }
}
