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

  /**
   * Вспомогательный метод: добавляет действие по типу (ATTACK или CAST_SPELL)
   */
  private void addActionForType(ActionPlan.ActionType type,
      Player player,
      Enemy enemy,
      List<StatusEffect> availableSpells,
      List<Targetable> alliedTargets,
      List<Targetable> enemyTargets,
      Random rnd) {
    switch (type) {
      case ATTACK:
        // Выбираем цель для атаки
        Targetable target = chooseAttackTarget(player, enemy, alliedTargets, enemyTargets, rnd);
        // Добавляем действие атаки в план
        actionPlan.addAction(new ActionPlan.Action(ActionPlan.ActionType.ATTACK, target, attackPower, null));
        break;
      case CAST_SPELL:
        if (!availableSpells.isEmpty()) {
          // Выбираем случайное заклинание
          int idx = rnd.nextInt(availableSpells.size());
          StatusEffect spell = availableSpells.get(idx);
          // Добавляем его в план с правильной целью
          addActionForSpell(spell, player, enemy, alliedTargets, enemyTargets, rnd);
        }
        break;
      default:
        break;
    }
  }

  /**
   * Добавляет заклинание в план действий с корректной целью
   */
  private void addActionForSpell(StatusEffect spell,
      Player player,
      Enemy enemy,
      List<Targetable> alliedTargets,
      List<Targetable> enemyTargets,
      Random rnd) {
    // Выбираем цель для заклинания
    Targetable target = chooseSpellTarget(spell, player, enemy, alliedTargets, enemyTargets, rnd);
    // Добавляем заклинание в план действий
    actionPlan.addAction(new ActionPlan.Action(ActionPlan.ActionType.CAST_SPELL, target, 0, spell));
  }

  /**
   * Метод выбора цели для заклинания
   */
  private Targetable chooseSpellTarget(StatusEffect spell,
      Player player,
      Enemy enemy,
      List<Targetable> alliedTargets,
      List<Targetable> enemyTargets,
      Random rnd) {
    TargetingRule rule = TargetingRule.NONE;

    // Если заклинание поддерживает TargetingRule
    if (spell instanceof TargetableStatusEffect tse) {
      rule = tse.getTargetingRule();
    }

    switch (rule) {
      case SELF:
        return this; // на себя
      case PLAYER:
        return player; // на игрока
      case ALLY:
        return alliedTargets.isEmpty() ? this : alliedTargets.get(rnd.nextInt(alliedTargets.size()));
      case ENEMY:
        return enemyTargets.isEmpty() ? enemy : enemyTargets.get(rnd.nextInt(enemyTargets.size()));
      case RANDOM_ALLY:
        return alliedTargets.isEmpty() ? this : alliedTargets.get(rnd.nextInt(alliedTargets.size()));
      case RANDOM_ENEMY:
        return enemyTargets.isEmpty() ? enemy : enemyTargets.get(rnd.nextInt(enemyTargets.size()));
      case NONE:
      default:
        return this; // по умолчанию на себя
    }
  }

  /**
   * Метод выбора цели для обычной атаки
   */
  private Targetable chooseAttackTarget(Player player,
      Enemy enemy,
      List<Targetable> alliedTargets,
      List<Targetable> enemyTargets,
      Random rnd) {
    if (this instanceof Unit) {
      // Юнит игрока атакует врага или, если врага нет, самого enemy
      if (!enemyTargets.isEmpty()) {
        return enemyTargets.get(rnd.nextInt(enemyTargets.size()));
      } else {
        return enemy; // атакуем врага
      }
    } else if (this instanceof Enemy) {
      // Враг атакует юнитов игрока, если их нет — игрока
      if (!enemyTargets.isEmpty()) {
        return enemyTargets.get(rnd.nextInt(enemyTargets.size()));
      } else {
        return player; // атакуем игрока
      }
    } else {
      return this; // на всякий случай на себя
    }
  }

  /**
   * Планирует действия юнита или врага на текущий ход.
   * Использует attackPower и spells, а также maxActionsPerTurn.
   */
  public void planActions(Player player, Enemy enemy) {
    System.out.println("1️⃣ planActions вызван для: " + getName());

    // 1️⃣ Очищаем предыдущий план действий
    actionPlan.clear();
    System.out.println("2️⃣ План действий очищен");

    // 2️⃣ Определяем количество действий, которое может выполнить юнит за ход
    int actionsCount = Math.min(getMaxActionsPerTurn(), 3); // максимум 3
    System.out.println("3️⃣ actionsCount = " + actionsCount);

    // 3️⃣ Создаем список доступных заклинаний
    List<StatusEffect> availableSpells = new ArrayList<>(spells);
    System.out.println("4️⃣ Доступные заклинания: " + availableSpells.size());

    // 4️⃣ Формируем списки союзников и врагов для ИИ
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
      alliedTargets = List.of();
      enemyTargets = player.getSlots().stream()
          .map(Slot::getUnit)
          .filter(u -> u != null)
          .map(u -> (Targetable) u)
          .toList();
    } else {
      alliedTargets = List.of();
      enemyTargets = List.of();
    }

    System.out.println("5️⃣ alliedTargets = " + alliedTargets.size() + ", enemyTargets = " + enemyTargets.size());

    Random rnd = new Random();

    // 5️⃣ Формируем план действий в зависимости от actionsCount
    for (int i = 0; i < actionsCount; i++) {
      List<ActionPlan.ActionType> possibleActions = new ArrayList<>();
      if (attackPower > 0)
        possibleActions.add(ActionPlan.ActionType.ATTACK);
      if (!availableSpells.isEmpty())
        possibleActions.add(ActionPlan.ActionType.CAST_SPELL);

      System.out.println("6️⃣ possibleActions на шаге " + i + ": " + possibleActions);

      if (!possibleActions.isEmpty()) {
        ActionPlan.ActionType chosenAction = possibleActions.get(rnd.nextInt(possibleActions.size()));
        System.out.println("7️⃣ Выбрано действие: " + chosenAction);

        if (chosenAction == ActionPlan.ActionType.ATTACK) {
          addActionForType(ActionPlan.ActionType.ATTACK, player, enemy, availableSpells, alliedTargets, enemyTargets,
              rnd);
        } else {
          // Для заклинаний выбираем случайные, без повторов
          List<StatusEffect> spellsCopy = new ArrayList<>(availableSpells);
          if (!spellsCopy.isEmpty()) {
            int idx = rnd.nextInt(spellsCopy.size());
            StatusEffect spell = spellsCopy.remove(idx);
            addActionForSpell(spell, player, enemy, alliedTargets, enemyTargets, rnd);
            System.out.println("8️⃣ Добавлено заклинание: " + spell.getClass().getSimpleName());
          }
        }

        // 🔹 Логирование: сразу после добавления действия в план
        if (!actionPlan.getActions().isEmpty()) {
          ActionPlan.Action lastAction = actionPlan.getActions().get(actionPlan.getActions().size() - 1);
          Targetable target = lastAction.getTarget();
          String targetName = (target instanceof Entity e) ? e.getName() : "неизвестная цель";
          System.out.println("9️⃣ " + getName() + " запланировал " + lastAction.getType() + " на " + targetName);
        } else {
          System.out.println("⚠️ Нет действий в плане после выбора");
        }
      } else {
        System.out.println("⚠️ Нет возможных действий на шаге " + i);
      }
    }
  }

  /**
   * Выполняет все действия, запланированные в actionPlan.
   * player — объект игрока
   * enemy — враг
   */
  public void executeActionPlan(Player player, Enemy enemy) {
    for (ActionPlan.Action action : actionPlan.getActions()) {
      switch (action.getType()) {
        case ATTACK -> {
          Targetable target = action.getTarget();
          performAttack(target);
        }
        case CAST_SPELL -> {
          Targetable target = action.getTarget();
          StatusEffect effect = action.getEffect();

          if (effect != null && target instanceof Entity entityTarget) {
            // применяем эффект при наложении
            effect.onApply(entityTarget);

            // если у тебя у Entity есть список активных эффектов — добавляем туда
            entityTarget.addStatusEffect(effect);
          }
        }
      }
    }

    // После выполнения очищаем план
    actionPlan.clear();
  }

  /**
   * Выполняет атаку по цели.
   */
  public void performAttack(Targetable target) {
    if (target instanceof Unit unit) {
      unit.takeDamage(attackPower);
    } else if (target instanceof Player player) {
      player.takeDamage(attackPower);
    } else if (target instanceof Enemy enemy) {
      enemy.takeDamage(attackPower);
    }
  }

  public ActionPlan getActionPlan() {
    return actionPlan;
  }

}
