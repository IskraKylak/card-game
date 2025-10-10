package io.github.some_example_name.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.some_example_name.core.effects.CardEffect;
import io.github.some_example_name.model.*;
import io.github.some_example_name.model.payload.StatusEffectPayload;
import io.github.some_example_name.model.payload.UnitAttackPayload;
import io.github.some_example_name.model.payload.UnitSpellPayload;
import io.github.some_example_name.model.status.StatusEffect;

public class GameEngine {

  private final GameContext context;
  private final Random random;

  public GameEngine(GameContext context) {
    this.context = context;
    this.random = new Random();
    startBattle();

    context.getEventBus().on(BattleEventType.UNIT_ATTACK_LOGIC, evt -> {
      UnitAttackPayload p = (UnitAttackPayload) evt.getPayload();
      p.getAttacker().performAttack(p.getTarget());
      context.getEventBus().emit(BattleEvent.of(BattleEventType.ENTITY_DAMAGED, p.getTarget()));
      p.getOnComplete().run();
    });
  }

  // -------------------- ИНИЦИАЛИЗАЦИЯ БОЯ --------------------
  private void startBattle() {
    Player player = context.getPlayer();
    player.initBattle();
    player.restoreMana(player.getMaxMana());

    // Очистка слотов игрока
    for (Slot slot : player.getSlots()) {
      slot.removeUnit();
    }

    // Планирование хода врага на первый раунд
    Enemy enemy = context.getEnemy();
    enemy.planActions(player, enemy);

    // Если в слотах уже есть юниты (на случай предзагруженного состояния),
    // планируем их действия
    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit != null) {
        unit.planActions(player, enemy);
      }
    }
  }

  // -------------------- КАРТЫ --------------------
  public boolean playCardOnTarget(Card card, Targetable target) {
    Player player = context.getPlayer();

    if (player.getMana() < card.getCost()) {
      System.out.println("Недостаточно маны для розыгрыша карты: " + card.getName());
      return false;
    }

    int count = card.getCountTarget();
    boolean applied = false;

    // --- одиночная цель ---
    if (count == 0) {
      boolean validTarget;
      switch (card.getType()) {
        case UNIT -> validTarget = target instanceof Slot;
        case ATTACK -> validTarget = target instanceof Enemy || target instanceof Player;
        case BUFF -> validTarget = target instanceof Unit;
        case DEBUFF -> validTarget = target instanceof Enemy;
        default -> validTarget = false;
      }

      if (!validTarget) {
        System.out.println("Карта " + card.getName() + " не может быть применена на эту цель.");
        return false;
      }

      for (CardEffect effect : card.getEffects()) {
        if (effect.apply(context, target)) {
          applied = true;
        }
      }
    }
    // --- множественные цели ---
    else {
      List<Targetable> possibleTargets = new ArrayList<>();

      switch (card.getType()) {
        case ATTACK -> {
          possibleTargets.addAll(player.getSlots().stream()
              .map(Slot::getUnit)
              .filter(u -> u != null)
              .map(u -> (Targetable) u)
              .toList());
          possibleTargets.add((Targetable) context.getEnemy());
          possibleTargets.add((Targetable) player);
        }
        case BUFF -> possibleTargets.addAll(player.getSlots().stream()
            .map(Slot::getUnit)
            .filter(u -> u != null)
            .map(u -> (Targetable) u)
            .toList());
        case DEBUFF -> possibleTargets.add((Targetable) context.getEnemy());
        case UNIT -> possibleTargets.addAll(player.getSlots().stream()
            .filter(s -> !s.isOccupied())
            .toList());
      }

      // Если целей нет — считаем карту разыгранной, эффекты не применяются
      if (possibleTargets.isEmpty()) {
        applied = true;
      } else {
        Random rnd = new Random();
        List<Targetable> copy = new ArrayList<>(possibleTargets);

        for (int i = 0; i < count && !copy.isEmpty(); i++) {
          int idx = rnd.nextInt(copy.size());
          Targetable t = copy.remove(idx);
          boolean result = false;
          for (CardEffect effect : card.getEffects()) {
            if (effect.apply(context, t)) {
              result = true;
            }
          }
          applied = applied || result;
        }
      }
    }

    // --- если карта сработала ---
    if (applied) {
      player.setMana(player.getMana() - card.getCost());

      if (card.isBurnOnPlay()) {
        System.out.println("🔥 Карта " + card.getName() + " сгорает после розыгрыша!");
        player.getHand().remove(card);
        player.getBattleDeck().remove(card);
        player.getDiscard().remove(card);
      } else {
        player.playCard(card);
      }

      context.getEventBus().emit(BattleEvent.of(BattleEventType.CARD_PLAYED, card));
    }

    checkBattleState();
    return applied;
  }

  // -------------------- ПОИСК СЛОТА ПО ЮНИТУ --------------------
  private Slot findSlotForUnit(Unit unit, Player player) {
    if (unit == null || player == null || player.getSlots() == null)
      return null;

    for (Slot slot : player.getSlots()) {
      if (unit.equals(slot.getUnit())) {
        return slot;
      }
    }

    return null;
  }

  private boolean checkBattleState() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    // 1️⃣ Удаляем мёртвых юнитов из слотов
    for (Slot slot : player.getSlots()) {
      Unit u = slot.getUnit();
      if (u != null && !u.isAlive()) {
        handleUnitDeath(u);
      }
    }

    // 2️⃣ Проверяем смерть игрока / врага
    if (!player.isAlive()) {
      context.getEventBus().emit(BattleEvent.of(BattleEventType.PLAYER_DIED, player));
      getWinner();
      return true;
    }

    if (!enemy.isAlive()) {
      context.getEventBus().emit(BattleEvent.of(BattleEventType.UNIT_DIED, enemy));
      getWinner();
      return true;
    }

    return false; // бой продолжается
  }

  // -------------------- END TURN --------------------

  // -------------------- END TURN --------------------

  // Основная функция endPlayerTurn, начинающая обработку хода
  public void endPlayerTurn(Runnable onTurnEnd) {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();
    TurnProcessor turnProcessor = new TurnProcessor();
    turnProcessor.setOnTurnEnd(onTurnEnd);

    processPlayerUnits(player, turnProcessor, enemy);
    processEnemy(enemy, turnProcessor, player);

    startPlayerTurn(turnProcessor);

    // 🚀 Старт очереди
    turnProcessor.runNext();
  }

  // ------------------------ ЮНИТ --------------------

  // Ход юнитов игрока
  private void processPlayerUnits(Player player, TurnProcessor turnProcessor, Enemy enemy) {
    System.out.println("-------------ХОД ЮНИТОВ ИГРОКА-------------");
    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit == null || !unit.isAlive())
        continue;

      // Эффекты
      processUnitEffects(unit, turnProcessor);

      // Если юнит умер от эффектов, пропускаем его действия
      if (!unit.isAlive())
        continue;

      // Действия
      processUnitActions(unit, turnProcessor, player, enemy);

      unit.getActionPlan().clear();
    }
  }

  // Эффекты юнита
  private void processUnitEffects(Unit unit, TurnProcessor turnProcessor) {
    for (StatusEffect effect : new ArrayList<>(unit.getStatusEffects())) {
      turnProcessor.addAction(() -> {
        if (!unit.isAlive()) {
          turnProcessor.runNext();
          return;
        }
        effect.onTurnStart(unit);

        context.getEventBus().emit(BattleEvent.of(
            BattleEventType.STATUS_EFFECT_TRIGGERED,
            new StatusEffectPayload(unit, effect, () -> {

              if (!effect.tick(unit)) {
                effect.onRemove(unit);
                unit.removeStatusEffect(effect);
              }

              // Проверяем смерть после эффекта
              if (checkBattleState())
                return;

              turnProcessor.runNext();
            })));
      });
    }
  }

  // Действия юнита
  private void processUnitActions(Unit unit, TurnProcessor turnProcessor, Player player, Enemy enemy) {
    for (ActionPlan.Action action : new ArrayList<>(unit.getActionPlan().getActions())) {
      turnProcessor.addAction(() -> {
        if (!unit.isAlive()) {
          turnProcessor.runNext(); // юнит умер — пропускаем действия
          return;
        }
        executeAction(unit, action, turnProcessor, player, enemy);
      });
    }
  }

  // Выполнение действия (атака/заклинание)
  private void executeAction(Unit unit, ActionPlan.Action action, TurnProcessor turnProcessor,
      Player player, Enemy enemy) {
    Targetable target = action.getTarget();

    // Перепланируем, если цель мертва
    if (target instanceof Entity entityTarget && !entityTarget.isAlive()) {
      unit.planActions(player, enemy);
      target = unit.getActionPlan().getActions().get(0).getTarget();
    }

    switch (action.getType()) {
      case ATTACK -> executeAttack(unit, target, turnProcessor, player);
      case CAST_SPELL -> executeSpell(unit, target, action.getEffect(), turnProcessor);
    }
  }

  // Заклинание
  private void executeSpell(Unit caster, Targetable target, StatusEffect effect, TurnProcessor turnProcessor) {
    context.getEventBus().emit(BattleEvent.of(
        BattleEventType.UNIT_CAST_SPELL,
        new UnitSpellPayload(caster, target, effect, () -> {
          if (!caster.isAlive()) {
            turnProcessor.runNext();
            return;
          }

          if (effect != null && target instanceof Entity entityTarget) {
            // Удаляем старый эффект, если есть
            StatusEffect existing = entityTarget.getStatusEffects().stream()
                .filter(e -> e.getName().equals(effect.getName()))
                .findFirst().orElse(null);

            if (existing != null) {
              existing.onRemove(entityTarget);
              entityTarget.removeStatusEffect(existing);
            }

            entityTarget.addStatusEffect(effect);

            effect.onApply(entityTarget);
            context.getEventBus().emit(BattleEvent.of(
                BattleEventType.STATUS_EFFECT_APPLIED, entityTarget));
          }

          if (checkBattleState())
            return; // если кто-то умер, прерываем очередь

          turnProcessor.runNext();
        })));
  }

  // Атака
  private void executeAttack(Unit attacker, Targetable target, TurnProcessor turnProcessor, Player player) {
    context.getEventBus().emit(BattleEvent.of(
        BattleEventType.UNIT_ATTACK,
        new UnitAttackPayload(attacker, target, () -> {
          if (checkBattleState())
            return; // после атаки проверяем смерть

          turnProcessor.runNext();
        })));
  }

  // Обработка смерти юнита
  private void handleUnitDeath(Entity entity) {
    context.getEventBus().emit(BattleEvent.of(BattleEventType.UNIT_DIED, entity));
    if (entity instanceof Unit deadUnit) {
      Slot slot = findSlotForUnit(deadUnit, context.getPlayer());
      if (slot != null)
        slot.removeUnit();
    }
  }

  // ------------------ ВРАГ --------------------

  private void processEnemy(Enemy enemy, TurnProcessor turnProcessor, Player player) {
    System.out.println("-------------ХОД ВРАГА-------------");
    if (enemy == null || !enemy.isAlive())
      return;

    // Эффекты врага
    processEnemyEffects(enemy, turnProcessor);

    // Если враг умер от эффектов, пропускаем действия
    if (!enemy.isAlive())
      return;

    // Планируем действия врага, если их нет
    if (enemy.getActionPlan().getActions().isEmpty()) {
      enemy.planActions(player, enemy);
    }

    // Действия врага
    processEnemyActions(enemy, turnProcessor, player);

    enemy.getActionPlan().clear();
  }

  // Эффекты врага
  private void processEnemyEffects(Enemy enemy, TurnProcessor turnProcessor) {
    for (StatusEffect effect : new ArrayList<>(enemy.getStatusEffects())) {
      turnProcessor.addAction(() -> {
        if (!enemy.isAlive()) {
          turnProcessor.runNext();
          return;
        }

        effect.onTurnStart(enemy);

        context.getEventBus().emit(BattleEvent.of(
            BattleEventType.STATUS_EFFECT_TRIGGERED,
            new StatusEffectPayload(enemy, effect, () -> {

              if (!effect.tick(enemy)) {
                effect.onRemove(enemy);
                enemy.removeStatusEffect(effect);
              }

              if (checkBattleState())
                return; // останавливаем очередь, если кто-то умер

              turnProcessor.runNext();
            })));
      });
    }
  }

  // Действия врага
  private void processEnemyActions(Enemy enemy, TurnProcessor turnProcessor, Player player) {
    for (ActionPlan.Action action : new ArrayList<>(enemy.getActionPlan().getActions())) {
      turnProcessor.addAction(() -> {
        if (!enemy.isAlive()) {
          turnProcessor.runNext(); // мёртвый враг не выполняет действия
          return;
        }
        executeEnemyAction(enemy, action, turnProcessor, player);
      });
    }
  }

  // Выполнение действия врага (атака/заклинание)
  private void executeEnemyAction(Enemy enemy, ActionPlan.Action action, TurnProcessor turnProcessor, Player player) {
    Targetable target = action.getTarget();

    // Перепланируем, если цель мертва
    if (target instanceof Entity entityTarget && !entityTarget.isAlive()) {
      enemy.planActions(player, enemy);
      target = enemy.getActionPlan().getActions().get(0).getTarget();
    }

    switch (action.getType()) {
      case ATTACK -> executeEnemyAttack(enemy, target, turnProcessor, player);
      case CAST_SPELL -> executeEnemySpell(enemy, target, action.getEffect(), turnProcessor);
    }
  }

  // Атака врага
  private void executeEnemyAttack(Enemy enemy, Targetable target, TurnProcessor turnProcessor, Player player) {
    context.getEventBus().emit(BattleEvent.of(
        BattleEventType.UNIT_ATTACK,
        new UnitAttackPayload(enemy, target, () -> {
          if (checkBattleState())
            return; // если кто-то умер, прерываем очередь
          turnProcessor.runNext();
        })));
  }

  // Заклинание врага
  private void executeEnemySpell(Enemy caster, Targetable target, StatusEffect effect, TurnProcessor turnProcessor) {
    System.out.println("Враг кастует заклинание: " + caster.getName());

    context.getEventBus().emit(BattleEvent.of(
        BattleEventType.UNIT_CAST_SPELL,
        new UnitSpellPayload(caster, target, effect, () -> {
          if (!caster.isAlive()) {
            turnProcessor.runNext();
            return;
          }

          if (effect != null && target instanceof Entity entityTarget) {
            // Удаляем старый эффект, если есть
            StatusEffect existing = entityTarget.getStatusEffects().stream()
                .filter(e -> e.getName().equals(effect.getName()))
                .findFirst().orElse(null);

            if (existing != null) {
              existing.onRemove(entityTarget);
              entityTarget.removeStatusEffect(existing);
            }

            // Добавляем новый эффект
            entityTarget.addStatusEffect(effect);
            effect.onApply(entityTarget);

            context.getEventBus().emit(BattleEvent.of(
                BattleEventType.STATUS_EFFECT_APPLIED, entityTarget));
          }

          if (checkBattleState())
            return; // останавливаем очередь, если кто-то умер

          turnProcessor.runNext();
        })));
  }

  // -------------------- КОЛОДА --------------------
  public void drawCards(int count) {
    Player player = context.getPlayer();

    int cardsNeeded = count;
    while (cardsNeeded > 0 && player.getHand().size() < player.getMaxHand()) {
      if (player.getBattleDeck().isEmpty() && !player.getDiscard().isEmpty()) {
        player.getBattleDeck().addAll(player.getDiscard());
        player.getDiscard().clear();
      }
      if (player.getBattleDeck().isEmpty())
        break;

      player.getHand().add(player.getBattleDeck().remove(0));
      cardsNeeded--;
    }

    player.restoreMana(player.getMaxMana());
  }

  // -------------------- ПРОВЕРКА КОНЦА БОЯ --------------------
  public boolean isBattleOver() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();
    return player.getHealth() <= 0 || enemy.getHealth() <= 0;
  }

  public String getWinner() {
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();
    if (player.getHealth() <= 0) {
      context.getEventBus().emit(BattleEvent.of(BattleEventType.RESTART, "Enemy"));
      return "Враг";
    }

    if (enemy.getHealth() <= 0) {
      context.getEventBus().emit(BattleEvent.of(BattleEventType.RESTART, "Player"));
      return "Игрок";
    }

    context.getEventBus().emit(BattleEvent.of(BattleEventType.RESTART, "Draw"));
    return "Ничья";
  }

  public void startPlayerTurn(TurnProcessor turnProcessor) {
    System.out.println("-------------ХОД ИГРОКА-------------");
    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    ArrayList<StatusEffect> effects = new ArrayList<>(player.getStatusEffects());

    if (effects.isEmpty()) {
      // 🔸 Нету эффектов — просто сразу продолжаем ход
      System.out.println("На игроке нет активных эффектов");
      processPlayerTurnWithoutEffects(turnProcessor, player, enemy);
      return;
    }

    // 🔹 Есть эффекты — обрабатываем каждый
    for (StatusEffect effect : effects) {
      effect.onTurnStart(player);

      turnProcessor.addAction(() -> {
        context.getEventBus().emit(BattleEvent.of(
            BattleEventType.STATUS_EFFECT_TRIGGERED,
            new StatusEffectPayload(player, effect, () -> {

              if (!effect.tick(player)) {
                effect.onRemove(player);
                player.removeStatusEffect(effect);
              }

              if (checkBattleState())
                return;

              processPlayerTurnWithoutEffects(turnProcessor, player, enemy);
              turnProcessor.runNext();
            })));
      });
    }
  }

  private void processPlayerTurnWithoutEffects(TurnProcessor turnProcessor, Player player, Enemy enemy) {
    player.restoreMana(player.getMaxMana());
    drawCards(player.getStartingHandSize());

    for (Slot slot : player.getSlots()) {
      Unit unit = slot.getUnit();
      if (unit != null) {
        System.out.println("В слоте есть юнит: " + unit.getName());
        unit.planActions(player, enemy);
      } else {
        System.out.println("В слоте нет юнита.");
      }
    }

    enemy.planActions(player, enemy);
  }

  public boolean summonUnit(Unit unit, Slot targetSlot) {
    if (targetSlot.getUnit() != null) {
      System.out.println("Слот уже занят, нельзя призвать юнита!");
      return false;
    }

    targetSlot.setUnit(unit);
    System.out.println("Призван юнит " + unit.getName() + " в слот " + targetSlot);

    // Планируем действия новопоявившегося юнита
    unit.planActions(context.getPlayer(), context.getEnemy());

    // Отправляем событие для UI
    context.getEventBus().emit(BattleEvent.of(BattleEventType.UNIT_SUMMONED, unit));
    return true;
  }
}
