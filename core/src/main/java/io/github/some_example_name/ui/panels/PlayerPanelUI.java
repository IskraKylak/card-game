package io.github.some_example_name.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.ui.elements.DeckUI;
import io.github.some_example_name.ui.elements.DiscardUI;
import io.github.some_example_name.ui.elements.HandUI;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class PlayerPanelUI extends Table {

  private final Player player;
  private final HandUI handUI;

  public PlayerPanelUI(Player player, Skin skin) {
    this.player = player;

    // зелёный фон
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.GREEN);
    pixmap.fill();
    Texture texture = new Texture(pixmap);
    pixmap.dispose();

    this.setBackground(new TextureRegionDrawable(texture));

    // Подпанели
    DeckUI deckUI = new DeckUI(player, skin);
    handUI = new HandUI(player, skin);
    DiscardUI discardUI = new DiscardUI(player, skin);
    StatusPanelUI statusPanelUI = new StatusPanelUI(player, skin);

    // Верхняя строка (колода - рука - отбой)
    Table topRow = new Table();
    topRow.add(deckUI).left().pad(10);
    topRow.add(handUI).expandX().center();
    topRow.add(discardUI).right().pad(10);

    // Добавляем всё в PlayerPanel
    this.add(topRow).expand().fill().row();
    this.add(statusPanelUI).right().pad(10).row();
  }

  // метод для обновления UI после изменения модели
  public void refresh() {
    handUI.update();
    // deckUI.update(); // можно добавить, если DeckUI будет динамический
    // discardUI.update(); // аналогично
    // statusPanelUI.update();
  }
}
