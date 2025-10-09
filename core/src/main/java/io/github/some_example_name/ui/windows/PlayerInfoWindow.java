package io.github.some_example_name.ui.windows;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.status.StatusEffect;

import java.util.List;

public class PlayerInfoWindow extends Window {

  public PlayerInfoWindow(Player entity, Skin skin) {
    super("Player Info", skin);

    // Настройки окна
    setSize(420, 520);
    setModal(true);
    setMovable(false);
    setResizable(false);
    setColor(1, 1, 1, 0.95f);
    padTop(40).padBottom(20).padLeft(15).padRight(15);

    // Основная информация
    Label nameLabel = new Label(entity.getName(), skin, "default");
    Label statsLabel = new Label(
        "HP: " + entity.getHealth() + "/" + entity.getMaxHealth(),
        skin, "default");

    Texture texture = new Texture(entity.getSpriteFolder() + "/player1.png");
    Image entityImage = new Image(new TextureRegionDrawable(texture));
    // entityImage.setSize(60, 60);

    // Контент с бафами/дебафами
    Table effectsTable = new Table(skin);
    effectsTable.defaults().pad(2);

    List<StatusEffect> effects = entity.getStatusEffects();
    if (effects.isEmpty()) {
      effectsTable.add(new Label("No active effects", skin)).left().row();
    } else {
      for (StatusEffect effect : effects) {
        String type = effect.isNegative() ? "❌ Debuff" : "✨ Buff";
        String text = type + " — " + effect.getName()
            + " (" + effect.getDuration() + " turns left)";
        effectsTable.add(new Label(text, skin)).left().row();
      }
    }

    // Скролл только на контент
    Table scrollContent = new Table();
    scrollContent.defaults().pad(8);

    // только один раз добавляем картинку!
    scrollContent.add(entityImage).size(200, 150).padBottom(10).row();
    scrollContent.add(statsLabel).padBottom(10).row();
    scrollContent.add(new Label("Active Effects:", skin, "default")).padBottom(5).left().row();
    scrollContent.add(effectsTable).left().row();

    ScrollPane scrollPane = new ScrollPane(scrollContent, skin);
    scrollPane.setFadeScrollBars(false);
    scrollPane.setScrollingDisabled(true, false);
    scrollPane.setForceScroll(false, true);

    // Футер с кнопкой
    TextButton closeButton = new TextButton("Close", skin);
    closeButton.addListener(event -> {
      if (closeButton.isPressed()) {
        remove();
        return true;
      }
      return false;
    });

    Table footer = new Table();
    footer.add(closeButton).center();

    // Компоновка окна: фиксированный header, скрол контент, фиксированный footer
    Table mainTable = new Table();
    mainTable.top().pad(10);
    mainTable.add(nameLabel).center().row();
    mainTable.add(scrollPane).grow().padBottom(10).row();
    mainTable.add(footer).bottom().fillX();

    add(mainTable).expand().fill();
  }
}
