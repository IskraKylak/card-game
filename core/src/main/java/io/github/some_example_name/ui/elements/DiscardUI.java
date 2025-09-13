package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.model.Player;

public class DiscardUI extends Table {

  private final Player player;
  private final Label counter;

  public DiscardUI(Player player, Skin skin) {
    this.player = player;

    // картинка для отбоя
    Texture discardTexture = new Texture("game/discard.png");
    Image discardImage = new Image(discardTexture);

    float targetHeight = 120f;
    float aspect = (float) discardTexture.getWidth() / discardTexture.getHeight();
    discardImage.setScaling(Scaling.fit);

    // счетчик отбоя
    counter = new Label(String.valueOf(player.getDiscard().size()), skin);
    counter.setFontScale(1.2f);

    this.add(discardImage).size(targetHeight * aspect, targetHeight).row();
    this.add(counter).padTop(5);
  }

  public void update() {
    counter.setText(String.valueOf(player.getDiscard().size()));
  }
}
