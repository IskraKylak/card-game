package io.github.some_example_name.model.data;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import io.github.some_example_name.core.effects.*;
import io.github.some_example_name.model.*;
import io.github.some_example_name.model.status.StatusEffect;
import io.github.some_example_name.model.status.TargetingRule;

public class CardLoader {

  private static class CardJson {
    int id;
    String name;
    String description;
    int cost;
    String type;
    String faction;
    JsonElement effect; // теперь может быть объектом или массивом
    String image;
    boolean isBurnOnPlay;
    int countTarget;
  }

  private static class EffectJson {
    String type;
    Integer value;
    Integer duration;
    Integer unitId;
    String status;
  }

  public static List<Card> loadCards(String path) {
    try {
      Gson gson = new Gson();
      List<CardJson> data = gson.fromJson(new FileReader(path),
          new TypeToken<List<CardJson>>() {
          }.getType());

      List<Card> result = new ArrayList<>();

      for (CardJson c : data) {
        List<CardEffect> effects = new ArrayList<>();

        // --- поддержка одиночного эффекта ---
        if (c.effect != null && c.effect.isJsonObject()) {
          EffectJson e = gson.fromJson(c.effect, EffectJson.class);
          CardEffect ce = createEffect(e);
          if (ce != null)
            effects.add(ce);
        }

        // --- поддержка списка эффектов ---
        else if (c.effect != null && c.effect.isJsonArray()) {
          JsonArray arr = c.effect.getAsJsonArray();
          for (JsonElement el : arr) {
            EffectJson e = gson.fromJson(el, EffectJson.class);
            CardEffect ce = createEffect(e);
            if (ce != null)
              effects.add(ce);
          }
        }

        Card card = new Card(
            c.id,
            c.name,
            c.description,
            c.cost,
            CardType.valueOf(c.type),
            Faction.valueOf(c.faction),
            effects, // <-- список эффектов
            c.image,
            c.isBurnOnPlay,
            c.countTarget);

        result.add(card);
      }

      return result;

    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  // --- Динамическое создание эффектов ---
  private static CardEffect createEffect(EffectJson e) {
    try {
      switch (e.type) {
        case "SummonUnitEffect":
          return new SummonUnitEffect(e.unitId);

        case "DamageEffect":
          return new DamageEffect(e.value);

        case "BuffEffect":
          return new BuffEffect(() -> (StatusEffect) createStatus(e.status, e.value, e.duration));

        case "DebuffEffect":
          return new DebuffEffect(() -> (StatusEffect) createStatus(e.status, e.value, e.duration));

        default:
          System.err.println("⚠️ Unknown effect type: " + e.type);
          return null;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  // --- Создание status-эффекта по названию класса ---
  private static Object createStatus(String className, int value, int duration) {
    try {
      String fullName = "io.github.some_example_name.model.status." + className;
      Class<?> clazz = Class.forName(fullName);
      Constructor<?> ctor = clazz.getConstructor(int.class, int.class, TargetingRule.class, int.class);
      return ctor.newInstance(duration, value, TargetingRule.NONE, 1);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
