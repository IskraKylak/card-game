package io.github.some_example_name.model.data;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.status.StatusEffect;

public class EnemyLoader {

  private static class EnemyJson {
    int id;
    String name;
    int health;
    int attack;
    String sprite;
    int maxActionsPerTurn;
    List<StatusEffect> spells; // заглушка, можно расширить позже
  }

  public static List<Enemy> loadEnemies(String path) {
    try {
      Gson gson = new Gson();
      List<EnemyJson> data = gson.fromJson(new FileReader(path),
          new TypeToken<List<EnemyJson>>() {
          }.getType());

      List<Enemy> enemies = new ArrayList<>();
      for (EnemyJson e : data) {
        enemies.add(new Enemy(e.id, e.name, e.health, e.attack, e.sprite, e.maxActionsPerTurn, e.spells));
      }
      return enemies;
    } catch (Exception ex) {
      ex.printStackTrace();
      return new ArrayList<>();
    }
  }
}
