package io.github.some_example_name.model;

public class Unit {
  private final int id;
  private final String name;
  private int health;
  private int maxHealth;
  private int attack;

  public Unit(int id, String name, int health, int attack) {
    this.id = id;
    this.name = name;
    this.health = health;
    this.maxHealth = health; // устанавливаем maxHealth равным стартовому здоровью
    this.attack = attack;
  }

  // --- getters ---
  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getHealth() {
    return health;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public int getAttack() {
    return attack;
  }

  // --- действия с юнитом ---
  public void takeDamage(int damage) {
    this.health = Math.max(0, health - damage);
  }

  public void heal(int amount) {
    this.health = Math.min(maxHealth, health + amount);
  }

  public boolean isAlive() {
    return health > 0;
  }
}
