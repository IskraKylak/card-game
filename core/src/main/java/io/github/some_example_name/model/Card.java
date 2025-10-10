package io.github.some_example_name.model;

import java.util.List;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.core.effects.CardEffect;

public class Card {
    private final int id; // Уникальный идентификатор карты
    private final String name; // Название
    private final String description; // Описание
    private final CardType type; // Тип карты (существо, заклинание, артефакт и т.п.)
    private final int manaCost; // Стоимость (мана/ресурсы)
    private final Faction faction;
    private final List<CardEffect> effects; // ✅ теперь список эффектов
    private final String imagePath; // Путь к изображению карты
    private final boolean burnOnPlay; // 👈 новый флаг

    private final int countTarget;

    public Card(int id, String name, String description, int manaCost, CardType type,
            Faction faction, List<CardEffect> effects, String imagePath,
            boolean burnOnPlay, int countTarget) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.type = type;
        this.faction = faction;
        this.effects = effects; // теперь принимает список
        this.imagePath = imagePath;
        this.burnOnPlay = burnOnPlay;
        this.countTarget = countTarget;
    }

    public int getCountTarget() {
        return countTarget;
    }

    public boolean isBurnOnPlay() {
        return burnOnPlay;
    }

    public String getImagePath() {
        return imagePath;
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

    // ✅ Геттер для списка эффектов
    public List<CardEffect> getEffects() {
        return effects;
    }

    // --- Вызов эффекта карты ---
    // --- Вызов всех эффектов карты ---
    public void play(GameContext context, Targetable target) {
        if (effects == null || effects.isEmpty())
            return;

        for (CardEffect effect : effects) {
            if (effect != null) {
                effect.apply(context, target);
            }
        }
    }
}
