package io.github.some_example_name.model.status;

import io.github.some_example_name.model.Entity;

public class PoisonEffect extends AbstractStatusEffect {
  private final int damagePerTurn;

  public PoisonEffect(int duration, int damagePerTurn, TargetingRule targetingRule) {
    super("Poison", duration, StatusType.DEBUFF, targetingRule); // ← указываем, что это дебаф
    this.damagePerTurn = damagePerTurn;
  }

  @Override
  public void onTurnStart(Entity target) {
    target.takeDamage(damagePerTurn);
  }

  @Override
  public TargetingRule getTargetingRule() {
    // ИИ будет выбирать случайного врага для этого эффекта
    return TargetingRule.RANDOM_ENEMY;
  }
}
