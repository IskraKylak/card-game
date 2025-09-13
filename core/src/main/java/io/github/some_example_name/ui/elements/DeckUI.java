package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.model.Player;

public class DeckUI extends Table {

  private final Player player;
  private final Label counter;

  public DeckUI(Player player, Skin skin) {
    this.player = player;

    Texture deckTexture = new Texture("game/card_back.png");
    Image deckImage = new Image(deckTexture);

    float targetHeight = 120f;
    float aspect = (float) deckTexture.getWidth() / deckTexture.getHeight();
    deckImage.setScaling(Scaling.fit);

    // Счётчик карт в колоде
    counter = new Label(String.valueOf(player.getBattleDeck().size()), skin);
    counter.setFontScale(1.2f);

    // Вложенная таблица: картинка + счётчик поверх
    Table stack = new Table();
    stack.add(deckImage).size(targetHeight * aspect, targetHeight);

    // Счётчик кладём поверх внизу справа
    this.add(stack).pad(10);
    this.row();
    this.add(counter).padTop(5);
  }

  public void update() {
    counter.setText(String.valueOf(player.getBattleDeck().size()));
  }
}
