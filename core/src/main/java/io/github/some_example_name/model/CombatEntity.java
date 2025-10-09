package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.some_example_name.model.status.StatusEffect;
import io.github.some_example_name.model.status.TargetableStatusEffect;
import io.github.some_example_name.model.status.TargetingRule;

public abstract class CombatEntity extends Entity {

  private int attackPower;
  private int maxActionsPerTurn = 1;
  String description;
  protected ActionPlan actionPlan = new ActionPlan();
  protected List<StatusEffect> spells;

  protected CombatEntity(int id, String name, String description, int health, int attackPower,
      int maxActionsPerTurn, List<StatusEffect> spells) {
    super(id, name, health);
    this.description = description;
    this.attackPower = attackPower;
    this.maxActionsPerTurn = maxActionsPerTurn;
    this.spells = spells != null ? spells : List.of();
  }

  // --- Геттеры ---
  public List<StatusEffect> getSpells() {
    return spells;
  }

  public String getDescription() {
    return description;
  }

  public int getMaxActionsPerTurn() {
    return maxActionsPerTurn;
  }

  public int getAttackPower() {
    return attackPower;
  }

  public int setAttack(int attackPower) {
    this.attackPower = Math.max(0, attackPower);
    return this.attackPower;
  }

  // Добавление действия
  private void addActionForType(ActionPlan.ActionType type,
      Player player,
      Enemy enemy,
      List<StatusEffect> availableSpells,
      List<Targetable> alliedTargets,
      List<Targetable> enemyTargets,
      Random rnd) {
    switch (type) {
      case ATTACK -> {
        Targetable target = chooseAttackTarget(player, enemy, alliedTargets, enemyTargets, rnd);
        actionPlan.addAction(new ActionPlan.Action(ActionPlan.ActionType.ATTACK, target, attackPower, null));
      }
      case CAST_SPELL -> {
        if (!availableSpells.isEmpty()) {
          int idx = rnd.nextInt(availableSpells.size());
          StatusEffect spell = availableSpells.get(idx);
          addActionForSpell(spell, player, enemy, alliedTargets, enemyTargets, rnd);
        }
      }
    }
  }

  private void addActionForSpell(StatusEffect spell,
      Player player,
      Enemy enemy,
      List<Targetable> alliedTargets,
      List<Targetable> enemyTargets,
      Random rnd) {
    Targetable target = chooseSpellTarget(spell, player, enemy, alliedTargets, enemyTargets, rnd);
    actionPlan.addAction(new ActionPlan.Action(ActionPlan.ActionType.CAST_SPELL, target, 0, spell));
  }

  /**
   * Новый метод выбора цели для заклинаний.
   * Работает корректно для юнитов и врагов (в том числе если враг один).
   */
  private Targetable chooseSpellTarget(StatusEffect spell,
      Player player,
      Enemy enemy,
      List<Targetable> alliedTargets,
      List<Targetable> enemyTargets,
      Random rnd) {

    TargetingRule rule = TargetingRule.NONE;
    if (spell != null) {
      rule = spell.getTargetingRule();
    }

    boolean isEnemyCaster = this instanceof Enemy;
    boolean isUnitCaster = this instanceof Unit;

    System.out.println("🎯 CHOOSE SPELL TARGET");
    System.out
        .println("Кастер: " + getName() + " (" + (isEnemyCaster ? "ENEMY" : isUnitCaster ? "UNIT" : "OTHER") + ")");
    System.out.println("Правило таргетинга: " + rule);
    System.out.println("Allied targets: " + alliedTargets.size() + ", Enemy targets: " + enemyTargets.size());

    Targetable chosenTarget;

    switch (rule) {
      case SELF -> {
        chosenTarget = this;
        System.out.println("Выбран SELF: " + ((Entity) chosenTarget).getName());
      }
      case PLAYER -> {
        chosenTarget = player;
        System.out.println("Выбран PLAYER: " + player.getName());
      }
      case ENEMY -> {
        if (isUnitCaster) {
          if (!enemyTargets.isEmpty()) {
            chosenTarget = enemyTargets.get(rnd.nextInt(enemyTargets.size()));
            System.out.println("Юнит выбирает ENEMY: " + ((Entity) chosenTarget).getName());
          } else {
            chosenTarget = enemy;
            System.out.println("Fallback на ENEMY: " + enemy.getName());
          }
        } else {
          if (!enemyTargets.isEmpty()) {
            chosenTarget = enemyTargets.get(rnd.nextInt(enemyTargets.size()));
            System.out.println("Враг выбирает ENEMY: " + ((Entity) chosenTarget).getName());
          } else {
            chosenTarget = player;
            System.out.println("Fallback на PLAYER: " + player.getName());
          }
        }
      }
      case ALLY -> {
        if (isUnitCaster) {
          if (!alliedTargets.isEmpty()) {
            chosenTarget = alliedTargets.get(rnd.nextInt(alliedTargets.size()));
            System.out.println("Юнит выбирает ALLY: " + ((Entity) chosenTarget).getName());
          } else {
            chosenTarget = this;
            System.out.println("Fallback на SELF: " + getName());
          }
        } else {
          chosenTarget = this;
          System.out.println("Враг выбирает ALLY (только SELF): " + getName());
        }
      }
      case ALL_ALLY -> {
        if (isUnitCaster) {
          List<Targetable> possibleTargets = new ArrayList<>(alliedTargets);
          possibleTargets.add(this);
          possibleTargets.add(player);
          chosenTarget = possibleTargets.get(rnd.nextInt(possibleTargets.size()));
          System.out.println("Юнит выбирает ALL_ALLY: " + ((Entity) chosenTarget).getName());
        } else {
          chosenTarget = this;
          System.out.println("Враг выбирает ALL_ALLY (только SELF): " + getName());
        }
      }
      case RANDOM_ALLY -> {
        if (!alliedTargets.isEmpty()) {
          chosenTarget = alliedTargets.get(rnd.nextInt(alliedTargets.size()));
          System.out.println("Выбран RANDOM_ALLY: " + ((Entity) chosenTarget).getName());
        } else {
          chosenTarget = this;
          System.out.println("Fallback на SELF: " + getName());
        }
      }
      case RANDOM_ENEMY -> {
        if (!enemyTargets.isEmpty()) {
          chosenTarget = enemyTargets.get(rnd.nextInt(enemyTargets.size()));
          System.out.println("Выбран RANDOM_ENEMY: " + ((Entity) chosenTarget).getName());
        } else {
          chosenTarget = isEnemyCaster ? player : enemy;
          System.out.println(
              "Fallback на " + (isEnemyCaster ? "PLAYER" : "ENEMY") + ": " + ((Entity) chosenTarget).getName());
        }
      }
      case NONE -> {
        chosenTarget = player;
        System.out.println("DEFAULT: SELF " + getName());
      }

      default -> {
        chosenTarget = player;
        System.out.println("DEFAULT: SELF " + getName());
      }
    }

    return chosenTarget;
  }

