package io.github.some_example_name.core;

import io.github.some_example_name.model.*;
import io.github.some_example_name.model.data.*;

public class BattleTest {
  public static void main(String[] args) {
    // Создаём игрока
    Player player = DataPlayers.createLifePlayer();
    player.buildDefaultDeckFromFaction();
    player.buildBattleDeck();
    player.initBattleDeck();

    // Создаём врага
    Enemy enemy = DataEnemy.createMage();

    // Контекст игры (например, 6 слотов)
    GameContext context = new GameContext(player, enemy);

    // Движок
    GameEngine engine = new GameEngine(context);

    System.out.println("=== Начало боя ===");
    System.out.println("Игрок HP: " + player.getHealth() + " | Враг HP: " + enemy.getHealth());

    int turn = 1;
    int maxTurns = 20; // ограничение

    while (!engine.isBattleOver() && turn <= maxTurns) {
      System.out.println("\n=== Ход " + turn + " ===");

      // --- Ход игрока ---
      System.out.println("\nХод игрока:");
      engine.playPlayerHand();

      // --- Ход юнитов игрока ---
      System.out.println("\nХод юнитов игрока:");

      // Вывод состояния после хода игрока
      engine.printState();

      if (engine.isBattleOver())
        break;

      // --- Ход врага ---
      System.out.println("\nХод врага:");
      engine.processEnemyTurn();

      // Вывод состояния после хода врага
      engine.printState();

      turn++;
    }

    System.out.println("\n=== Конец боя ===");
    System.out.println("Победитель: " + engine.getWinner());
  }
}
