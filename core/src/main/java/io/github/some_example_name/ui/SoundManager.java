package io.github.some_example_name.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
  private static final Map<String, Sound> sounds = new HashMap<>();

  public static void load() {
    sounds.put("spell", Gdx.audio.newSound(Gdx.files.internal("music/defolt-effect/magic.mp3")));
    sounds.put("attack", Gdx.audio.newSound(Gdx.files.internal("music/defolt-effect/attack.mp3")));
    sounds.put("card-play", Gdx.audio.newSound(Gdx.files.internal("music/defolt-effect/cart-play.mp3")));
    sounds.put("buff", Gdx.audio.newSound(Gdx.files.internal("music/defolt-effect/buff.mp3")));
    sounds.put("debuff", Gdx.audio.newSound(Gdx.files.internal("music/defolt-effect/debuff.mp3")));
    // sounds.put("death",
    // Gdx.audio.newSound(Gdx.files.internal("music/defolt-effect/death.mp3")));
    sounds.put("default", Gdx.audio.newSound(Gdx.files.internal("music/defolt-effect/1.mp3")));
  }

  public static void play(String name) {
    Sound sound = sounds.get(name);
    if (sound != null)
      sound.play(1.0f);
    else
      sounds.get("default").play(0.5f);
  }

  public static void register(String name, String path) {
    sounds.put(name, Gdx.audio.newSound(Gdx.files.internal(path)));
  }

  public static void dispose() {
    for (Sound s : sounds.values())
      s.dispose();
    sounds.clear();
  }
}
