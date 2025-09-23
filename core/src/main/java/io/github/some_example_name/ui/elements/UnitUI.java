package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.model.Unit;
import io.github.some_example_name.ui.effects.BuffEffectUI;

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

  // 🔹 иконки баффов/дебаффов
  private static final Texture BUFF_ICON = new Texture(Gdx.files.internal("game/icons/buff.png"));
  private static final Texture DEBUFF_ICON = new Texture(Gdx.files.internal("game/icons/debuff.png"));

  public UnitUI(Unit unit, Skin skin) {
    this.unit = unit;
    this.skin = skin;

    String spriteFolder = unit.getSpriteFolder();

    idleAnimation = loadAnimation(spriteFolder, "idle", 0.2f, Animation.PlayMode.LOOP);
    attackAnimation = loadAnimation(spriteFolder, "attack", 0.1f, Animation.PlayMode.NORMAL);
    deadAnimation = loadAnimation(spriteFolder, "dead", 0.1f, Animation.PlayMode.NORMAL);
    currentAnimation = idleAnimation;

    TextureRegion firstFrame = idleAnimation.getKeyFrame(0);
    unitImage = new Image(firstFrame);
    unitImage.setSize(150, 120);

    nameLabel = new Label(unit.getName(), skin);
    attackLabel = new Label("ATK: " + unit.getAttackPower(), skin);
    hpLabel = new Label("HP: " + unit.getHealth(), skin);

    this.add(unitImage).size(150, 120).row();
    this.add(nameLabel).padTop(2).row();
    this.add(attackLabel).padTop(2).row();
    this.add(hpLabel).padTop(2).row();
  }

  public void playBuffAnimation() {
    BuffEffectUI effect = new BuffEffectUI(getWidth(), getHeight());
    addActor(effect);
    effect.toFront();
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

  @Override
  public void draw(Batch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    // 🔹 Проверяем есть ли эффекты
    if (!unit.getStatusEffects().isEmpty()) {
      boolean hasBuff = unit.getStatusEffects().stream().anyMatch(e -> !e.isNegative());
      boolean hasDebuff = unit.getStatusEffects().stream().anyMatch(e -> e.isNegative());

      float iconSize = 24f;
      float padding = 4f;

      float startX = getX() + getWidth() - iconSize - padding;
      float iconY = getY() + getHeight() - iconSize - padding;

      if (hasBuff && hasDebuff) {
        // обе иконки рядом
        batch.draw(DEBUFF_ICON, startX - iconSize - 2, iconY, iconSize, iconSize);
        batch.draw(BUFF_ICON, startX, iconY, iconSize, iconSize);
      } else if (hasDebuff) {
        batch.draw(DEBUFF_ICON, startX, iconY, iconSize, iconSize);
      } else if (hasBuff) {
        batch.draw(BUFF_ICON, startX, iconY, iconSize, iconSize);
      }
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

  public void refresh() {
    nameLabel.setText(unit.getName());
    attackLabel.setText("ATK: " + unit.getAttackPower());
    hpLabel.setText("HP: " + unit.getHealth());

    if (!unit.isAlive()) {
      playDead();
    } else {
      if (currentAnimation != attackAnimation) {
        playIdle();
      }
    }
  }
}
