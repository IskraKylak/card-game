package io.github.some_example_name.model.status;

/**
 * Интерфейс для эффектов, которые могут иметь правило выбора цели.
 * Используется для ИИ (юниты и враги), чтобы понимать, на кого применять
 * заклинание.
 */
public interface TargetableStatusEffect extends StatusEffect {

  /**
   * Получить правило выбора цели.
   * Например, SELF, PLAYER, RANDOM_ENEMY и т.д.
   */
  TargetingRule getTargetingRule();
}
