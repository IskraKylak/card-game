package io.github.some_example_name.model.payload;

import io.github.some_example_name.model.CombatEntity;
import io.github.some_example_name.model.Targetable;
import io.github.some_example_name.model.status.StatusEffect;

public class UnitSpellPayload {
  private final CombatEntity caster;
  private final Targetable target;
  private final StatusEffect effect;
  private final Runnable onComplete;

  public UnitSpellPayload(CombatEntity caster, Targetable target, StatusEffect effect, Runnable onComplete) {
    this.caster = caster;
    this.target = target;
    this.effect = effect;
    this.onComplete = onComplete;
  }

  public CombatEntity getCaster() {
    return caster;
  }

  public Targetable getTarget() {
    return target;
  }

  public StatusEffect getEffect() {
    return effect;
  }

  public Runnable getOnComplete() {
    return onComplete;
  }
}
