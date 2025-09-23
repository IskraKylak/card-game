package io.github.some_example_name.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.some_example_name.model.Enemy;

import java.util.ArrayList;
import java.util.List;

public class EnemyUI extends Table {
  private final Enemy enemy;
  private final Skin skin;

  private Animation<TextureRegion> idleAnimation;
  private Animation<TextureRegion> attackAnimation;
  private Animation<TextureRegion> currentAnimation;
  private float stateTime = 0f;

  private Image unitImage;
  private Label nameLabel;
  private Label attackLabel;
  private Label hpLabel;

  private static final Texture BUFF_ICON = new Texture(Gdx.files.internal("game/icons/buff.png"));
  private static final Texture DEBUFF_ICON = new Texture(Gdx.files.internal("game/icons/debuff.png"));

  public EnemyUI(Enemy enemy, Skin skin) {
    this.enemy = enemy;
    this.skin = skin;

    String spriteFolder = enemy.getSpriteFolder();

    idleAnimation = loadAnimation(spriteFolder, "idle", 0.1f, Animation.PlayMode.LOOP);
    attackAnimation = loadAnimation(spriteFolder, "attack", 0.05f, Animation.PlayMode.NORMAL);
    currentAnimation = idleAnimation;

    // Берем первый кадр для Image
    TextureRegion firstFrame = idleAnimation.getKeyFrame(0);
    unitImage = new Image(firstFrame);
    unitImage.setSize(240, 260);

    // Лейблы
    nameLabel = new Label(enemy.getName(), skin);
    attackLabel = new Label("ATK: " + enemy.getAttackPower(), skin);
    hpLabel = new Label("HP: " + enemy.getHealth(), skin);

    // Добавляем в Table: спрайт сверху, лейблы снизу
    this.add(unitImage).size(240, 260).row();
    this.add(nameLabel).padTop(2).row();
    this.add(attackLabel).padTop(2).row();
    this.add(hpLabel).padTop(2).row();
  }

  public void refreshHP() {
    hpLabel.setText("HP: " + enemy.getHealth());
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    // 🔹 Отображение эффектов
    if (!enemy.getStatusEffects().isEmpty()) {
      boolean hasBuff = enemy.getStatusEffects().stream().anyMatch(e -> !e.isNegative());
      boolean hasDebuff = enemy.getStatusEffects().stream().anyMatch(e -> e.isNegative());

      float iconSize = 24f;
      float padding = 4f;

      float startX = getX() + getWidth() - iconSize - padding - 20f;
      float iconY = getY() + getHeight() - iconSize - padding - 50f;

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

  public Enemy getEnemy() {
    return enemy;
  }

  /**
   * Обновляет отображение врага по данным из модели.
   */
  public void refresh() {
    nameLabel.setText(enemy.getName());
    attackLabel.setText("ATK: " + enemy.getAttackPower());
    hpLabel.setText("HP: " + enemy.getHealth());

    // Если враг мертв — скрываем его
    if (!enemy.isAlive()) {
      this.setVisible(false);
    } else {
      this.setVisible(true);
    }
  }
}
