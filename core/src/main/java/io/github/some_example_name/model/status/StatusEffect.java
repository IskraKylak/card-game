package io.github.some_example_name.model.status;

import io.github.some_example_name.model.Entity;

public interface StatusEffect {
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
}
