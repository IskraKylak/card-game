package io.github.some_example_name.model.status;

import io.github.some_example_name.model.CombatEntity;
import io.github.some_example_name.model.Entity;

public class PoisonEffect extends AbstractStatusEffect {
  private final int damagePerTurn;
  private final int initialDuration;

  public PoisonEffect(int duration, int amount, TargetingRule targetingRule, int targetCount) {
    super("Poison", duration, StatusType.DEBUFF, targetingRule, targetCount); // ← указываем, что это дебаф
    this.damagePerTurn = amount;
    this.initialDuration = duration;
  }

  @Override
  public void onApply(Entity target) {
    System.out.println("💥 onApply нихуя не наносит" + target);
  }

  @Override
  public void onTurnStart(Entity target) {
    System.out.println("💥 onTurnStart наносит урон " + target + ": " + damagePerTurn);

    target.takeDamage(damagePerTurn);
  }

  @Override
  public PoisonEffect copy() {
    return new PoisonEffect(this.initialDuration, damagePerTurn, getTargetingRule(), getTargetCount());
  }
}
