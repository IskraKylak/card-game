package io.github.some_example_name.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.some_example_name.model.Player;
import io.github.some_example_name.ui.panels.BoardUI;
import io.github.some_example_name.ui.panels.PlayerPanelUI;

import com.badlogic.gdx.scenes.scene2d.ui.Value;

public class BattleScreenUI extends ScreenAdapter {

  private Stage stage;
  private BoardUI boardUI;
  private PlayerPanelUI playerPanelUI;
  private Player player; // добавляем ссылку на игрока

  public BattleScreenUI(Player player, Skin skin) {
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);

    this.player = player;

    boardUI = new BoardUI();
    playerPanelUI = new PlayerPanelUI(player, skin);

    Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    // Делаем деление экрана примерно 70/30
    root.add(boardUI).expandX().fillX().top().height(Gdx.graphics.getHeight() * 0.7f).row();
    root.add(playerPanelUI).expandX().fillX().bottom().height(Gdx.graphics.getHeight() * 0.3f);
  }

  @Override
  public void render(float delta) {
    // Очищаем экран
    Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
