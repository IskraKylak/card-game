package io.github.some_example_name.model.status;

import io.github.some_example_name.model.CombatEntity;
import io.github.some_example_name.model.Entity;

public class AttackBuffEffect extends AbstractStatusEffect {
  private final int bonusAttack;
  private final int initialDuration;

  public AttackBuffEffect(int duration, int amount, TargetingRule targetingRule, int targetCount) {
    super("Attack Buff", duration, StatusType.BUFF, targetingRule, targetCount); // ← указываем, что это бафф
    this.bonusAttack = amount;
    this.initialDuration = duration;
  }

  @Override
  public void onApply(Entity entity) {
    if (entity instanceof CombatEntity ce) {
      ce.setAttack(ce.getAttackPower() + bonusAttack);
    }
  }

  @Override
  public void onTurnStart(Entity target) {
    if (duration <= 0)
      return; // мгновенно не применяем
    if (target instanceof CombatEntity ce) {
      ce.setAttack(ce.getAttackPower() + bonusAttack);
    }
  }

  @Override
  public void onRemove(Entity entity) {
    if (entity instanceof CombatEntity ce) {
      // ce.setAttack(ce.getAttackPower() - bonusAttack);
    }
  }

  @Override
  public AttackBuffEffect copy() {
    return new AttackBuffEffect(this.initialDuration, this.bonusAttack, getTargetingRule(), getTargetCount());
  }
}
