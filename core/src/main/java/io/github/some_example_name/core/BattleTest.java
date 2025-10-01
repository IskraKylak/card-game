package io.github.some_example_name.core;

import io.github.some_example_name.model.*;
import io.github.some_example_name.model.data.*;

public class BattleTest {
  public static void main(String[] args) {
    // Создаём игрока
    Player player = DataPlayers.getPlayerByFaction(Faction.LIFE);
    player.buildDefaultDeckFromFaction();
    player.buildBattleDeck();
    player.initBattle();
  }
}
