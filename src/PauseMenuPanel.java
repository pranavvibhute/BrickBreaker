import java.awt.*;
import javax.swing.*;

public class PauseMenuPanel extends JPanel {

    Image backgroundImage = Toolkit.getDefaultToolkit().getImage("assets\\pause_menu_bg.png");
    private GamePanel gamePanel; // Reference to the GamePanel
    private JFrame frame; // Reference to the JFrame


    // Pass GamePanel and MusicPlayer as parameters to the constructor
    public PauseMenuPanel(GamePanel gamePanel, JFrame frame) {
       
        setLayout(null);
        setOpaque(false); // Make background transparent if needed

        this.gamePanel = gamePanel; // Store the reference to GamePanel 
        this.frame = frame; // Store the reference to JFrame

        // Button icons
        ImageIcon resumeIcon = new ImageIcon("assets\\Resume_button.png");
        ImageIcon menuIcon = new ImageIcon("assets\\Menu_button.png");
        ImageIcon exitIcon = new ImageIcon("assets\\Exit_button.png");

        JButton resumeBtn = new JButton(resumeIcon);
        JButton menuBtn = new JButton(menuIcon);
        JButton exitBtn = new JButton(exitIcon);

        // Positioning buttons
        resumeBtn.setBounds(360, 250, 300, 150);
        menuBtn.setBounds(360, 370, 300, 150);
        exitBtn.setBounds(370, 385, 300, 200);

        for (JButton btn : new JButton[]{resumeBtn, menuBtn, exitBtn}) {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
        }

        // Button actions
        resumeBtn.addActionListener(e -> {
            gamePanel.setPaused(false);
            gamePanel.removePauseMenu();
            gamePanel.requestFocusInWindow();
            MusicPlayer.stopBackground();
            MusicPlayer.playGameplayMusic();
        });

        menuBtn.addActionListener(e -> {
            StartMenuPanel startMenu = new StartMenuPanel(frame);
            PanelSwitcher.switchPanel(frame, startMenu, MusicPlayer.START_MENU);
            startMenu.requestFocusInWindow();
        });

        exitBtn.addActionListener(e -> System.exit(0));

        add(resumeBtn);
        add(menuBtn);
        add(exitBtn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
