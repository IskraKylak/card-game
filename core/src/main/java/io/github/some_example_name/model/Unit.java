package io.github.some_example_name.model;

import java.util.List;

import io.github.some_example_name.model.status.StatusEffect;

public class Unit extends CombatEntity {
  private String spriteFolder;

  public Unit(int id, String name, String description, int health, int attack, String spriteFolder,
      int maxActionsPerTurn,
      List<StatusEffect> spells) {
    super(id, name, description, health, attack, maxActionsPerTurn, spells);
    this.spriteFolder = spriteFolder;
  }

  @Override
  public String getSpriteFolder() {
    return spriteFolder;
  }
}
