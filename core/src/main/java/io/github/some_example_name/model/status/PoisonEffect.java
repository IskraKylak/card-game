package io.github.some_example_name.model.status;

import io.github.some_example_name.model.Entity;

public class PoisonEffect extends AbstractStatusEffect {
  private final int damagePerTurn;

  public PoisonEffect(int duration, int damagePerTurn) {
    super("Poison", duration, StatusType.DEBUFF); // ← указываем, что это дебаф
    this.damagePerTurn = damagePerTurn;
  }

  @Override
  public void onTurnStart(Entity target) {
    target.takeDamage(damagePerTurn);
  }
}
