package io.github.some_example_name.model;

import java.util.List;
import java.util.Random;

import io.github.some_example_name.model.status.StatusEffect;

public class Enemy extends CombatEntity {
  private String spriteFolder;

  public Enemy(int id, String name, String description, int health, int attackPower, String spriteFolder,
      int maxActionsPerTurn,
      List<StatusEffect> spells) {
    super(id, name, description, health, attackPower, maxActionsPerTurn, spells);
    this.spriteFolder = spriteFolder;
  }

  public String takeTurn() {
    int choice = new Random().nextInt(3);
    switch (choice) {
      case 0:
        return "ATTACK";
      case 1:
        return "BUFF";
      default:
        return "ATTACK";
    }
  }

  @Override
  public String getSpriteFolder() {
    return spriteFolder;
  }
}
