package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Entity;
import io.github.some_example_name.model.Targetable;
import io.github.some_example_name.model.status.StatusEffect;
import java.util.function.Supplier;

public class DebuffEffect implements CardEffect {
  private final Supplier<StatusEffect> effectSupplier;

  public DebuffEffect(Supplier<StatusEffect> effectSupplier) {
    this.effectSupplier = effectSupplier;
  }

  @Override
  public boolean apply(GameContext context, Targetable target) {
    if (target instanceof Entity) {
      Entity entity = (Entity) target;
      StatusEffect effect = effectSupplier.get(); // <-- создаём новый каждый раз
      entity.addStatusEffect(effect);
      System.out.println("На " + entity.getName() + " наложен дебафф: " + effect.getName());
      return true;
    }
    System.out.println("Дебафф можно применить только на юнита!");
    return false;
  }
}
