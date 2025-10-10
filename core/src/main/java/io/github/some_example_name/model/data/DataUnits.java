package io.github.some_example_name.model.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.some_example_name.model.Unit;
import io.github.some_example_name.model.status.StatusEffect;

public class DataUnits {

  private static final String PATH = "data/units.json"; // путь к JSON
  private static final Map<Integer, UnitTemplate> unitTemplates = new HashMap<>();

  private static int nextUnitId = 1000;

  static {
    // Загружаем юнитов из JSON
    List<Unit> unitsFromJson = UnitLoader.loadUnits(PATH);
    for (Unit u : unitsFromJson) {
      unitTemplates.put(u.getId(),
          new UnitTemplate(
              u.getName(),
              u.getDescription(),
              u.getHealth(),
              u.getAttackPower(),
              u.getSpriteFolder(),
              u.getMaxActionsPerTurn(),
              u.getSpells(),
              u.getStatusEffects() // добавили statusEffects
          ));
    }
  }

  // создаём новый объект юнита по id
  public static Unit getUnitById(int unitId) {
    UnitTemplate template = unitTemplates.get(unitId);
    if (template == null)
      throw new IllegalArgumentException("Unknown unit id: " + unitId);

    // создаем копии списков, чтобы не делились ссылками между юнитами
    List<StatusEffect> clonedSpells = StatusEffect.cloneList(template.spells);
    List<StatusEffect> clonedActiveEffects = StatusEffect.cloneList(template.activeEffects);

    return new Unit(
        generateUniqueId(),
        template.name,
        template.description,
        template.health,
        template.attack,
        template.sprite,
        template.maxActionsPerTurn,
        clonedSpells,
        clonedActiveEffects);
  }

  private static int generateUniqueId() {
    return nextUnitId++;
  }

  private static class UnitTemplate {
    final String name;
    final String description;
    final int health;
    final int attack;
    final String sprite;
    final int maxActionsPerTurn;
    final List<StatusEffect> spells;
    final List<StatusEffect> activeEffects;

    UnitTemplate(String name, String description, int health, int attack, String sprite, int maxActionsPerTurn,
        List<StatusEffect> spells, List<StatusEffect> activeEffects) {
      this.name = name;
      this.description = description;
      this.health = health;
      this.attack = attack;
      this.sprite = sprite;
      this.maxActionsPerTurn = maxActionsPerTurn;
      this.spells = spells;
      this.activeEffects = activeEffects;
    }
  }
}
