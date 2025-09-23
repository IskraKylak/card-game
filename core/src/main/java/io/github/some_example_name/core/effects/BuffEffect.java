package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Unit;
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
      unit.addStatusEffect(effect);
      System.out.println("На " + unit.getName() + " наложен эффект: " + effect.getName());
      return true;
    }
    System.out.println("Эффект можно применить только на юнита!");
    return false;
  }
}
