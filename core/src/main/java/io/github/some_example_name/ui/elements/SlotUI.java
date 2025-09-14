package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.some_example_name.model.Slot;

public class SlotUI extends Table {
  private final Slot slot;
  private final Label contentLabel;

  public SlotUI(Slot slot, Skin skin) {
    this.slot = slot;

    // Фон берем из skin (если в uiskin.json есть "default-round")
    if (skin.has("default-round", com.badlogic.gdx.scenes.scene2d.utils.Drawable.class)) {
      this.setBackground(skin.getDrawable("default-round"));
    }

    contentLabel = new Label("Empty", skin);
    this.add(contentLabel).expand().center();
  }

  public void refresh() {
    if (slot.isOccupied()) {
      contentLabel.setText(slot.getUnit().getName());
    } else {
      contentLabel.setText("Empty");
    }
  }

  public Slot getSlot() {
    return slot;
  }
}
