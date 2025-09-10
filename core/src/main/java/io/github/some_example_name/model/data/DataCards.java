package io.github.some_example_name.model.data;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.core.effects.SummonUnitEffect;
import io.github.some_example_name.model.Card;
import io.github.some_example_name.model.CardType;
import io.github.some_example_name.model.Faction;

public class DataCards {

    public static List<Card> createAllCards() {
        List<Card> cards = new ArrayList<>();

        cards.add(new Card(
                1,
                "Призыв Скваер",
                "Скваер - абсолютный лох",
                1,
                CardType.UNIT,
                Faction.LIFE,
                new SummonUnitEffect(1) // ID юнита из DataUnits
        ));
        cards.add(new Card(
                2,
                "Призыв Лучника",
                "Лучник - убейте эту тварь",
                2,
                CardType.UNIT,
                Faction.LIFE,
                new SummonUnitEffect(2) // ID юнита из DataUnits
        ));
        cards.add(new Card(
                3,
                "Призыв воина",
                "Самий крутой пока чел",
                3,
                CardType.UNIT,
                Faction.LIFE,
                new SummonUnitEffect(3) // ID юнита из DataUnits
        ));

        return cards;
    }

    // Метод для создания карт по фракции
    public static List<Card> createFactionCards(Faction faction) {
        List<Card> cards = new ArrayList<>();

        switch (faction) {
            case LIFE:
                cards.add(new Card(
                        1,
                        "Призыв Скваер",
                        "Скваер - абсолютный лох",
                        1,
                        CardType.UNIT,
                        Faction.LIFE,
                        new SummonUnitEffect(1) // ID юнита из DataUnits
                ));
                cards.add(new Card(
                        2,
                        "Призыв Лучника",
                        "Лучник - убейте эту тварь",
                        2,
                        CardType.UNIT,
                        Faction.LIFE,
                        new SummonUnitEffect(2) // ID юнита из DataUnits
                ));
                cards.add(new Card(
                        3,
                        "Призыв воина",
                        "Самий крутой пока чел",
                        3,
                        CardType.UNIT,
                        Faction.LIFE,
                        new SummonUnitEffect(3) // ID юнита из DataUnits
                ));
                break;
            case DEATH:
                cards.add(new Card(
                        3,
                        "Скелет",
                        "Призывает 1/1 скелета",
                        2,
                        CardType.UNIT,
                        Faction.DEATH,
                        new SummonUnitEffect(2) // допустим, Skeleton имеет id 2
                ));
                cards.add(new Card(
                        4,
                        "Проклятие",
                        "Накладывает дебаф на врага",
                        1,
                        CardType.DEBUFF,
                        Faction.DEATH,
                        null // можно будет сделать DebuffEffect позже
                ));
                break;
            case ORDER:
                cards.add(new Card(
                        5,
                        "Страж порядка",
                        "Призывает 2/2 стража",
                        3,
                        CardType.UNIT,
                        Faction.ORDER,
                        new SummonUnitEffect(3)));
                break;
            case CHAOS:
                cards.add(new Card(
                        6,
                        "Хаотический взрыв",
                        "Наносит 3 урона всем юнитам",
                        3,
                        CardType.ATTACK,
                        Faction.CHAOS,
                        null // можно сделать AoEAttackEffect позже
                ));
                break;
        }

        return cards;
    }
}
