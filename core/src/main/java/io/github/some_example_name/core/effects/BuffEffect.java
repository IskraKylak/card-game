package io.github.some_example_name.core.effects;

import io.github.some_example_name.core.BattleEvent;
import io.github.some_example_name.core.BattleEventType;
import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Unit;
import io.github.some_example_name.model.payload.StatusEffectPayload;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Targetable;
import io.github.some_example_name.model.status.StatusEffect;
import java.util.function.Supplier;

public class BuffEffect implements CardEffect {
  private final Supplier<StatusEffect> effectSupplier;

  public BuffEffect(Supplier<StatusEffect> effectSupplier) {
    this.effectSupplier = effectSupplier;
  }

  @Override
  public boolean apply(GameContext context, Targetable target) {
    if (target instanceof Unit || target instanceof Player) { // <- теперь и Player
      StatusEffect effect = effectSupplier.get();

      // 🔹 проверка на уже существующий эффект (для Unit и Player)
      Targetable container = target; // юнит или игрок
      if (container instanceof Unit unit) {
        StatusEffect existing = unit.getStatusEffects().stream()
            .filter(e -> e.getName().equals(effect.getName()))
            .findFirst().orElse(null);
        if (existing != null) {
          existing.onRemove(unit);
          unit.removeStatusEffect(existing);
        }

        effect.onApply(unit);
        context.getEventBus().emit(BattleEvent.of(
            BattleEventType.STATUS_EFFECT_APPLIED,
            new StatusEffectPayload(unit, effect, () -> {
            })));

        if (effect.getDuration() > 0) {
          unit.addStatusEffect(effect);
        }

      } else if (container instanceof Player player) {
        StatusEffect existing = player.getStatusEffects().stream()
            .filter(e -> e.getName().equals(effect.getName()))
            .findFirst().orElse(null);
        if (existing != null) {
          existing.onRemove(player);
          player.removeStatusEffect(existing);
        }

        effect.onApply(player);
        context.getEventBus().emit(BattleEvent.of(
            BattleEventType.STATUS_EFFECT_APPLIED,
            new StatusEffectPayload(player, effect, () -> {
            })));

        if (effect.getDuration() > 0) {
          player.addStatusEffect(effect);
        }
      }

      System.out.println("На " + container + " наложен эффект: " + effect.getName());
      return true;
    }

    System.out.println("Эффект можно применить только на Unit или Player!");
    return false;
  }

}
