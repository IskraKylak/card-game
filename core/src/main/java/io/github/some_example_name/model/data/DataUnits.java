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
    return new Unit(generateUniqueId(), "Скваер", 4, 1);
  }

  public static Unit createArcher() {
    return new Unit(generateUniqueId(), "Лучник", 2, 4);
  }

  public static Unit createSwordsman() {
    return new Unit(generateUniqueId(), "Пехотинец", 4, 2);
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
