package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.some_example_name.model.Slot;
import io.github.some_example_name.model.Unit;

public class SlotUI extends Table {
  private final Slot slot;
  private final Label contentLabel;
  private final Skin skin;

  public SlotUI(Slot slot, Skin skin) {
    this.slot = slot;
    this.skin = skin;

    // Фон (если есть в skin)
    if (skin.has("default-round", com.badlogic.gdx.scenes.scene2d.utils.Drawable.class)) {
      this.setBackground(skin.getDrawable("default-round"));
    }

    contentLabel = new Label("Empty", skin);
    this.add(contentLabel).expand().center();
  }

  /**
   * Возвращает Slot (модель).
   */
  public Slot getSlot() {
    return slot;
  }

  /**
   * Возвращает вложенный UnitUI если он есть, иначе null.
   */
  public io.github.some_example_name.ui.elements.UnitUI getUnitUI() {
    for (Actor a : this.getChildren()) {
      if (a instanceof io.github.some_example_name.ui.elements.UnitUI) {
        return (io.github.some_example_name.ui.elements.UnitUI) a;
      }
    }
    return null;
  }

  /**
   * Устанавливает отображение юнита внутри этого SlotUI. Если unit == null —
   * показывает "Empty" label. Если unit != null — создаёт UnitUI и помещает
   * внутрь.
   */
  public void setUnit(io.github.some_example_name.model.Unit unit) {
    this.clear(); // убираем старые элементы (label или UnitUI)
    if (unit != null) {
      UnitUI ui = new UnitUI(unit, skin);
      // добавляем UnitUI в слот (размеры задаются в родителе slotsRow)
      this.add(ui).expand().center();
    } else {
      // возвращаем пустой лейбл
      this.add(contentLabel).expand().center();
    }
  }

  /**
   * Быстрая очистка к состоянию "пустой слот".
   */
  public void clearToEmpty() {
    setUnit(null);
  }

  /**
   * Обновляет внутренности SlotUI: либо обновляет существующий UnitUI (если он
   * тот же юнит), либо заменяет содержимое на UnitUI/Empty в зависимости от
   * модели.
   */
  public void refresh() {
    if (slot.getUnit() != null) {
      UnitUI existing = getUnitUI();
      if (existing != null) {
        // если UnitUI есть — обновляем данные внутри
        if (existing.getEntity() == slot.getUnit()) {
          existing.refresh();
        } else {
          // в слоте другой юнит — заменяем
          setUnit(slot.getUnit());
        }
      } else {
        // был пустой — появился юнит
        setUnit(slot.getUnit());
      }
    } else {
      // модель говорит, что слот пуст — показываем пустой лейбл
      clearToEmpty();
    }
  }
}
