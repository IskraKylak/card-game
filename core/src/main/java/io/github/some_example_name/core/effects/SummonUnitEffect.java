package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Slot;
import io.github.some_example_name.model.Targetable;
import io.github.some_example_name.model.Unit;
import io.github.some_example_name.model.data.DataUnits;

public class SummonUnitEffect implements CardEffect {
  private final int unitId;

  public SummonUnitEffect(int unitId) {
    this.unitId = unitId;
  }

  @Override
  public boolean apply(GameContext context, Targetable target) {
    if (!(target instanceof Slot)) {
      System.out.println("Суммон юнита можно применять только на слот!");
      return false;
    }

    Slot slot = (Slot) target; // приведение безопасно
    if (slot.isOccupied()) {
      System.out.println("Слот " + slot.getId() + " занят, нельзя призвать юнита.");
      return false;
    }

    Unit unit = DataUnits.getUnitById(unitId);
    slot.setUnit(unit);
    return true;
  }
}
