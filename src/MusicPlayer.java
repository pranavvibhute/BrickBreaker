import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.sound.sampled.*;

public class MusicPlayer {

    // Constants for music identifiers
    public static final String START_MENU = "start";
    public static final String PAUSE_MENU = "pause";
    public static final String GAMEPLAY = "gameplay";

    private static final HashMap<String, Clip> bgmTracks = new HashMap<>();
    private static final HashMap<String, Clip> soundEffects = new HashMap<>();
    private static Clip currentBackground;

    /** Initialize and preload all background music and sound effects */
    public static void init() {
        // Load Sound Effects
        MusicPlayer.loadSoundEffect("pop", "resources/sfx/pop.wav");
        MusicPlayer.loadSoundEffect("break", "resources/sfx/break.wav");
        MusicPlayer.loadSoundEffect("powerup1", "resources/sfx/powerup1.wav");
        MusicPlayer.loadSoundEffect("powerup2", "resources/sfx/powerup2.wav");
        MusicPlayer.loadSoundEffect("powerup3", "resources/sfx/powerup3.wav");
        MusicPlayer.loadSoundEffect("gameover", "resources/sfx/gameover.wav");
        MusicPlayer.loadSoundEffect("boss-defeat", "resources/sfx/boss-defeat.wav");

        // Load Background Music
        MusicPlayer.loadBackgroundMusic(MusicPlayer.START_MENU, "resources/bgm/bgm2.wav");
        MusicPlayer.loadBackgroundMusic(MusicPlayer.PAUSE_MENU, "resources/bgm/bgm1.wav");
        MusicPlayer.loadBackgroundMusic(MusicPlayer.GAMEPLAY, "resources/bgm/bgm0.wav");

    }

    /** Load a background music track if not already loaded */
    private static void loadBackgroundMusic(String name, String filePath) {
        if (bgmTracks.containsKey(name)) return;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("❌ Music file not found: " + filePath);
                return;
            }

            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            bgmTracks.put(name, clip);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("⚠️ Failed to load BGM (" + name + "): " + e.getMessage());
        }
    }

    /** Play specified background music */
    private static void playBackground(String name) {
        stopBackground();

        Clip clip = bgmTracks.get(name);
        if (clip != null) {
            clip.setFramePosition(0); // Restart
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            currentBackground = clip;
        } else {
            System.err.println("⚠️ Music not loaded: " + name);
        }
    }

    /** Stop current background music without closing the clip */
    public static void stopBackground() {
        if (currentBackground != null && currentBackground.isRunning()) {
            currentBackground.stop();
            currentBackground.setFramePosition(0);
        }
        currentBackground = null;
    }

    /** Play background music for specific windows */
    public static void playStartMenuMusic() {
        playBackground(START_MENU);
    }

    public static void playPauseMenuMusic() {
        playBackground(PAUSE_MENU);
    }

    public static void playGameplayMusic() {
        playBackground(GAMEPLAY);
    }

    /** Pause current music */
    public static void pauseBackground() {
        if (currentBackground != null && currentBackground.isRunning()) {
            currentBackground.stop();
        }
    }

    /** Resume paused music */
    public static void resumeBackground() {
        if (currentBackground != null) {
            currentBackground.start();
        }
    }

    /** Load a sound effect if not already loaded */
    private static void loadSoundEffect(String name, String filePath) {
        if (soundEffects.containsKey(name)) return;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("❌ SFX file not found: " + filePath);
                return;
            }

            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            soundEffects.put(name, clip);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("⚠️ Failed to load SFX (" + name + "): " + e.getMessage());
        }
    }

    /** Play a loaded sound effect */
    public static void playSoundEffect(String name) {
        Clip clip = soundEffects.get(name);
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } else {
            System.err.println("⚠️ Sound effect not loaded: " + name);
        }
    }
}
