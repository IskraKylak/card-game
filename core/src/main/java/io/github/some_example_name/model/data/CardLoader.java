package io.github.some_example_name.model.data;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
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
    EffectJson effect;
    String image;
    boolean isBurnOnPlay;
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
        CardEffect effect = createEffect(c.effect);
        Card card = new Card(
            c.id,
            c.name,
            c.description,
            c.cost,
            CardType.valueOf(c.type),
            Faction.valueOf(c.faction),
            effect,
            c.image,
            c.isBurnOnPlay);

        result.add(card);
      }
      return result;

    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  // 👇 создаёт эффекты динамически
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

  // 👇 создаёт status-эффект по названию класса (из JSON)
  private static Object createStatus(String className, int value, int duration) {
    try {
      // полный путь к классу
      String fullName = "io.github.some_example_name.model.status." + className;

      // получаем класс и конструктор
      Class<?> clazz = Class.forName(fullName);
      Constructor<?> ctor = clazz.getConstructor(int.class, int.class, TargetingRule.class);

      // создаём объект
      return ctor.newInstance(duration, value, TargetingRule.NONE);

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
