package io.github.some_example_name.ui.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class BuffEffectUI extends Actor {
  private Animation<TextureRegion> animation;
  private float stateTime = 0f;
  private Runnable onComplete;

  public BuffEffectUI(float centerX, float centerY, Runnable onComplete) {
    this.onComplete = onComplete;

    Array<TextureRegion> frames = new Array<>();
    for (int i = 1; i <= 24; i++) {
      Texture tex = new Texture(Gdx.files.internal("game/effects/buff_glow" + i + ".png"));
      frames.add(new TextureRegion(tex));
    }

    animation = new Animation<>(0.2f, frames, Animation.PlayMode.NORMAL);

    setSize(180, 140);
    setPosition(centerX - getWidth() / 2f, centerY - getHeight() / 2f);
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    stateTime += delta;

    // Ждём окончания анимации
    if (animation.isAnimationFinished(stateTime)) {
      remove();
      if (onComplete != null) {
        onComplete.run();
        onComplete = null; // чтобы случайно не вызвать дважды
      }
    }
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    TextureRegion frame = animation.getKeyFrame(stateTime);
    batch.draw(frame, getX(), getY(), getWidth(), getHeight());
  }
}
