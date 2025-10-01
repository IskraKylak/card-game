package io.github.some_example_name.model.data;

import java.util.List;
import io.github.some_example_name.model.Card;
import io.github.some_example_name.model.Faction;

public class DataCards {

        private static final String PATH = "data/cards.json";

        /**
         * Загружает все карты из JSON.
         */
        public static List<Card> createAllCards() {
                return CardLoader.loadCards(PATH);
        }

        /**
         * Загружает карты только для указанной фракции.
         */
        public static List<Card> createFactionCards(Faction faction) {
                List<Card> all = CardLoader.loadCards(PATH);
                all.removeIf(card -> card.getFaction() != faction);
                return all;
        }
}
