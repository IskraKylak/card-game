package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Targetable;

public class NoEffect implements CardEffect {
  @Override
  public boolean apply(GameContext context, Targetable target) {
    return false; // просто "пустой эффект"
  }
}
