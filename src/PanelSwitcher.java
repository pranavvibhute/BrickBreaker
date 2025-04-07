import javax.swing.*;

public class PanelSwitcher {

    public static void switchPanel(JFrame frame, JPanel newPanel, String musicType) {
        // Stop current background music
        MusicPlayer.stopBackground();

        // Switch the panel
        frame.setContentPane(newPanel);
        frame.revalidate();
        frame.repaint();

        // Play new background music
        switch (musicType) {
            case MusicPlayer.START_MENU:
                MusicPlayer.playStartMenuMusic();
                break;
            case MusicPlayer.PAUSE_MENU:
                MusicPlayer.playPauseMenuMusic();
                break;
            case MusicPlayer.GAMEPLAY:
                MusicPlayer.playGameplayMusic();
                break;
            default:
                break;
        }
    }
}
