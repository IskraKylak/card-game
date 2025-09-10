package io.github.some_example_name.model;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.core.effects.CardEffect;

public class Card {
    private final int id; // Уникальный идентификатор карты
    private final String name; // Название
    private final String description; // Описание
    private final CardType type; // Тип карты (существо, заклинание, артефакт и т.п.)
    private final int manaCost; // Стоимость (мана/ресурсы)
    private final Faction faction;
    private final CardEffect effect; // Эффект карты

    public Card(int id, String name, String description, int manaCost, CardType type, Faction faction,
            CardEffect effect) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.type = type;
        this.faction = faction;
        this.effect = effect;
    }

    // --- Геттеры ---
    public Faction getFaction() {
        return faction;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CardType getType() {
        return type;
    }

    public int getCost() {
        return manaCost;
    }

    public CardEffect getEffect() {
        return effect;
    }

    // --- Вызов эффекта карты ---
    public void play(GameContext context, Slot targetSlot) {
        if (effect != null) {
            effect.apply(context, targetSlot);
        }
    }
}
