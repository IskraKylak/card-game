package io.github.some_example_name.model;

public class Unit extends Entity {
  private final int id;
  private String spriteFolder;

  public Unit(int id, String name, int health, int attack, String spriteFolder) {
    super(name, health, attack);
    this.id = id;
    this.spriteFolder = spriteFolder;
  }

  // --- getters ---
  public int getId() {
    return id;
  }

  @Override
  public String getSpriteFolder() {
    return spriteFolder;
  }
}
