package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.*;

import io.github.some_example_name.model.Unit;

public class UnitUI extends EntityUI<Unit> {
  public UnitUI(Unit unit, Skin skin) {
    super(unit, skin, 125, 90); // размеры юнита
  }
}
