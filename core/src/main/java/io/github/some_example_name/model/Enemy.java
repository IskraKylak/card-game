package io.github.some_example_name.model;

import java.util.Random;

public class Enemy {
  private String name;
  private int health;
  private int maxHealth; // добавим максимум, чтобы нельзя было перебить

  private int attackPower;
  private String spriteFolder;

  public Enemy(String name, int health, int attackPower, String spriteFolder) {
    this.name = name;
    this.health = health;
    this.maxHealth = health; // максимальное здоровье равно стартовому
    this.attackPower = attackPower;
    this.spriteFolder = spriteFolder;
  }

  public String getSpriteFolder() {
    return spriteFolder;
  }

  // --- getters ---
  public String getName() {
    return name;
  }

  public int getHealth() {
    return health;
  }

  public int getAttackPower() {
    return attackPower;
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

  // --- model actions ---
  public void takeDamage(int dmg) {
    health = Math.max(0, health - dmg);
  }

  public boolean isAlive() {
    return health > 0;
  }

  /** Лечим врага на указанное количество HP */
  public void heal(int amount) {
    health = Math.min(maxHealth, health + amount);
  }
}
