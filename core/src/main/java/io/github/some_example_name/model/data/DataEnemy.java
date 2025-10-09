package io.github.some_example_name.model.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.status.StatusEffect;

public class DataEnemy {

  private static final String PATH = "data/enemies.json";

  private static final Map<Integer, EnemyTemplate> enemyTemplates = new HashMap<>();

  static {
    List<Enemy> enemies = EnemyLoader.loadEnemies(PATH);
    for (Enemy e : enemies) {
      enemyTemplates.put(e.getId(),
          new EnemyTemplate(e.getName(), e.getDescription(), e.getHealth(), e.getAttackPower(), e.getSpriteFolder(),
              e.getMaxActionsPerTurn(), e.getSpells()));
    }
  }

  // Создание нового экземпляра врага по id
  public static Enemy getEnemyById(int enemyId) {
    EnemyTemplate template = enemyTemplates.get(enemyId);
    if (template == null) {
      throw new IllegalArgumentException("Unknown enemy id: " + enemyId);
    }
    return new Enemy(generateUniqueId(), template.name, template.description, template.health, template.attack,
        template.sprite,
        template.maxActionsPerTurn, template.spells);
  }

  private static int nextEnemyId = 5000; // уникальные ID для инстансов

  private static int generateUniqueId() {
    return nextEnemyId++;
  }

  // Шаблон врага
  private static class EnemyTemplate {
    final String name;
    final String description;
    final int health;
    final int attack;
    final String sprite;
    final int maxActionsPerTurn;
    final List<StatusEffect> spells; // можно добавить позже

    EnemyTemplate(String name, String description, int health, int attack, String sprite, int maxActionsPerTurn,
        List<StatusEffect> spells) {
      this.name = name;
      this.description = description;
      this.health = health;
      this.attack = attack;
      this.sprite = sprite;
      this.maxActionsPerTurn = maxActionsPerTurn;
      this.spells = spells;
    }
  }
}
