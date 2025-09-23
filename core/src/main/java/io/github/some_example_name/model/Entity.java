package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import io.github.some_example_name.model.status.StatusEffect;

public abstract class Entity implements Targetable {
  protected String name;
  protected int health;
  protected int maxHealth;
  private int attackPower;

  protected List<StatusEffect> activeEffects = new ArrayList<>();

  public Entity(String name, int health, int attackPower) {
    this.name = name;
    this.health = health;
    this.maxHealth = health;
    this.attackPower = attackPower;
  }

  public int getAttackPower() {
    return attackPower;
  }

  // --- эффекты ---
  public void addStatusEffect(StatusEffect effect) {
    effect.onApply(this);
    activeEffects.add(effect);
  }

  public int setAttack(int attackPower) {
    this.attackPower = Math.max(0, attackPower); // ⚡ не меньше 0
    return this.attackPower;
  }

  public List<StatusEffect> getStatusEffects() {
    return activeEffects;
  }

  // --- здоровье ---
  public int getHealth() {
    return health;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public void setHealth(int hp) {
    this.health = Math.max(0, Math.min(hp, maxHealth));
  }

  public void takeDamage(int dmg) {
    this.health = Math.max(0, health - dmg);
  }

  public void heal(int amount) {
    this.health = Math.min(maxHealth, health + amount);
  }

  public boolean isAlive() {
    return health > 0;
  }

  // Внутри Entity
  public String getName() {
    return name;
  }

  public void removeStatusEffect(StatusEffect effect) {
    effect.onRemove(this);
    activeEffects.remove(effect);
  }

  // --- абстрактный метод для спрайтов ---
  public abstract String getSpriteFolder();
}
