package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.some_example_name.core.BattleEvent;
import io.github.some_example_name.core.BattleEventType;
import io.github.some_example_name.model.status.StatusEffect;

public abstract class Entity implements Targetable {
  protected String name;
  protected int health;
  protected int maxHealth;

  private final int id;

  protected List<StatusEffect> activeEffects = new ArrayList<>();

  protected Entity(int id, String name, int health) {
    this.name = name;
    this.health = health;
    this.maxHealth = health;
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  // --- эффекты ---
  public void addStatusEffect(StatusEffect effect) {
    activeEffects.add(effect);
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

  public void setHealth(int health) {
    this.health = Math.max(0, Math.min(health, maxHealth));
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
