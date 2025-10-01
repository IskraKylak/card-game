package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.*;

import io.github.some_example_name.model.Enemy;

public class EnemyUI extends EntityUI<Enemy> {
  public EnemyUI(Enemy enemy, Skin skin) {
    super(enemy, skin, 240, 260); // размеры врага
  }
}
