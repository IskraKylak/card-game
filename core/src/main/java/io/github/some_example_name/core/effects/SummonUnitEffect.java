package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.BattleEvent;
import io.github.some_example_name.core.BattleEventType;
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
    if (!(target instanceof Slot slot)) {
      System.out.println("Суммон юнита можно применять только на слот!");
      return false;
    }

    if (slot.isOccupied()) {
      System.out.println("Слот " + slot.getId() + " занят, нельзя призвать юнита.");
      return false;
    }

    // Создаём новый экземпляр юнита
    Unit unit = DataUnits.getUnitById(unitId);

    // Эмитим событие о призыве юнита
    context.getEventBus().emit(
        BattleEvent.of(
            BattleEventType.UNIT_SUMMONED,
            new UnitSummonPayload(unit, slot)));

    return true;
  }

  // Payload события для передачи юнита и слота
  public static class UnitSummonPayload {
    public final Unit unit;
    public final Slot slot;

    public UnitSummonPayload(Unit unit, Slot slot) {
      this.unit = unit;
      this.slot = slot;
    }
  }
}
