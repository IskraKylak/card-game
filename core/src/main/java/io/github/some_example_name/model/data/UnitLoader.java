package io.github.some_example_name.model.data;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.some_example_name.model.Unit;
import io.github.some_example_name.model.status.StatusEffect;

public class UnitLoader {

  private static class UnitJson {
    int id;
    String name;
    int health;
    int attack;
    String sprite;
    int maxActionsPerTurn;
    List<StatusEffect> spells; // заглушка, можно расширить позже
  }

  public static List<Unit> loadUnits(String path) {
    try {
      Gson gson = new Gson();
      List<UnitJson> data = gson.fromJson(new FileReader(path),
          new TypeToken<List<UnitJson>>() {
          }.getType());

      List<Unit> units = new ArrayList<>();
      for (UnitJson u : data) {
        units.add(new Unit(u.id, u.name, u.health, u.attack, u.sprite, u.maxActionsPerTurn, u.spells));
      }
      return units;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
