package io.github.some_example_name.model.data;

import io.github.some_example_name.model.Faction;
import io.github.some_example_name.model.Player;

public class DataPlayers {

  public static Player getPlayerByFaction(Faction faction) {
    return PlayerLoader.getPlayerByFaction(faction);
  }
}
