package io.github.some_example_name.model.status;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.model.Entity;

public interface StatusEffect {

  int getTargetCount();

  /** Имя эффекта (например, "Poison", "Strength Buff") */
  String getName();

  StatusType getType(); // новый метод

  boolean isNegative(); // новый метод

  /** Сколько ходов ещё действует эффект */
  int getDuration();

  /** Применяется в момент наложения */
  void onApply(Entity target);

  /** Применяется каждый ход */
  void onTurnStart(Entity target);

  /** Применяется в момент снятия */
  void onRemove(Entity target);

  /** Уменьшает длительность, возвращает true если эффект всё ещё активен */
  boolean tick(Entity target);

  TargetingRule getTargetingRule();

  public abstract AbstractStatusEffect copy();

  /**
   * Утилита: клонирует список эффектов, вызывая copy() у каждого.
   * Возвращает новый ArrayList (никогда не возвращает null).
   */
  static List<StatusEffect> cloneList(List<StatusEffect> list) {
    List<StatusEffect> out = new ArrayList<>();
    if (list == null || list.isEmpty()) {
      return out;
    }
    for (StatusEffect e : list) {
      if (e == null)
        continue;
      try {
        StatusEffect c = e.copy();
        if (c != null)
          out.add(c);
      } catch (Exception ex) {
        // защита от возможных ошибок в copy() — лучше логировать, но не падать
        ex.printStackTrace();
      }
    }
    return out;
  }
}
