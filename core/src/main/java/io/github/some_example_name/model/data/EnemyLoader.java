package io.github.some_example_name.model.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.status.AttackBuffEffect;
import io.github.some_example_name.model.status.PoisonEffect;
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

        enemies.add(new Enemy(id, name, health, attack, sprite, maxActions, spells));
      }

    } catch (Exception ex) {
      ex.printStackTrace();
      // на случай ошибки возвращаем то, что успели загрузить (или пустой список)
    }

    return enemies;
  }

  // парсер одного объекта заклинания из JSON -> StatusEffect
  private static StatusEffect deserializeEffect(JsonObject o) {
    // допустимые ключи: "type" или "effectType"
    String type = null;
    if (o.has("type"))
      type = o.get("type").getAsString();
    else if (o.has("effectType"))
      type = o.get("effectType").getAsString();
    if (type == null)
      return null;

    switch (type) {
      case "AttackBuff":
      case "AttackBuffEffect": {
        int bonus = o.has("bonusAttack") ? o.get("bonusAttack").getAsInt()
            : o.has("bonus") ? o.get("bonus").getAsInt() : 1;
        int duration = o.has("duration") ? o.get("duration").getAsInt() : 1;
        TargetingRule tr = parseTargetingRule(o);
        return new AttackBuffEffect(bonus, duration, tr);
      }
      case "Poison":
      case "PoisonEffect": {
        int duration = o.has("duration") ? o.get("duration").getAsInt() : 1;
        int dmg = o.has("damagePerTurn") ? o.get("damagePerTurn").getAsInt()
            : o.has("damage") ? o.get("damage").getAsInt() : 1;
        TargetingRule tr = parseTargetingRule(o);
        return new PoisonEffect(duration, dmg, tr);
      }
      // добавляй case для других эффектов по мере необходимости
      default:
        System.out.println("Unknown effect type in JSON: " + type);
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
