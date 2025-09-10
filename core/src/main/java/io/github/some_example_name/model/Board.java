package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

  private List<Slot> slots;

  public Board(int maxSlots) {
    slots = new ArrayList<>();
    for (int i = 0; i < maxSlots; i++) {
      Slot slot = new Slot(i);
      slots.add(slot);
    }
  }

  public Slot getFirstFreeSlot() {
    for (Slot slot : slots) {
      if (slot.getUnit() == null) {
        return slot;
      }
    }
    return null; // если все слоты заняты
  }

  public List<Slot> getSlots() {
    return slots;
  }

  public Slot getSlot(int index) {
    if (index < 0 || index >= slots.size())
      return null;
    return slots.get(index);
  }

  public void resetBoard() {
    for (Slot slot : slots) {
      slot.removeUnit();
    }
  }

  /** Возвращает true, если есть хотя бы один пустой слот */
  public boolean hasFreeSlot() {
    for (Slot s : slots) {
      if (!s.isOccupied())
        return true;
    }
    return false;
  }

  /**
   * Пытается поставить юнита на первый свободный слот.
   * Возвращает true если успешно, false если свободных слотов нет.
   */
  public boolean addUnit(Unit unit) {
    for (Slot s : slots) {
      if (!s.isOccupied()) {
        s.setUnit(unit);
        return true;
      }
    }
    return false;
  }

  /**
   * Пытается поставить юнита в конкретный слот по индексу (id).
   * Возвращает true если успешно (слот существует и пуст), иначе false.
   */
  public boolean placeUnitAt(int slotId, Unit unit) {
    Slot s = getSlot(slotId);
    if (s != null && !s.isOccupied()) {
      s.setUnit(unit);
      return true;
    }
    return false;
  }
}
