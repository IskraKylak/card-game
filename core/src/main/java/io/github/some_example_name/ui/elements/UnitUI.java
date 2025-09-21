package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.model.Unit;

import java.util.ArrayList;
import java.util.List;

public class UnitUI extends Table {
  private final Unit unit;
  private final Skin skin;

  private Animation<TextureRegion> idleAnimation;
  private Animation<TextureRegion> attackAnimation;
  private Animation<TextureRegion> currentAnimation;
  private Animation<TextureRegion> deadAnimation;
  private float stateTime = 0f;

  private Image unitImage;
  private Label nameLabel;
  private Label attackLabel;
  private Label hpLabel;

  public UnitUI(Unit unit, Skin skin) {
    this.unit = unit;
    this.skin = skin;

    String spriteFolder = unit.getSpriteFolder();

    idleAnimation = loadAnimation(spriteFolder, "idle", 0.2f, Animation.PlayMode.LOOP);
    attackAnimation = loadAnimation(spriteFolder, "attack", 0.1f, Animation.PlayMode.NORMAL);
    deadAnimation = loadAnimation(spriteFolder, "dead", 0.1f, Animation.PlayMode.NORMAL);
    currentAnimation = idleAnimation;

    // Берем первый кадр для Image
    TextureRegion firstFrame = idleAnimation.getKeyFrame(0);
    unitImage = new Image(firstFrame);
    unitImage.setSize(150, 120);

    // Лейблы
    nameLabel = new Label(unit.getName(), skin);
    attackLabel = new Label("ATK: " + unit.getAttack(), skin);
    hpLabel = new Label("HP: " + unit.getHealth(), skin);

    // Добавляем в Table: спрайт сверху, лейблы снизу
    this.add(unitImage).size(150, 120).row();
    this.add(nameLabel).padTop(2).row();
    this.add(attackLabel).padTop(2).row();
    this.add(hpLabel).padTop(2).row();
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

  @Override
  public void act(float delta) {
    super.act(delta);
    if (currentAnimation != null) {
      stateTime += delta;
      unitImage.setDrawable(new TextureRegionDrawable(currentAnimation.getKeyFrame(stateTime)));
    }
  }

  public void playIdle() {
    currentAnimation = idleAnimation;
    stateTime = 0f;
  }

  public void playAttack() {
    currentAnimation = attackAnimation;
    stateTime = 0f;
  }

  public void playDead() {
    currentAnimation = deadAnimation;
    stateTime = 0f;
  }

  public Unit getUnit() {
    return unit;
  }
}
