package io.github.some_example_name.core;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Slot;
import io.github.some_example_name.model.Unit;
import io.github.some_example_name.model.Enemy;

public class GameContext {

  private Player player; // Игрок
  private Enemy enemy; // Враг

  public GameContext(Player player, Enemy enemy) {
    this.player = player;
    this.enemy = enemy;
  }

  // --- Геттеры ---
  public Player getPlayer() {
    return player;
  }

  public Enemy getEnemy() {
    return enemy;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public void setEnemy(Enemy enemy) {
    this.enemy = enemy;
  }

  /** Возвращает список юнитов игрока через слоты */
  public List<Unit> getPlayerUnits() {
    List<Unit> units = new ArrayList<>();
    for (Slot slot : player.getSlots()) {
      if (slot.isOccupied()) {
        units.add(slot.getUnit());
      }
    }
    return units;
  }

  /** Очистка слотов перед новым боем */
  public void reset() {
    for (Slot slot : player.getSlots()) {
      slot.removeUnit();
    }
    player.setHealth(player.getMaxHealth());
    player.setMana(player.getMaxMana());
    // карты игрока очищаются через методы игрока
  }
}
