package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import io.github.some_example_name.model.Card;
import io.github.some_example_name.ui.BattleScreenUI;

public class CardActor extends Table {
  private final Card card;
  private float grabOffsetX, grabOffsetY;
  private float startX, startY;
  private final BattleScreenUI battleScreenUI;

  public CardActor(Card card, Skin skin, BattleScreenUI battleScreenUI) {
    this.card = card;
    this.battleScreenUI = battleScreenUI;

    // Картинка
    Texture texture = new Texture(card.getImagePath());
    Image cardImage = new Image(new TextureRegion(texture));

    // Название и описание
    Label nameLabel = new Label(card.getName(), skin);
    Label costLabel = new Label("Mana: " + card.getCost(), skin);
    // Label descLabel = new Label(card.getDescription(), skin);
    // descLabel.setWrap(true);

    // Сборка UI
    this.add(cardImage).size(100, 150).row();
    this.add(nameLabel).row();
    this.add(costLabel).row();
    // this.add(descLabel).width(100).row();

    // DragListener
    this.addListener(new DragListener() {
      @Override
      public void dragStart(InputEvent event, float x, float y, int pointer) {
        grabOffsetX = x;
        grabOffsetY = y;
        startX = getX();
        startY = getY();
        toFront(); // карта наверх
      }

      @Override
      public void drag(InputEvent event, float x, float y, int pointer) {
        setPosition(getX() + x - grabOffsetX, getY() + y - grabOffsetY);

        // проверяем, есть ли цель под картой
        Vector2 stageCoords = localToStageCoordinates(new Vector2(getWidth() / 2, getHeight() / 2));
        Object target = battleScreenUI.getBoardUI().findTargetAt(stageCoords.x, stageCoords.y);

        if (target != null) {
          setHighlighted(true); // подсвечиваем карту
        } else {
          setHighlighted(false); // убираем подсветку
        }
      }

      @Override
      public void dragStop(InputEvent event, float x, float y, int pointer) {
        Vector2 stageCoords = localToStageCoordinates(new Vector2(getWidth() / 2, getHeight() / 2));
        battleScreenUI.onCardDropped(CardActor.this, stageCoords.x, stageCoords.y);
        setHighlighted(false);
      }
    });
  }

  public void setHighlighted(boolean highlighted) {
    if (highlighted) {
      this.setColor(0, 1, 0, 1); // зелёный оттенок
      this.setDebug(true); // включаем рамку, если используешь debug
    } else {
      this.setColor(1, 1, 1, 1); // возвращаем стандартный вид
      this.setDebug(false);
    }
  }

  public Card getCard() {
    return card;
  }

  public void resetPosition() {
    setPosition(startX, startY);
  }
}
