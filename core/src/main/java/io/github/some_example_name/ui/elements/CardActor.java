package io.github.some_example_name.ui.elements;

// В начало импортов добавь:
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.ui.windows.CardInfoWindow;

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
import io.github.some_example_name.model.Unit;
import io.github.some_example_name.ui.BattleScreenUI;
import io.github.some_example_name.ui.SoundManager;
import io.github.some_example_name.ui.panels.BoardUI;

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

        boolean shouldHighlight = false;

        // Для карт с countTarget > 0 — подсветка, если наведена на BoardUI
        Vector2 stageCoords = localToStageCoordinates(new Vector2(getWidth() / 2, getHeight() / 2));
        if (card.getCountTarget() > 0) {
          Object hovered = battleScreenUI.getBoardUI().hit(stageCoords.x, stageCoords.y, true);
          if (hovered != null) {
            shouldHighlight = true;
          }
        } else {
          // Для обычных карт проверяем цель под картой
          Object target = battleScreenUI.getBoardUI().findTargetAt(stageCoords.x, stageCoords.y);

          if (card.getType() == CardType.UNIT && target instanceof Slot slot && !slot.isOccupied()) {
            shouldHighlight = true;
          }

          if ((card.getType() == CardType.ATTACK || card.getType() == CardType.DEBUFF) && target instanceof Enemy) {
            shouldHighlight = true;
          }

          if (card.getType() == CardType.BUFF && target instanceof io.github.some_example_name.model.Unit unit
              && unit.isAlive()) {
            shouldHighlight = true;
          }
        }

        setHighlighted(shouldHighlight);
      }

      @Override
      public void dragStop(InputEvent event, float x, float y, int pointer) {
        Vector2 stageCoords = localToStageCoordinates(new Vector2(getWidth() / 2, getHeight() / 2));

        if (card.getCountTarget() > 0) {
          // Просто кидаем на BoardUI — Engine выберет случайные цели
          battleScreenUI.onCardDropped(CardActor.this, -1, -1);
        } else {
          battleScreenUI.onCardDropped(CardActor.this, stageCoords.x, stageCoords.y);
        }

        setHighlighted(false);
      }

    });

    // 👇 Добавляем обработчик клика
    this.addListener(new ClickListener() {
      private long lastDownTime = 0;
      private boolean dragged = false;

      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        lastDownTime = System.currentTimeMillis();
        dragged = false;
        return super.touchDown(event, x, y, pointer, button);
      }

      @Override
      public void touchDragged(InputEvent event, float x, float y, int pointer) {
        dragged = true;
        super.touchDragged(event, x, y, pointer);
      }

      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        long now = System.currentTimeMillis();
        // Быстрый клик (меньше 200 мс, без перетаскивания)
        if (!dragged && (now - lastDownTime < 200)) {
          CardInfoWindow infoWindow = new CardInfoWindow(card, skin);
          getStage().addActor(infoWindow.fadeIn()); // плавное появление
        }
        super.touchUp(event, x, y, pointer, button);
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
