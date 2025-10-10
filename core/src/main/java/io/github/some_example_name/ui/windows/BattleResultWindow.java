package io.github.some_example_name.ui.windows;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.some_example_name.model.Player;
import com.badlogic.gdx.graphics.Color;

public class BattleResultWindow extends Window {

  public BattleResultWindow(String winner, Skin skin, Runnable onRestart) {
    super("Battle Result", skin);

    // Настройки окна
    setSize(400, 200);
    setModal(true);
    setMovable(false);
    setResizable(false);
    setColor(1, 1, 1, 0.95f);
    padTop(20).padBottom(20).padLeft(15).padRight(15);

    // Лейбл с информацией о победителе
    Label winnerLabel = new Label("Winner: " + winner, skin);
    winnerLabel.setColor(Color.YELLOW);
    winnerLabel.setFontScale(1.2f);

    // Кнопка рестарт
    TextButton restartButton = new TextButton("Restart Game", skin);
    restartButton.addListener(event -> {
      if (restartButton.isPressed()) {
        remove(); // закрываем модальное окно
        onRestart.run(); // вызываем логику рестарта
        return true;
      }
      return false;
    });

    // Компоновка окна
    Table mainTable = new Table();
    mainTable.top().pad(10);
    mainTable.add(winnerLabel).center().padBottom(20).row();
    mainTable.add(restartButton).center();

    add(mainTable).expand().fill();
  }
}
