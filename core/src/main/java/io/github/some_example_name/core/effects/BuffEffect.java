package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.BattleEvent;
import io.github.some_example_name.core.BattleEventType;
import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Unit;
import io.github.some_example_name.model.payload.StatusEffectPayload;
import io.github.some_example_name.model.Targetable;
import io.github.some_example_name.model.status.StatusEffect;
import java.util.function.Supplier;

public class BuffEffect implements CardEffect {
  private final Supplier<StatusEffect> effectSupplier;

  public BuffEffect(Supplier<StatusEffect> effectSupplier) {
    this.effectSupplier = effectSupplier;
  }

  @Override
  public boolean apply(GameContext context, Targetable target) {
    if (target instanceof Unit) {
      Unit unit = (Unit) target;
      StatusEffect effect = effectSupplier.get(); // ← создаём новый экземпляр

      // 🔹 Проверяем, есть ли такой эффект у юнита
      StatusEffect existing = unit.getStatusEffects().stream()
          .filter(e -> e.getName().equals(effect.getName())) // можно e.getClass() == effect.getClass()
          .findFirst()
          .orElse(null);

      if (existing != null) {
        existing.onRemove(unit);
        unit.removeStatusEffect(existing);
      }
      // ✅ Сначала применяем мгновенный эффект, если есть
      effect.onApply(unit);
      context.getEventBus().emit(BattleEvent.of(
          BattleEventType.STATUS_EFFECT_APPLIED,
          new StatusEffectPayload(unit, effect, () -> {
            // Callback для завершения анимации или логики, если нужно
          })));

      if (effect.getDuration() > 0) {
        unit.addStatusEffect(effect); // добавляем в список, если длительный
      }

      System.out.println("На " + unit.getName() + " наложен эффект: " + effect.getName());
      return true;
    }
    System.out.println("Эффект можно применить только на юнита!");
    return false;
  }
}
