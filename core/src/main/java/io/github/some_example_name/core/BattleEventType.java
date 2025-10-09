package io.github.some_example_name.core;

/**
 * Типы событий, которые мы будем эмитить внутри движка. По мере роста проекта
 * сюда можно добавлять новые типы.
 */
public enum BattleEventType {
  BATTLE_STARTED, BATTLE_ENDED,

  TURN_STARTED, TURN_ENDED,

  CARD_PLAYED, CARD_DISCARDED,

  UNIT_SUMMONED,

  ENTITY_BUFFED,

  ENTITY_DEBUFFED,

  ENTITY_DAMAGED,

  UNIT_DIED,

  PLAYER_DIED,

  STATUS_APPLIED, STATUS_REMOVED,

  MANA_CHANGED, HAND_CHANGED, DECK_SHUFFLED,

  ENEMY_ACTION_PLANNED,

  UNIT_ATTACK,

  UNIT_ATTACK_LOGIC,

  UNIT_CAST_SPELL,

  STATUS_EFFECT_TRIGGERED,

  STATUS_EFFECT_APPLIED,

  RESTART,

  // вспомогательные
  ANIMATION_COMPLETED
}
