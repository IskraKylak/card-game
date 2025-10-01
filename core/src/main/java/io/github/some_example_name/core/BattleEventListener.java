package io.github.some_example_name.core;

/**
 * Интерфейс слушателя событий шины.
 */
public interface BattleEventListener {
  void onBattleEvent(BattleEvent event);
}
