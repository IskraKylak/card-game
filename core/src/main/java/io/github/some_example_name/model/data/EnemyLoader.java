package io.github.some_example_name.model.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.status.StatusEffect;
import io.github.some_example_name.model.status.TargetingRule;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EnemyLoader {

  public static List<Enemy> loadEnemies(String path) {
    List<Enemy> enemies = new ArrayList<>();
    try (FileReader fr = new FileReader(path)) {
      JsonElement rootEl = JsonParser.parseReader(fr);
      if (!rootEl.isJsonArray())
        return enemies;
      JsonArray arr = rootEl.getAsJsonArray();

      for (JsonElement el : arr) {
        if (!el.isJsonObject())
          continue;
        JsonObject jo = el.getAsJsonObject();

        int id = jo.has("id") ? jo.get("id").getAsInt() : 0;
        String name = jo.has("name") ? jo.get("name").getAsString() : "Unknown";
        String description = jo.has("description") ? jo.get("description").getAsString() : "";
        int health = jo.has("health") ? jo.get("health").getAsInt() : 1;
        int attack = jo.has("attack") ? jo.get("attack").getAsInt() : 0;
        String sprite = jo.has("sprite") ? jo.get("sprite").getAsString() : "";
        int maxActions = jo.has("maxActionsPerTurn") ? jo.get("maxActionsPerTurn").getAsInt() : 1;

        List<StatusEffect> spells = new ArrayList<>();
        if (jo.has("spells") && jo.get("spells").isJsonArray()) {
          JsonArray spellsArr = jo.getAsJsonArray("spells");
          for (JsonElement se : spellsArr) {
            if (!se.isJsonObject())
              continue;
            StatusEffect effect = deserializeEffect(se.getAsJsonObject());
            if (effect != null)
              spells.add(effect);
          }
        }

        enemies.add(new Enemy(id, name, description, health, attack, sprite, maxActions, spells));
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return enemies;
  }

  // Динамическое создание эффектов через рефлексию
  private static StatusEffect deserializeEffect(JsonObject o) {
    String type = o.has("type") ? o.get("type").getAsString()
        : o.has("effectType") ? o.get("effectType").getAsString()
            : null;
    if (type == null)
      return null;

    int duration = o.has("duration") ? o.get("duration").getAsInt() : 1;
    int amount = o.has("amount") ? o.get("amount").getAsInt()
        : o.has("damagePerTurn") ? o.get("damagePerTurn").getAsInt()
            : o.has("damage") ? o.get("damage").getAsInt()
                : 1;
    int targetCount = o.has("targetCount") ? o.get("targetCount").getAsInt() : 1;

    TargetingRule tr = parseTargetingRule(o);

    try {
      // Собираем полное имя класса
      String className = "io.github.some_example_name.model.status." + type;
      Class<?> clazz = Class.forName(className);

      // используем новый конструктор с targetCount
      return (StatusEffect) clazz
          .getConstructor(int.class, int.class, TargetingRule.class, int.class)
          .newInstance(duration, amount, tr, targetCount);

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Не удалось создать эффект: " + type);
      return null;
    }
  }

  private static TargetingRule parseTargetingRule(JsonObject o) {
    if (!o.has("targetingRule"))
      return TargetingRule.NONE;
    try {
      String s = o.get("targetingRule").getAsString().toUpperCase(Locale.ROOT);
      return TargetingRule.valueOf(s);
    } catch (Exception ex) {
      return TargetingRule.NONE;
    }
  }
}
