package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

public class UnitUI extends Actor {
  private Animation<TextureRegion> idleAnimation;
  private Animation<TextureRegion> attackAnimation;
  private Animation<TextureRegion> deathAnimation;

  private Animation<TextureRegion> currentAnimation;
  private float stateTime = 0f;

  public UnitUI(String spriteFolder) {
    // грузим кадры из папки
    idleAnimation = loadAnimation(spriteFolder, "idle", 0.2f, Animation.PlayMode.LOOP);
    attackAnimation = loadAnimation(spriteFolder, "attack", 0.1f, Animation.PlayMode.NORMAL);
    deathAnimation = loadAnimation(spriteFolder, "dead", 0.3f, Animation.PlayMode.NORMAL);

    currentAnimation = idleAnimation; // по умолчанию
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
      return null; // нет кадров

    Animation<TextureRegion> anim = new Animation<>(frameDuration, frames.toArray(new TextureRegion[0]));
    anim.setPlayMode(mode);
    return anim;
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    if (currentAnimation != null) {
      stateTime += delta;
    }
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    if (currentAnimation == null)
      return;

    TextureRegion frame = currentAnimation.getKeyFrame(stateTime);
    batch.draw(frame, getX(), getY(), getWidth(), getHeight());
  }

  public void playIdle() {
    currentAnimation = idleAnimation;
    stateTime = 0f;
  }

  public void playAttack() {
    currentAnimation = attackAnimation;
    stateTime = 0f;
  }

  public void playDeath() {
    currentAnimation = deathAnimation;
    stateTime = 0f;
  }
}
