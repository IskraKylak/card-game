// File: core/EnemyAction.java
package io.github.some_example_name.core;

import io.github.some_example_name.model.Unit;

public class EnemyAction {
  public enum Type {
    ATTACK, BUFF, NONE
  }

  private final Type type;
  private final Unit targetUnit; // может быть null — значит цель игрока
  private final int amount; // урон или сила баффа (если нужно)

  public EnemyAction(Type type, Unit targetUnit, int amount) {
    this.type = type;
    this.targetUnit = targetUnit;
    this.amount = amount;
  }

  public Type getType() {
    return type;
  }

  public Unit getTargetUnit() {
    return targetUnit;
  }

  public int getAmount() {
    return amount;
  }

  public static EnemyAction none() {
    return new EnemyAction(Type.NONE, null, 0);
  }
}
