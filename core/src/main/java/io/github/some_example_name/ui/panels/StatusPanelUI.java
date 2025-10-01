package io.github.some_example_name.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.some_example_name.model.Player;

public class StatusPanelUI extends Table {

  private final Player player;
  private final Label healthLabel;
  private final Label manaLabel;
  private final TextButton endTurnButton;

  public StatusPanelUI(Player player, Skin skin) {
    this.player = player;

    healthLabel = new Label("HP: " + player.getHealth() + "/" + player.getMaxHealth(), skin);
    manaLabel = new Label("Mana: " + player.getMana() + "/" + player.getMaxMana(), skin);
    endTurnButton = new TextButton("End Turn", skin);

    this.add(healthLabel).padRight(10);
    this.add(manaLabel).padRight(10);
    this.add(endTurnButton);
  }

  public void update() {
    healthLabel.setText("HP: " + player.getHealth() + "/" + player.getMaxHealth());
    manaLabel.setText("Mana: " + player.getMana() + "/" + player.getMaxMana());
  }

  public TextButton getEndTurnButton() {
    return endTurnButton;
  }
}
