package io.github.some_example_name.model.payload;

import io.github.some_example_name.model.CombatEntity;
import io.github.some_example_name.model.Targetable;

/**
 * Payload для события атаки.
 * Хранит кто атакует, кого атакует и что делать после завершения анимации.
 */
public class UnitAttackPayload {
  private final CombatEntity attacker;
  private final Targetable target;
  private final Runnable onComplete;

  public UnitAttackPayload(CombatEntity attacker, Targetable target, Runnable onComplete) {
    this.attacker = attacker;
    this.target = target;
    this.onComplete = onComplete;
  }

  public CombatEntity getAttacker() {
    return attacker;
  }

  public Targetable getTarget() {
    return target;
  }

  public Runnable getOnComplete() {
    return onComplete;
  }
}
