package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.some_example_name.model.Faction;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.ui.BattleScreenUI;

public class CardGame extends Game {

  @Override
  public void create() {
    // Создаём игрока
    Player player = new Player(
        30, // maxHealth
        10, // maxMana
        5, // maxHand
        5, // maxUnits
        Faction.LIFE // фракция
    );
    player.buildDefaultDeckFromFaction();
    player.buildBattleDeck();

    for (int i = 0; i < 5; i++) { // берём 5 карт в руку
      player.drawCard();
    }

    // Используем стандартный skin libGDX
    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
    setScreen(new BattleScreenUI(player, skin));
  }
}
