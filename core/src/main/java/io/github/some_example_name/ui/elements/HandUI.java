package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.github.some_example_name.model.Card;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.ui.BattleScreenUI;

public class HandUI extends Table {
  private final Player player;
  private final Skin skin;
  private final BattleScreenUI battleScreenUI;

  public HandUI(Player player, Skin skin, BattleScreenUI battleScreenUI) {
    this.player = player;
    this.skin = skin;
    this.battleScreenUI = battleScreenUI;
    update();
  }

  public void update() {
    this.clear();
    for (Card card : player.getHand()) {
      CardActor cardActor = new CardActor(card, skin, battleScreenUI);
      this.add(cardActor).pad(5);
    }
  }
}
