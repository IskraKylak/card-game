package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.model.status.StatusEffect;

/**
 * ActionPlan хранит список действий, которые Entity (юнит или враг) планирует
 * выполнить за ход.
 */
public class ActionPlan {

  /**
   * Тип действия
   */
  public enum ActionType {
    ATTACK, // обычная атака
    CAST_SPELL, // применение заклинания/эффекта
    BUFF, // наложение баффа на себя или союзников
    DEBUFF, // наложение дебаффа на противников
    NONE // пустое действие
  }

  /**
   * Описание одного действия
   */
  public static class Action {
    private final ActionType type; // тип действия
    private final Targetable target; // цель действия (может быть Enemy, Unit, Player)
    private final int amount; // количество урона/лечения/усиления
    private final StatusEffect effect; // если это заклинание/эффект

    public Action(ActionType type, Targetable target, int amount, StatusEffect effect) {
      this.type = type;
      this.target = target;
      this.amount = amount;
      this.effect = effect;
    }

    public ActionType getType() {
      return type;
    }

    public Targetable getTarget() {
      return target;
    }

    public int getAmount() {
      return amount;
    }

    public StatusEffect getEffect() {
      return effect;
    }
  }

  private final List<Action> actions = new ArrayList<>();

  /**
   * Добавить действие в план
   */
  public void addAction(Action action) {
    actions.add(action);
  }

  /**
   * Получить все запланированные действия
   */
  public List<Action> getActions() {
    return actions;
  }

  /**
   * Очистить план (начало нового хода)
   */
  public void clear() {
    actions.clear();
  }
}
