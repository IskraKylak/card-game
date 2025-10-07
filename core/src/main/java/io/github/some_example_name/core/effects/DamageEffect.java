package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.BattleEvent;
import io.github.some_example_name.core.BattleEventType;
import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Targetable;
import io.github.some_example_name.model.Slot;
import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.Unit;

public class DamageEffect implements CardEffect {
  private final int damage;

  public DamageEffect(int damage) {
    this.damage = damage;
  }

  @Override
  public boolean apply(GameContext context, Targetable target) {
    if (target instanceof Slot) {
      Slot slot = (Slot) target; // безопасное приведение
      Unit u = slot.getUnit();
      if (u != null) {
        u.takeDamage(damage);
        System.out.println(u.getName() + " получает " + damage + " урона!");

        context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED, u));
        return true;
      } else {
        System.out.println("Слот пуст, урон не применён.");
        return false;
      }
    } else if (target instanceof Enemy) {
      Enemy enemy = (Enemy) target; // безопасное приведение
      enemy.takeDamage(damage);
      context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED, enemy));
      System.out.println(enemy.getName() + " получает " + damage + " урона!");
      return true;
    }

    return false;
  }
}
