package io.github.some_example_name.core;

import java.util.LinkedList;
import java.util.Queue;

public class TurnProcessor {
  private final Queue<Runnable> actions = new LinkedList<>();
  private Runnable onTurnEnd;

  /** Добавить действие в очередь */
  public void addAction(Runnable action) {
    actions.add(action);
  }

  /** Установить коллбек конца хода */
  public void setOnTurnEnd(Runnable onTurnEnd) {
    this.onTurnEnd = onTurnEnd;
  }

  /** Запускает следующее действие в очереди */
  public void runNext() {
    Runnable action = actions.poll();
    if (action != null) {
      action.run(); // выполняем текущее
    } else {
      if (onTurnEnd != null) {
        onTurnEnd.run(); // очередь пуста → конец хода
      }
    }
  }

  /** Полностью очистить очередь */
  public void clear() {
    actions.clear();
  }
}
