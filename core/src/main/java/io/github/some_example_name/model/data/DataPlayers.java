package io.github.some_example_name.model.data;

import io.github.some_example_name.model.Faction;
import io.github.some_example_name.model.Player;

public class DataPlayers {

  public static Player createLifePlayer() {
    Player p = new Player(20, 3, 4, 6, Faction.LIFE);
    p.getDefaultDeck().addAll(DataCards.createFactionCards(Faction.LIFE));
    return p;
  }

  public static Player createDeathPlayer() {
    Player p = new Player(18, 4, 4, 6, Faction.DEATH);
    p.getDefaultDeck().addAll(DataCards.createFactionCards(Faction.DEATH));
    return p;
  }

  public static Player createOrderPlayer() {
    Player p = new Player(22, 2, 4, 6, Faction.ORDER);
    p.getDefaultDeck().addAll(DataCards.createFactionCards(Faction.ORDER));
    return p;
  }

  public static Player createChaosPlayer() {
    Player p = new Player(19, 3, 4, 6, Faction.CHAOS);
    p.getDefaultDeck().addAll(DataCards.createFactionCards(Faction.CHAOS));
    return p;
  }

  public static Player getPlayerByFaction(Faction faction) {
    switch (faction) {
      case LIFE:
        return createLifePlayer();
      case DEATH:
        return createDeathPlayer();
      case ORDER:
        return createOrderPlayer();
      case CHAOS:
        return createChaosPlayer();
      default:
        return createLifePlayer();
    }
  }
}
