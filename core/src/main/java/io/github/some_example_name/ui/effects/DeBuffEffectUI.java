package io.github.some_example_name.ui.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class DeBuffEffectUI extends Actor {

  private Animation<TextureRegion> animation;
  private float stateTime = 0f;

  public DeBuffEffectUI(float centerX, float centerY) {
    // Загружаем кадры
    Array<TextureRegion> frames = new Array<>();
    for (int i = 1; i <= 18; i++) { // допустим 5 кадров: buff_glow1.png ... buff_glow5.png
      Texture tex = new Texture(Gdx.files.internal("game/effects/debuff" + i + ".png"));
      frames.add(new TextureRegion(tex));
    }

    animation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);

    setSize(450, 420); // размер эффекта
    setPosition(centerX - getWidth() / 2f - 20f, centerY - getHeight() / 2f + 20f);
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    stateTime += delta;

    if (animation.isAnimationFinished(stateTime)) {
      remove(); // удаляем после окончания анимации
    }
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    TextureRegion frame = animation.getKeyFrame(stateTime);
    batch.draw(frame, getX(), getY(), getWidth(), getHeight());
  }
}
