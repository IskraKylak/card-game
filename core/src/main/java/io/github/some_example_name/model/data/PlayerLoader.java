package io.github.some_example_name.model.data;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.some_example_name.model.Faction;
import io.github.some_example_name.model.Player;

public class PlayerLoader {

  private static class PlayerJson {
    int id;
    String name;
    int health;
    int attack;
    int defense;
    int maxMana;
    String faction;
  }

  public static List<Player> loadPlayers(String path) {
    try {
      Gson gson = new Gson();
      List<PlayerJson> data = gson.fromJson(new FileReader(path),
          new TypeToken<List<PlayerJson>>() {
          }.getType());

      List<Player> players = new ArrayList<>();
      for (PlayerJson p : data) {
        Player player = new Player(p.id, p.name, p.health, p.attack, p.defense, p.maxMana, Faction.valueOf(p.faction));
        players.add(player);
      }
      return players;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  public static Player getPlayerByFaction(Faction faction) {
    return loadPlayers("data/players.json").stream()
        .filter(p -> p.getFaction() == faction)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown faction: " + faction));
  }
}
