package io.github.some_example_name.model.data;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.core.effects.BuffEffect;
import io.github.some_example_name.core.effects.CardEffect;
import io.github.some_example_name.core.effects.DamageEffect;
import io.github.some_example_name.core.effects.DebuffEffect;
import io.github.some_example_name.core.effects.SummonUnitEffect;
import io.github.some_example_name.model.Card;
import io.github.some_example_name.model.CardType;
import io.github.some_example_name.model.Faction;
import io.github.some_example_name.model.status.AttackBuffEffect;
import io.github.some_example_name.model.status.PoisonEffect;

public class DataCards {

        public static List<Card> createAllCards() {
                List<Card> cards = new ArrayList<>();

                cards.add(new Card(1, "Squeer's Call", "Squier is a total loser", 1, CardType.UNIT, Faction.LIFE,
                                new SummonUnitEffect(1), // ID юнита из DataUnits
                                "game/card-unit3.png"));
                cards.add(new Card(2, "Summon a Warrior", "Warrior - average fighter", 2, CardType.UNIT, Faction.LIFE,
                                new SummonUnitEffect(2), // ID юнита из DataUnits
                                "game/card-unit2.png"));
                cards.add(new Card(3, "Summoning the Knight", "The coolest guy so far", 3, CardType.UNIT, Faction.LIFE,
                                new SummonUnitEffect(3), // ID юнита из DataUnits
                                "game/card-unit1.png"));
                cards.add(new Card(4, "Hit", "4 damage to enemy", 2, // мана-стоимость
                                CardType.ATTACK, Faction.CHAOS, new DamageEffect(4), "game/card-hit.png"));
                cards.add(new Card(10, "Rallying Strike", "Даёт юниту +3 к атаке на 2 хода", 2, // мана
                                CardType.BUFF, Faction.LIFE, new BuffEffect(() -> new AttackBuffEffect(3, 99)),
                                "game/card-attackPower.png"));
                cards.add(new Card(10, "Poison", "Отравляет врага", 3, // мана
                                CardType.DEBUFF, Faction.LIFE, new DebuffEffect(() -> new PoisonEffect(4, 3)),
                                "game/card-poison.png"));

                return cards;
        }

        // Метод для создания карт по фракции
        public static List<Card> createFactionCards(Faction faction) {
                List<Card> cards = new ArrayList<>();

                switch (faction) {
                case LIFE:
                        cards.add(new Card(1, "Squeer's Call", "Squier is a total loser", 1, CardType.UNIT,
                                        Faction.LIFE, new SummonUnitEffect(1), "game/card-unit3.png" // ID юнита из
                                                                                                     // DataUnits
                        ));
                        cards.add(new Card(2, "Summon a Warrior", "Warrior - average fighter", 2, CardType.UNIT,
                                        Faction.LIFE, new SummonUnitEffect(2), // ID юнита из DataUnits
                                        "game/card-unit2.png"));
                        cards.add(new Card(3, "Summoning the Knight", "The coolest guy so far", 3, CardType.UNIT,
                                        Faction.LIFE, new SummonUnitEffect(3), // ID юнита из DataUnits
                                        "game/card-unit1.png"));
                        cards.add(new Card(4, "Hit", "4 damage to enemy", 2, // мана-стоимость
                                        CardType.ATTACK, Faction.CHAOS, new DamageEffect(4), "game/card-hit.png"));
                        cards.add(new Card(10, "Rallying Strike", "Даёт юниту +3 к атаке на 2 хода", 2, // мана
                                        CardType.BUFF, Faction.LIFE, new BuffEffect(() -> new AttackBuffEffect(3, 99)),
                                        "game/card-attackPower.png"));
                        cards.add(new Card(10, "Poison", "Отравляет врага", 3, // мана
                                        CardType.DEBUFF, Faction.LIFE, new DebuffEffect(() -> new PoisonEffect(4, 3)),
                                        "game/card-poison.png"));
                        break;
                case DEATH:
                        cards.add(new Card(3, "Скелет", "Призывает 1/1 скелета", 2, CardType.UNIT, Faction.DEATH,
                                        new SummonUnitEffect(2), // допустим, Skeleton имеет id 2
                                        "game/skeleton.png"));
                        cards.add(new Card(4, "Проклятие", "Накладывает дебаф на врага", 1, CardType.DEBUFF,
                                        Faction.DEATH, null, // можно будет сделать DebuffEffect позже
                                        ""));
                        break;
                case ORDER:
                        cards.add(new Card(5, "Страж порядка", "Призывает 2/2 стража", 3, CardType.UNIT, Faction.ORDER,
                                        new SummonUnitEffect(3), // ID юнита из DataUnits
                                        "game/guard.png"));
                        break;
                case CHAOS:
                        cards.add(new Card(6, "Хаотический взрыв", "Наносит 3 урона всем юнитам", 3, CardType.ATTACK,
                                        Faction.CHAOS, null, // можно сделать AoEAttackEffect позже
                                        ""));
                        break;
                }

                return cards;
        }
}
