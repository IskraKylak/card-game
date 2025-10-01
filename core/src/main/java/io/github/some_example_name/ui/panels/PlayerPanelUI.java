package io.github.some_example_name.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.core.GameEngine;
import io.github.some_example_name.model.Card;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.ui.BattleScreenUI;
import io.github.some_example_name.ui.elements.CardActor;
import io.github.some_example_name.ui.elements.DeckUI;
import io.github.some_example_name.ui.elements.DiscardUI;
import io.github.some_example_name.ui.elements.HandUI;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class PlayerPanelUI extends Table {

  private final GameContext context;
  private final HandUI handUI;
  private final DeckUI deckUI;
  private final DiscardUI discardUI;

  private final GameEngine engine;
  private final BattleScreenUI battleScreenUI;

  public PlayerPanelUI(GameContext context, GameEngine engine, Skin skin, BattleScreenUI battleScreenUI) {
    this.context = context;
    this.engine = engine;
    this.battleScreenUI = battleScreenUI;

    // получаем текущего игрока из контекста
    Player player = context.getPlayer();

    // зелёный фон
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.GREEN);
    pixmap.fill();
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    this.setBackground(new TextureRegionDrawable(texture));

    // Подпанели (все работают через игрока, которого берём из контекста)
    deckUI = new DeckUI(player, skin);
    handUI = new HandUI(player, skin, battleScreenUI);
    discardUI = new DiscardUI(player, skin);

    // Верхняя строка (колода - рука - отбой)
    Table topRow = new Table();
    topRow.add(deckUI).left().pad(10);
    topRow.add(handUI).expandX().center();
    topRow.add(discardUI).right().pad(10);

    // Добавляем всё в PlayerPanel
    this.add(topRow).expand().fill().row();

  }

  public CardActor findCardActor(Card card) {
    for (Actor actor : handUI.getChildren()) {
      if (actor instanceof CardActor cardActor) {
        if (cardActor.getCard() == card) {
          return cardActor;
        }
      }
    }
    return null;
  }

  // метод для обновления UI после изменения модели
  public void refresh() {
    // всегда берём игрока из контекста
    Player player = context.getPlayer();

    handUI.update();
    deckUI.update(); // можно добавить при динамическом отображении
    discardUI.update(); // аналогично
  }
}
