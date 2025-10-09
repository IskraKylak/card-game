package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class BattleMusic {
  private Music music;

  public void play() {
    if (music == null) {
      music = Gdx.audio.newMusic(Gdx.files.internal("music/battle-music.mp3"));
      music.setLooping(true); // 🔁 зацикливаем
      music.setVolume(0.5f); // 🔊 громкость от 0 до 1
    }
    music.play();
  }

  public void stop() {
    if (music != null)
      music.stop();
  }

  public void dispose() {
    if (music != null) {
      music.dispose();
      music = null;
    }
  }
}
