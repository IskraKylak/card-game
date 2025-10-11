package io.github.some_example_name.model.status;

import io.github.some_example_name.model.CombatEntity;
import io.github.some_example_name.model.Entity;

public class RegenEffect extends AbstractStatusEffect {

  private final int healAmount;
  private final int initialDuration;

  public RegenEffect(int duration, int amount, TargetingRule targetingRule, int targetCount) {
    super("Regeneration", duration, StatusType.BUFF, targetingRule, targetCount);
    this.healAmount = amount;
    this.initialDuration = duration;
  }

  @Override
  public void onApply(Entity entity) {
    // Можно сразу немного подлечить при применении, если хочешь
    // if (entity instanceof CombatEntity ce) {
    // ce.heal(healAmount);
    // }
  }

  @Override
  public void onTurnStart(Entity entity) {
    if (duration <= 0)
      return;
    entity.heal(healAmount);

  }

  @Override
  public void onRemove(Entity entity) {
    // при снятии ничего не делаем
  }

  @Override
  public RegenEffect copy() {
    return new RegenEffect(this.initialDuration, this.healAmount, getTargetingRule(), getTargetCount());
  }
}
