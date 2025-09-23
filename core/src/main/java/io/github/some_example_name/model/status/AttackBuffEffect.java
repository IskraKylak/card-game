package io.github.some_example_name.model.status;

import io.github.some_example_name.model.Entity;

public class AttackBuffEffect extends AbstractStatusEffect {
  private final int bonusAttack;

  public AttackBuffEffect(int bonusAttack, int duration) {
    super("Attack Buff", duration, StatusType.BUFF); // ← указываем, что это бафф
    this.bonusAttack = bonusAttack;
  }

  @Override
  public void onApply(Entity entity) {
    entity.setAttack(entity.getAttackPower() + bonusAttack);
  }

  @Override
  public void onRemove(Entity entity) {
    entity.setAttack(entity.getAttackPower() - bonusAttack);
  }
}
