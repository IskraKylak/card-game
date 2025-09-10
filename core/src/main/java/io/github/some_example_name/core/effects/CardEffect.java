package io.github.some_example_name.core.effects;

import io.github.some_example_name.model.Slot;
import io.github.some_example_name.core.GameContext;

public interface CardEffect {
  void apply(GameContext context, Slot targetSlot);
}
