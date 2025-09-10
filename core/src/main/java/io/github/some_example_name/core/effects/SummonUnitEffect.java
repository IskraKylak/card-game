package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.*;
import io.github.some_example_name.model.data.DataUnits;

public class SummonUnitEffect implements CardEffect {
  private int unitId;

  public SummonUnitEffect(int unitId) {
    this.unitId = unitId;
  }

  @Override
  public void apply(GameContext context, Slot targetSlot) {
    Unit unit = DataUnits.getUnitById(unitId);

    if (targetSlot != null && !targetSlot.isOccupied()) {
      targetSlot.setUnit(unit);
      System.out.println(unit.getName() + " призван в слот " + targetSlot.getId());
    } else {
      System.out.println("Нет свободного слота для призыва " + unit.getName());
    }
  }
}
