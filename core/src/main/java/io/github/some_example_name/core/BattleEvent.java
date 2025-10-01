package io.github.some_example_name.core;

/**
 * Простое событие: тип + произвольный payload. Payload может быть любой объект:
 * Unit, EnemyAction, Card, Integer и т.п. Мы используем Object payload для
 * простоты; при желании можно сделать generic.
 */
public class BattleEvent {
  private final BattleEventType type;
  private final Object payload;

  public BattleEvent(BattleEventType type, Object payload) {
    this.type = type;
    this.payload = payload;
  }

  public BattleEventType getType() {
    return type;
  }

  public Object getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return "BattleEvent{type=" + type + ", payload=" + payload + "}";
  }

  // Удобная фабрика
  public static BattleEvent of(BattleEventType type, Object payload) {
    return new BattleEvent(type, payload);
  }
}
