package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import io.github.some_example_name.model.Card;

public class CardActor extends Table {
  private Card card;
  private float grabOffsetX, grabOffsetY; // смещение при захвате
  private float startX, startY; // начальная позиция

  public CardActor(Card card, Skin skin) {
    this.card = card;

    // картинка
    Texture texture = new Texture(card.getImagePath());
    Image cardImage = new Image(new TextureRegion(texture));

    // название
    Label nameLabel = new Label(card.getName(), skin);
    // мана
    Label costLabel = new Label("Mana: " + card.getCost(), skin);
    // описание
    Label descLabel = new Label(card.getDescription(), skin);
    descLabel.setWrap(true);

    // собираем UI
    this.add(cardImage).size(100, 150).row();
    this.add(nameLabel).row();
    this.add(costLabel).row();
    this.add(descLabel).width(100).row();

    // --- Логика перетаскивания ---
    // перетаскивание
    this.addListener(new DragListener() {
      @Override
      public void dragStart(InputEvent event, float x, float y, int pointer) {
        grabOffsetX = x;
        grabOffsetY = y;

        // запоминаем стартовую позицию
        startX = getX();
        startY = getY();

        toFront(); // карта наверх
      }

      @Override
      public void drag(InputEvent event, float x, float y, int pointer) {
        setPosition(getX() + x - grabOffsetX, getY() + y - grabOffsetY);
      }

      @Override
      public void dragStop(InputEvent event, float x, float y, int pointer) {
        // TODO: тут можно проверить "зону сброса" (поле боя)
        // пока возвращаем обратно
        setPosition(startX, startY);
      }
    });
  }

  public Card getCard() {
    return card;
  }
}
