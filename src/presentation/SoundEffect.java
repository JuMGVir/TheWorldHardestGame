package presentation;

import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundEffect {

    private Clip clip;
    private FloatControl volumeControl;

    private static final Preferences PREFS = Preferences.userNodeForPackage(SoundEffect.class);
    private static final String MUSIC_VOL_KEY = "musicVolume";
    private static final String SFX_VOL_KEY = "sfxVolume";
    private static final float DEFAULT_MUSIC_VOL = 0.75f;
    private static final float DEFAULT_SFX_VOL = 0.75f;

    private SoundEffect(String path) {
        try {
            URL url = SoundEffect.class.getResource(path);
            if (url == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(ais);
            try {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            } catch (IllegalArgumentException e) {
                volumeControl = null;
            }
        } catch (UnsupportedAudioFileException | IOException
                | LineUnavailableException | IllegalArgumentException e) {
            clip = null;
        }
    }

    public void setVolume(float level) {
        if (clip == null || volumeControl == null) return;
        if (level <= 0f) { level = 0f; }
        if (level > 1f) { level = 1f; }
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        float db = (level - 1f) * Math.abs(min);
        db = Math.max(min, Math.min(max, db));
        volumeControl.setValue(db);
    }

    public void play() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public static final SoundEffect COIN       = new SoundEffect("/SOUND/1-coin-collected.wav");
    public static final SoundEffect DEATH      = new SoundEffect("/SOUND/2-death.wav");
    public static final SoundEffect THEME      = new SoundEffect("/SOUND/3-game-theme.wav");
    public static final SoundEffect NEXT_LEVEL = new SoundEffect("/SOUND/4-next-level.wav");
    public static final SoundEffect SFX        = new SoundEffect("/SOUND/5-sfx.wav");

    public static void setMusicVolume(float level) {
        PREFS.putFloat(MUSIC_VOL_KEY, level);
        THEME.setVolume(level);
    }

    public static void setSFXVolume(float level) {
        PREFS.putFloat(SFX_VOL_KEY, level);
        COIN.setVolume(level);
        DEATH.setVolume(level);
        NEXT_LEVEL.setVolume(level);
        SFX.setVolume(level);
    }

    public static float getMusicVolume() {
        return PREFS.getFloat(MUSIC_VOL_KEY, DEFAULT_MUSIC_VOL);
    }

    public static float getSFXVolume() {
        return PREFS.getFloat(SFX_VOL_KEY, DEFAULT_SFX_VOL);
    }

    public static void loadVolumes() {
        setMusicVolume(getMusicVolume());
        setSFXVolume(getSFXVolume());
    }
}
