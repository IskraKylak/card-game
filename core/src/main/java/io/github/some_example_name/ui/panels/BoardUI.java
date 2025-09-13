package io.github.some_example_name.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Верхняя часть экрана (Board) ~70% высоты
 * Пустой, без слотов и юнитов
 */

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class BoardUI extends Table {

  public BoardUI() {
    Table table = this;
    table.top();

    // синий фон
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.BLUE);
    pixmap.fill();
    Texture texture = new Texture(pixmap);
    pixmap.dispose();

    table.setBackground(new TextureRegionDrawable(texture));
  }
}
