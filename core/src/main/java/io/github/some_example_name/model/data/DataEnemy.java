package io.github.some_example_name.model.data;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.model.Enemy;

public class DataEnemy {

  // Шаблоны врагов
  private static final Enemy GOBLIN_TEMPLATE = new Enemy("Goblin", 10, 2);
  private static final Enemy ORC_TEMPLATE = new Enemy("Orc", 20, 4);
  private static final Enemy MAGE_TEMPLATE = new Enemy("Mage", 12, 3);

  // Создать список всех врагов (новые экземпляры для боя)
  public static List<Enemy> createAllEnemies() {
    List<Enemy> enemies = new ArrayList<>();
    enemies.add(createGoblin());
    enemies.add(createOrc());
    enemies.add(createMage());
    return enemies;
  }

  // Создаём новые экземпляры врагов для боя
  public static Enemy createGoblin() {
    return new Enemy(GOBLIN_TEMPLATE.getName(), GOBLIN_TEMPLATE.getHealth(), GOBLIN_TEMPLATE.getAttackPower());
  }

  public static Enemy createOrc() {
    return new Enemy(ORC_TEMPLATE.getName(), ORC_TEMPLATE.getHealth(), ORC_TEMPLATE.getAttackPower());
  }

  public static Enemy createMage() {
    return new Enemy(MAGE_TEMPLATE.getName(), MAGE_TEMPLATE.getHealth(), MAGE_TEMPLATE.getAttackPower());
  }
}
