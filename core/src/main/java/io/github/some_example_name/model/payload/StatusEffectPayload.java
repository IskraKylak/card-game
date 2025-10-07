package io.github.some_example_name.model.payload;

import io.github.some_example_name.model.Entity;
import io.github.some_example_name.model.status.StatusEffect;

public class StatusEffectPayload {
  private final Entity target;
  private final StatusEffect effect;
  private final Runnable onComplete;

  public StatusEffectPayload(Entity target, StatusEffect effect, Runnable onComplete) {
    this.target = target;
    this.effect = effect;
    this.onComplete = onComplete;
  }

  public Entity getTarget() {
    return target;
  }

  public StatusEffect getEffect() {
    return effect;
  }

  public Runnable getOnComplete() {
    return onComplete;
  }
}
