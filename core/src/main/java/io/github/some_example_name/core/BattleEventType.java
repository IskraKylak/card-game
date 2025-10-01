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

  STATUS_APPLIED, STATUS_REMOVED,

  MANA_CHANGED, HAND_CHANGED, DECK_SHUFFLED,

  ENEMY_ACTION_PLANNED,

  // вспомогательные
  ANIMATION_COMPLETED
}
