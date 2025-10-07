package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.model.Card;
import io.github.some_example_name.model.CardType;
import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.model.Slot;
import io.github.some_example_name.ui.BattleScreenUI;

public class CardActor extends Table {
  private final Card card;
  private float grabOffsetX, grabOffsetY;
  private float startX, startY;
  private final BattleScreenUI battleScreenUI;
  private Image highlightImage; // 👈 поле класса (например, внутри CellUI)

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
    this.add(cardImage).size(50, 70).row();
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

        // Проверяем цель под картой
        Vector2 stageCoords = localToStageCoordinates(new Vector2(getWidth() / 2, getHeight() / 2));
        Object target = battleScreenUI.getBoardUI().findTargetAt(stageCoords.x, stageCoords.y);

        boolean shouldHighlight = false;

        if (card.getType() == CardType.UNIT && target instanceof Slot) {
          // юнит можно ставить только в слот
          Slot slot = (Slot) target;
          if (!slot.isOccupied()) {
            shouldHighlight = true;
          }
        }

        if ((card.getType() == CardType.ATTACK || card.getType() == CardType.DEBUFF) && target instanceof Enemy) {
          // атака работает только по врагу
          shouldHighlight = true;
        }

        if (card.getType() == CardType.BUFF && target instanceof io.github.some_example_name.model.Unit) {
          // Баф можно накладывать только на юнита
          io.github.some_example_name.model.Unit unit = (io.github.some_example_name.model.Unit) target;
          if (unit.isAlive()) {
            shouldHighlight = true;
          }
        }

        setHighlighted(shouldHighlight);
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
      if (highlightImage == null) {
        Texture texture = new Texture(Gdx.files.internal("game/highlight.png"));
        highlightImage = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        highlightImage.setTouchable(Touchable.disabled);

        // 🔹 Размер рамки под саму картинку карты (фиксированный)
        float imageWidth = 150f;
        float imageHeight = 170f;

        // 🔹 Центрируем по горизонтали, опускаем вниз под карту
        float x = (getWidth() - imageWidth) / 2f;
        float y = (getHeight() - imageHeight) / 2f - 5f; // чуть ниже центра

        highlightImage.setBounds(x, y, imageWidth, imageHeight);
        addActorAt(0, highlightImage); // под контент
      }

      highlightImage.setVisible(true);
    } else if (highlightImage != null) {
      highlightImage.setVisible(false);
    }
  }

  public Card getCard() {
    return card;
  }

  public void resetPosition() {
    setPosition(startX, startY);
  }
}
