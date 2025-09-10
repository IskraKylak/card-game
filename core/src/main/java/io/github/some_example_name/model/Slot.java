package io.github.some_example_name.model;

public class Slot {
  private final int id; // уникальный идентификатор слота
  private Unit unit; // null если пустой

  public Slot(int id) {
    this.id = id;
    this.unit = null;
  }

  public int getId() {
    return id;
  }

  public boolean isOccupied() {
    return unit != null;
  }

  public Unit getUnit() {
    return unit;
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  public void removeUnit() {
    this.unit = null;
  }
}