  // Выбор цели для обычной атаки
  private Targetable chooseAttackTarget(Player player,
      Enemy enemy,
      List<Targetable> alliedTargets,
      List<Targetable> enemyTargets,
      Random rnd) {
    if (this instanceof Unit) {
      if (!enemyTargets.isEmpty()) {
        return enemyTargets.get(rnd.nextInt(enemyTargets.size()));
      } else {
        return enemy;
      }
    } else if (this instanceof Enemy) {
      if (!enemyTargets.isEmpty()) {
        return enemyTargets.get(rnd.nextInt(enemyTargets.size()));
      } else {
        return player;
      }
    } else {
      return this;
    }
  }

  // Планирование действий
  public void planActions(Player player, Enemy enemy) {
    System.out.println("1️⃣ planActions вызван для: " + getName());
    actionPlan.clear();

    int actionsCount = Math.min(getMaxActionsPerTurn(), 3);
    List<StatusEffect> availableSpells = new ArrayList<>(spells);

    List<Targetable> alliedTargets;
    List<Targetable> enemyTargets;

    if (this instanceof Unit unit) {
      alliedTargets = player.getSlots().stream()
          .map(Slot::getUnit)
          .filter(u -> u != null && u != unit)
          .map(u -> (Targetable) u)
          .toList();
      enemyTargets = List.of((Targetable) enemy);
    } else if (this instanceof Enemy) {
      // ❗ У врага нет союзников, только он сам
      alliedTargets = List.of(this);

      // враг видит вражескими все юниты игрока и самого игрока
      enemyTargets = new ArrayList<>();
      enemyTargets.add(player);
      enemyTargets.addAll(
          player.getSlots().stream()
              .map(Slot::getUnit)
              .filter(u -> u != null)
              .map(u -> (Targetable) u)
              .toList());
    } else {
      alliedTargets = List.of();
      enemyTargets = List.of();
    }

    Random rnd = new Random();

    for (int i = 0; i < actionsCount; i++) {
      List<ActionPlan.ActionType> possibleActions = new ArrayList<>();
      if (attackPower > 0)
        possibleActions.add(ActionPlan.ActionType.ATTACK);
      if (!availableSpells.isEmpty())
        possibleActions.add(ActionPlan.ActionType.CAST_SPELL);

      if (!possibleActions.isEmpty()) {
        ActionPlan.ActionType chosenAction = possibleActions.get(rnd.nextInt(possibleActions.size()));

        if (chosenAction == ActionPlan.ActionType.ATTACK) {
          addActionForType(ActionPlan.ActionType.ATTACK, player, enemy, availableSpells, alliedTargets, enemyTargets,
              rnd);
        } else {
          List<StatusEffect> spellsCopy = new ArrayList<>(availableSpells);
          if (!spellsCopy.isEmpty()) {
            int idx = rnd.nextInt(spellsCopy.size());
            StatusEffect spell = spellsCopy.remove(idx);

            StatusEffect newSpell = spell.copy();

            addActionForSpell(newSpell, player, enemy, alliedTargets, enemyTargets, rnd);
          }
        }
      }
    }
  }

  // Выполняет атаку по цели
  public void performAttack(Targetable target) {
    if (target instanceof Unit unit) {
      System.out.println("💥 performAttack наносит урон UNIT: " + attackPower);
      unit.takeDamage(attackPower);
    } else if (target instanceof Player player) {
      System.out.println("💥 performAttack наносит урон PLAYER: " + attackPower);
      player.takeDamage(attackPower);
    } else if (target instanceof Enemy enemy) {
      enemy.takeDamage(attackPower);
    }
  }

  public ActionPlan getActionPlan() {
    return actionPlan;
  }
}
