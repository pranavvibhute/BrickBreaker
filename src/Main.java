import javax.swing.JFrame;

public class Main{
    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker");
        // GamePanel panel = new GamePanel();
        // frame.add(panel);
        StartMenuPanel menu = new StartMenuPanel(frame);
        frame.setContentPane(menu);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(1024, 768);
        frame.setResizable(false);
        frame.setLocation(300, 50);

        MusicPlayer.init(); // Initialize the music player
         // Play Start Menu Music
         MusicPlayer.playStartMenuMusic();
    }

}
