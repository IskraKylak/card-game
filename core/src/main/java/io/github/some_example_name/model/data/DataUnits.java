package io.github.some_example_name.model.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.some_example_name.model.Unit;

public class DataUnits {

  private static final Map<Integer, Supplier<Unit>> unitFactories;

  static {
    Map<Integer, Supplier<Unit>> map = new HashMap<>();
    map.put(1, DataUnits::createScuaer);
    map.put(2, DataUnits::createArcher);
    map.put(3, DataUnits::createSwordsman);
    unitFactories = Collections.unmodifiableMap(map);
  }

  private static int nextUnitId = 1000;

  private static int generateUniqueId() {
    return nextUnitId++;
  }

  public static Unit createScuaer() {
    return new Unit(generateUniqueId(), "Squire", 4, 1, "game/unit/squire");
  }

  public static Unit createArcher() {
    return new Unit(generateUniqueId(), "Warrior", 2, 4, "game/unit/warrior");
  }

  public static Unit createSwordsman() {
    return new Unit(generateUniqueId(), "Infantryman", 4, 2, "game/unit/infantryman");
  }

  // Получение нового экземпляра юнита по id
  public static Unit getUnitById(int unitId) {
    Supplier<Unit> factory = unitFactories.get(unitId);
    if (factory != null) {
      return factory.get();
    } else {
      throw new IllegalArgumentException("Unknown unit id: " + unitId);
    }
  }
}
