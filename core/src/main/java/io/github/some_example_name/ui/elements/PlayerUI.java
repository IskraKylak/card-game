package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.ui.panels.BoardUI;

import java.util.ArrayList;
import java.util.List;

public class PlayerUI extends Table {

  private final Player player;
  private final Skin skin;

  private Animation<TextureRegion> idleAnimation;
  private Animation<TextureRegion> currentAnimation;
  private float stateTime = 0f;

  private Image playerImage;
  private Label nameLabel;
  private Label hpLabel;

  public PlayerUI(Player player, Skin skin, String spriteFolder, float spriteWidth, float spriteHeight) {
    this.player = player;
    this.skin = skin;

    idleAnimation = loadAnimation(spriteFolder, "Player", 0.2f, Animation.PlayMode.LOOP);
    currentAnimation = idleAnimation;

    TextureRegion firstFrame = idleAnimation != null ? idleAnimation.getKeyFrame(0) : null;
    playerImage = new Image(firstFrame);
    playerImage.setSize(spriteWidth, spriteHeight);

    nameLabel = new Label(player.getName(), skin);
    hpLabel = new Label("HP: " + player.getHealth(), skin);

    this.add(playerImage).size(spriteWidth, spriteHeight).row();
    // this.add(nameLabel).padTop(2).row();
    // this.add(hpLabel).padTop(2).row();
  }

  private Animation<TextureRegion> loadAnimation(String folder, String prefix, float frameDuration,
      Animation.PlayMode mode) {
    List<TextureRegion> frames = new ArrayList<>();
    int i = 1;
    while (Gdx.files.internal(folder + "/" + prefix + i + ".png").exists()) {
      Texture texture = new Texture(Gdx.files.internal(folder + "/" + prefix + i + ".png"));
      frames.add(new TextureRegion(texture));
      i++;
    }
    if (frames.isEmpty())
      return null;
    Animation<TextureRegion> anim = new Animation<>(frameDuration, frames.toArray(new TextureRegion[0]));
    anim.setPlayMode(mode);
    return anim;
  }

  public Vector2 getCenterInBoard(BoardUI boardUI) {
    return localToActorCoordinates(boardUI, new Vector2(getWidth() / 2f, getHeight() / 2f));
  }

  public void playHit() {
    this.addAction(Actions.sequence(
        Actions.color(Color.RED, 0.1f), // быстрый красный
        Actions.color(Color.WHITE, 0.1f) // возврат в норму
    ));
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    if (currentAnimation != null) {
      stateTime += delta;
      playerImage.setDrawable(new TextureRegionDrawable(currentAnimation.getKeyFrame(stateTime)));
    }
  }

  public void refresh() {
    hpLabel.setText("HP: " + player.getHealth());
  }

  public void playIdle() {
    currentAnimation = idleAnimation;
    stateTime = 0f;
  }

  public Player getPlayer() {
    return player;
  }
}
