package io.github.some_example_name.model;

import java.util.Random;

public class Enemy extends Entity {
  private String spriteFolder;

  public Enemy(String name, int health, int attackPower, String spriteFolder) {
    super(name, health, attackPower);
    this.spriteFolder = spriteFolder;
  }

  public String takeTurn() {
    int choice = new Random().nextInt(3);
    switch (choice) {
      case 0:
        return "ATTACK";
      case 1:
        return "BUFF";
      default:
        return "ATTACK";
    }
  }

  @Override
  public String getSpriteFolder() {
    return spriteFolder;
  }
}
