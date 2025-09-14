package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.some_example_name.core.GameEngine;
import io.github.some_example_name.model.Enemy;
import io.github.some_example_name.core.GameContext;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.model.data.DataPlayers;
import io.github.some_example_name.model.data.DataEnemy;
import io.github.some_example_name.ui.BattleScreenUI;

public class CardGame extends Game {

  @Override
  public void create() {
    // Создаём контекст
    // Создаём игрока
    Player player = DataPlayers.createLifePlayer();
    player.buildDefaultDeckFromFaction();
    player.buildBattleDeck();
    player.initBattleDeck();

    // Создаём врага
    Enemy enemy = DataEnemy.createOrc();

    // Контекст игры (например, 6 слотов)
    GameContext context = new GameContext(player, enemy);

    // Движок
    GameEngine engine = new GameEngine(context);

    // Загружаем skin
    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    // Передаём в UI уже весь контекст
    setScreen(new BattleScreenUI(context, engine, skin));
  }
}
