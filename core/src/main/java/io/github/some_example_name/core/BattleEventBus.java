package io.github.some_example_name.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BattleEventBus {

  private final Map<BattleEventType, List<Consumer<BattleEvent>>> listeners = new HashMap<>();

  public void on(BattleEventType type, Consumer<BattleEvent> listener) {
    listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
  }

  public void emit(BattleEvent event) {
    List<Consumer<BattleEvent>> eventListeners = listeners.get(event.getType());
    if (eventListeners != null) {
      for (Consumer<BattleEvent> listener : eventListeners) {
        listener.accept(event);
      }
    }
  }
}
