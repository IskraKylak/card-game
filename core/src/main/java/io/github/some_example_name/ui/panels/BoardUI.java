package io.github.some_example_name.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.core.GameEngine;
import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.Slot;
import io.github.some_example_name.ui.elements.SlotUI;

public class BoardUI extends Table {

  private final GameContext context;
  private final GameEngine engine;

  public BoardUI(GameContext context, GameEngine engine, Skin skin) {
    this.context = context;
    this.engine = engine;

    // синий фон
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.BLUE);
    pixmap.fill();
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    this.setBackground(new TextureRegionDrawable(texture));

    Player player = context.getPlayer();
    Enemy enemy = context.getEnemy();

    // --- Enemy сверху ---
    Label enemyLabel = new Label("Enemy: " + enemy.getName() + " HP: " + enemy.getHealth(), skin);
    this.add(enemyLabel).expandX().top().center().padTop(10).row();

    // --- пустое пространство между верхом и низом ---
    this.add().expand().row();

    // --- Слоты игрока ---
    Table slotsRow = new Table();
    slotsRow.setDebug(true);
    for (Slot slot : player.getSlots()) {
      SlotUI slotUI = new SlotUI(slot, skin);
      slotsRow.add(slotUI).size(40, 30).pad(40);
    }

    // --- Игрок снизу ---
    Label playerLabel = new Label(
        "Player HP: " + player.getHealth() + "/" + player.getMaxHealth() +
            " Mana: " + player.getMana() + "/" + player.getMaxMana(),
        skin);

    // Сначала слоты, затем игрок
    this.add(slotsRow).expandX().top().center().padBottom(10).row();
    this.add(playerLabel).expandX().top().center().padBottom(10).row();
  }
}
