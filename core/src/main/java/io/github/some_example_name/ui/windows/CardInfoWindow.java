package io.github.some_example_name.ui.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.some_example_name.model.Card;

public class CardInfoWindow extends Window {

  public CardInfoWindow(Card card, Skin skin) {
    super("Card Info", skin);

    // Настройки окна
    setSize(400, 500);
    setModal(true);
    setMovable(false);
    setResizable(false);
    setColor(1, 1, 1, 0.95f);
    padBottom(10);

    // Центрирование
    setPosition(
        getStage() == null ? 440 : (getStage().getWidth() - getWidth()) / 2f,
        getStage() == null ? 140 : (getStage().getHeight() - getHeight()) / 2f);

    // ---------- Контент ----------
    Table content = new Table(skin);
    content.defaults().pad(8);

    Texture texture = new Texture(card.getImagePath());
    Image cardImage = new Image(new TextureRegionDrawable(texture));
    cardImage.setSize(100, 160);

    Label name = new Label(card.getName(), skin);
    Label type = new Label("Type: " + card.getType(), skin);
    Label cost = new Label("Mana Cost: " + card.getCost(), skin);
    Label desc = new Label(card.getDescription(), skin);
    desc.setWrap(true);

    content.add(cardImage).size(100, 160).row();
    content.add(name).row();
    content.add(type).row();
    content.add(cost).row();
    content.add(desc).width(340).padBottom(20).row();

    // ---------- Скролл ----------
    ScrollPane scrollPane = new ScrollPane(content, skin);
    scrollPane.setFadeScrollBars(false);
    scrollPane.setScrollingDisabled(true, false); // скролл только по вертикали

    // ---------- Футер ----------
    TextButton closeButton = new TextButton("Close", skin);
    closeButton.addListener(event -> {
      if (closeButton.isPressed()) {
        remove();
        return true;
      }
      return false;
    });

    Table footer = new Table(skin);
    footer.add(closeButton).pad(10).center();

    // ---------- Компоновка ----------
    // Три секции: скролл-контент, разделитель, футер
    Table main = new Table();
    main.setFillParent(true);
    main.add(scrollPane).expand().fill().row();
    main.add(footer).height(60).fillX();

    add(main).expand().fill();
  }

  // Эффект плавного появления
  public CardInfoWindow fadeIn() {
    getColor().a = 0f;
    addAction(Actions.fadeIn(0.2f));
    return this;
  }
}
