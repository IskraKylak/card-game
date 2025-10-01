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
    // загружаем юнитов из JSON
    List<Unit> unitsFromJson = UnitLoader.loadUnits(PATH);
    for (Unit u : unitsFromJson) {
      unitTemplates.put(u.getId(), new UnitTemplate(u.getName(), u.getHealth(), u.getAttackPower(), u.getSpriteFolder(),
          u.getMaxActionsPerTurn(), u.getSpells()));
    }
  }

  // создаём новый объект юнита по id
  public static Unit getUnitById(int unitId) {
    UnitTemplate template = unitTemplates.get(unitId);
    if (template == null)
      throw new IllegalArgumentException("Unknown unit id: " + unitId);

    return new Unit(generateUniqueId(), template.name, template.health, template.attack, template.sprite,
        template.maxActionsPerTurn, template.spells);
  }

  private static int generateUniqueId() {
    return nextUnitId++;
  }

  private static class UnitTemplate {
    final String name;
    final int health;
    final int attack;
    final String sprite;
    final int maxActionsPerTurn;
    final List<StatusEffect> spells;

    UnitTemplate(String name, int health, int attack, String sprite, int maxActionsPerTurn, List<StatusEffect> spells) {
      this.name = name;
      this.health = health;
      this.attack = attack;
      this.sprite = sprite;
      this.maxActionsPerTurn = maxActionsPerTurn;
      this.spells = spells;
    }
  }
}
