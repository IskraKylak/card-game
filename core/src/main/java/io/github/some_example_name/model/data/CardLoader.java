package io.github.some_example_name.model.data;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.some_example_name.core.effects.*;
import io.github.some_example_name.model.*;

import io.github.some_example_name.model.status.AttackBuffEffect;
import io.github.some_example_name.model.status.PoisonEffect;
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
  }

  private static class EffectJson {
    String type; // DamageEffect, BuffEffect, etc
    Integer value;
    Integer duration;
    Integer unitId;
    String status; // AttackBuffEffect, PoisonEffect
  }

  public static List<Card> loadCards(String path) {
    try {
      Gson gson = new Gson();
      List<CardJson> data = gson.fromJson(new FileReader(path),
          new TypeToken<List<CardJson>>() {
          }.getType());

      List<Card> result = new ArrayList<>();
      for (CardJson c : data) {
        CardEffect effect = null;

        switch (c.effect.type) {
          case "SummonUnitEffect":
            effect = new SummonUnitEffect(c.effect.unitId);
            break;
          case "DamageEffect":
            effect = new DamageEffect(c.effect.value);
            break;
          case "BuffEffect":
            if ("AttackBuffEffect".equals(c.effect.status)) {
              effect = new BuffEffect(
                  () -> new AttackBuffEffect(c.effect.value, c.effect.duration, TargetingRule.NONE));
            }
            break;
          case "DebuffEffect":
            if ("PoisonEffect".equals(c.effect.status)) {
              effect = new DebuffEffect(() -> new PoisonEffect(c.effect.value, c.effect.duration, TargetingRule.NONE));
            }
            break;
        }

        Card card = new Card(
            c.id,
            c.name,
            c.description,
            c.cost,
            CardType.valueOf(c.type),
            Faction.valueOf(c.faction),
            effect,
            c.image);

        result.add(card);
      }
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
