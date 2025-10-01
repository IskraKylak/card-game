package io.github.some_example_name.model.status;

import io.github.some_example_name.model.Entity;

public abstract class AbstractStatusEffect implements StatusEffect {
  protected final String name;
  protected int duration;
  protected final StatusType type;
  private final TargetingRule targetingRule;

  protected AbstractStatusEffect(String name, int duration, StatusType type, TargetingRule targetingRule) {
    this.name = name;
    this.duration = duration;
    this.type = type;
    this.targetingRule = targetingRule;
  }

  @Override
  public TargetingRule getTargetingRule() {
    return targetingRule; // возвращаем правило, заданное при создании эффекта
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getDuration() {
    return duration;
  }

  @Override
  public StatusType getType() {
    return type;
  }

  @Override
  public boolean isNegative() {
    return type == StatusType.DEBUFF;
  }

  @Override
  public void onApply(Entity target) {
  }

  @Override
  public void onTurnStart(Entity target) {
  }

  @Override
  public void onRemove(Entity target) {
  }

  @Override
  public boolean tick(Entity target) {
    System.out.println("Duration: " + duration + " Target: " + target);
    duration--;
    if (duration <= 0) {
      onRemove(target);
      return false;
    }

    return true;
  }
}
