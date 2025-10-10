package io.github.some_example_name.model.data;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import io.github.some_example_name.model.Faction;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.status.StatusEffect;
import io.github.some_example_name.model.status.TargetingRule;

public class PlayerLoader {

  public static List<Player> loadPlayers(String path) {
    List<Player> players = new ArrayList<>();

    try (FileReader fr = new FileReader(path)) {
      JsonElement rootEl = JsonParser.parseReader(fr);
      if (!rootEl.isJsonArray())
        return players;

      JsonArray arr = rootEl.getAsJsonArray();

      for (JsonElement el : arr) {
        if (!el.isJsonObject())
          continue;

        JsonObject jo = el.getAsJsonObject();

        int id = jo.has("id") ? jo.get("id").getAsInt() : 0;
        String name = jo.has("name") ? jo.get("name").getAsString() : "Unknown";
        int health = jo.has("health") ? jo.get("health").getAsInt() : 10;
        int maxMana = jo.has("maxMana") ? jo.get("maxMana").getAsInt() : 3;
        int maxHand = jo.has("maxHand") ? jo.get("maxHand").getAsInt() : 5;
        int maxUnits = jo.has("maxUnits") ? jo.get("maxUnits").getAsInt() : 5;
        Faction faction = jo.has("faction") ? Faction.valueOf(jo.get("faction").getAsString()) : Faction.LIFE;

        // --- Загружаем эффекты игрока ---
        List<StatusEffect> activeEffects = new ArrayList<>();
        if (jo.has("activeEffects") && jo.get("activeEffects").isJsonArray()) {
          JsonArray arrEff = jo.getAsJsonArray("activeEffects");
          for (JsonElement e : arrEff) {
            if (!e.isJsonObject())
              continue;
            StatusEffect effect = deserializeEffect(e.getAsJsonObject());
            if (effect != null)
              activeEffects.add(effect);
          }
        }

        Player player = new Player(id, name, health, maxMana, maxHand, maxUnits, faction, activeEffects);
        players.add(player);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return players;
  }

  // --- Создание эффекта через reflection ---
  private static StatusEffect deserializeEffect(JsonObject o) {
    String type = o.has("type") ? o.get("type").getAsString()
        : o.has("effectType") ? o.get("effectType").getAsString()
            : null;
    if (type == null)
      return null;

    int duration = o.has("duration") ? o.get("duration").getAsInt() : 1;
    int amount = o.has("amount") ? o.get("amount").getAsInt()
        : o.has("bonus") ? o.get("bonus").getAsInt()
            : o.has("damage") ? o.get("damage").getAsInt()
                : 1;
    int targetCount = o.has("targetCount") ? o.get("targetCount").getAsInt() : 1;

    TargetingRule tr = parseTargetingRule(o);

    try {
      String className = "io.github.some_example_name.model.status." + type;
      Class<?> clazz = Class.forName(className);
      return (StatusEffect) clazz
          .getConstructor(int.class, int.class, TargetingRule.class, int.class)
          .newInstance(duration, amount, tr, targetCount);
    } catch (Exception e) {
      System.out.println("Не удалось создать эффект: " + type + " — " + e.getMessage());
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

  public static Player getPlayerByFaction(Faction faction) {
    return loadPlayers("data/players.json").stream()
        .filter(p -> p.getFaction() == faction)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown faction: " + faction));
  }
}
