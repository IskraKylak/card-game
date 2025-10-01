package io.github.some_example_name.model;

import java.util.List;

import io.github.some_example_name.model.status.StatusEffect;

public abstract class CombatEntity extends Entity {

  private int attackPower;
  private int maxActionsPerTurn = 1;
  protected ActionPlan actionPlan = new ActionPlan();
  // Список заклинаний назначается при создании
  protected List<StatusEffect> spells;

  protected CombatEntity(int id, String name, int health, int attackPower,
      int maxActionsPerTurn, List<StatusEffect> spells) {
    super(id, name, health);

    this.attackPower = attackPower;
    this.maxActionsPerTurn = maxActionsPerTurn;
    this.spells = spells != null ? spells : List.of(); // если null → пустой список
  }

  // --- Доступ к заклинаниям ---
  public List<StatusEffect> getSpells() {
    return spells;
  }

  public int getMaxActionsPerTurn() {
    return this.maxActionsPerTurn;
  }

  public int getAttackPower() {
    return this.attackPower;
  }

  public int setAttack(int attackPower) {
    this.attackPower = Math.max(0, attackPower); // ⚡ не меньше 0
    return this.attackPower;
  }

  public void planActions(Player player, Enemy enemy) {
    actionPlan.clear();

    // Количество действий определяется у сущности
    int actionsCount = getMaxActionsPerTurn();
  }
}
