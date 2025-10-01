package io.github.some_example_name.model.status;

/**
 * Перечисление правил выбора цели для StatusEffect при ИИ.
 */
public enum TargetingRule {
  NONE, // Нет правила (например, карты игрока, цель выбирается вручную)
  SELF, // Всегда применяет эффект на себя
  PLAYER, // Всегда на игрока
  ALLY, // На союзного юнита
  ENEMY, // На врага
  RANDOM_ALLY, // Случайный союзник
  RANDOM_ENEMY // Случайный враг
}
