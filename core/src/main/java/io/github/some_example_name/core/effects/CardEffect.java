package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Entity;
import io.github.some_example_name.model.Targetable;

public interface CardEffect {
  /**
   * Применяет эффект карты на указанную цель.
   * 
   * @param context текущий контекст игры
   * @param target  цель, на которую применяется карта
   * @return true если эффект применён успешно, false иначе
   */
  boolean apply(GameContext context, Targetable target);
}
