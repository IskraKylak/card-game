package io.github.some_example_name.model.status;

import io.github.some_example_name.model.CombatEntity;
import io.github.some_example_name.model.Entity;

public class AttackBuffEffect extends AbstractStatusEffect {
  private final int bonusAttack;

  public AttackBuffEffect(int bonusAttack, int duration, TargetingRule targetingRule) {
    super("Attack Buff", duration, StatusType.BUFF, targetingRule); // ← указываем, что это бафф
    this.bonusAttack = bonusAttack;
  }

  @Override
  public void onApply(Entity entity) {
    if (duration <= 0)
      return; // мгновенно не применяем
    if (entity instanceof CombatEntity ce) {
      ce.setAttack(ce.getAttackPower() + bonusAttack);
    }
  }

  @Override
  public void onTurnStart(Entity target) {
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
}
