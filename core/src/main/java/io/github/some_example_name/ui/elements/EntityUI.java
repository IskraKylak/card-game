package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.model.CombatEntity;
import io.github.some_example_name.model.Entity;
import io.github.some_example_name.ui.effects.BuffEffectUI;
import io.github.some_example_name.ui.panels.BoardUI;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityUI<T extends CombatEntity> extends Table {
  protected final T entity;
  protected final Skin skin;

  protected Animation<TextureRegion> idleAnimation;
  protected Animation<TextureRegion> attackAnimation;
  protected Animation<TextureRegion> hitAnimation;
  protected Animation<TextureRegion> deadAnimation;
  protected Animation<TextureRegion> currentAnimation;

  protected float stateTime = 0f;

  protected Image entityImage;
  protected Label nameLabel;
  protected Label attackLabel;
  protected Label hpLabel;

  // 🔹 иконки эффектов
  private static final Texture BUFF_ICON = new Texture(Gdx.files.internal("game/icons/buff.png"));
  private static final Texture DEBUFF_ICON = new Texture(Gdx.files.internal("game/icons/debuff.png"));

  public EntityUI(T entity, Skin skin, float spriteWidth, float spriteHeight) {
    this.entity = entity;
    this.skin = skin;

    String spriteFolder = entity.getSpriteFolder();

    idleAnimation = loadAnimation(spriteFolder, "idle", 0.2f, Animation.PlayMode.LOOP);
    attackAnimation = loadAnimation(spriteFolder, "attack", 0.2f, Animation.PlayMode.NORMAL);
    deadAnimation = loadAnimation(spriteFolder, "dead", 0.1f, Animation.PlayMode.NORMAL);
    currentAnimation = idleAnimation;

    TextureRegion firstFrame = idleAnimation != null ? idleAnimation.getKeyFrame(0) : null;
    entityImage = new Image(firstFrame);
    entityImage.setSize(spriteWidth, spriteHeight);

    nameLabel = new Label(entity.getName(), skin);
    attackLabel = new Label("ATK: " + entity.getAttackPower(), skin);
    hpLabel = new Label("HP: " + entity.getHealth(), skin);

    this.add(entityImage).size(spriteWidth, spriteHeight).row();
    this.add(nameLabel).padTop(2).row();
    this.add(attackLabel).padTop(2).row();
    this.add(hpLabel).padTop(2).row();
  }

  public Vector2 getCenterInBoard(BoardUI boardUI) {
    return localToActorCoordinates(boardUI, new Vector2(getWidth() / 2f, getHeight() / 2f));
  }

  // --- Методы анимаций ---
  public void playIdle() {
    currentAnimation = idleAnimation;
    stateTime = 0f;
  }

  public void playAttack() {
    currentAnimation = attackAnimation;
    stateTime = 0f;
  }

  public void playDead() {
    if (deadAnimation != null) {
      currentAnimation = deadAnimation;
      stateTime = 0f;
    }
  }

  public void playHit() {
    if (hitAnimation == null)
      hitAnimation = idleAnimation;
    currentAnimation = hitAnimation;
    stateTime = 0f;
  }

  public void playBuffAnimation() {
    BuffEffectUI effect = new BuffEffectUI(getWidth(), getHeight());
    addActor(effect);
    effect.toFront();
  }

  public void playDebuffAnimation() {
    BuffEffectUI effect = new BuffEffectUI(getWidth(), getHeight());
    addActor(effect);
    effect.toFront();
  }

  // --- Центр для эффектов ---
  public float getCenterX() {
    return getX() + getWidth() / 2f;
  }

  public float getCenterY() {
    return getY() + getHeight() / 2f;
  }

  // --- Обновление UI ---
  public void refresh() {
    nameLabel.setText(entity.getName());
    attackLabel.setText("ATK: " + entity.getAttackPower());
    hpLabel.setText("HP: " + entity.getHealth());
  }

  // --- Рисование эффектов ---
  @Override
  public void draw(Batch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    if (!entity.getStatusEffects().isEmpty()) {
      boolean hasBuff = entity.getStatusEffects().stream().anyMatch(e -> !e.isNegative());
      boolean hasDebuff = entity.getStatusEffects().stream().anyMatch(e -> e.isNegative());

      float iconSize = 24f;
      float padding = 4f;

      float startX = getX() + getWidth() - iconSize - padding;
      float iconY = getY() + getHeight() - iconSize - padding;

      if (hasBuff && hasDebuff) {
        batch.draw(DEBUFF_ICON, startX - iconSize - 2, iconY, iconSize, iconSize);
        batch.draw(BUFF_ICON, startX, iconY, iconSize, iconSize);
      } else if (hasDebuff) {
        batch.draw(DEBUFF_ICON, startX, iconY, iconSize, iconSize);
      } else if (hasBuff) {
        batch.draw(BUFF_ICON, startX, iconY, iconSize, iconSize);
      }
    }
  }

  // --- Логика анимаций ---
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
      entityImage.setDrawable(new TextureRegionDrawable(currentAnimation.getKeyFrame(stateTime)));
    }
  }

  public T getEntity() {
    return entity;
  }
}
