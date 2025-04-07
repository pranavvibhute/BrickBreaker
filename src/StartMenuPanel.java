import java.awt.*;
import javax.swing.*;

public class StartMenuPanel extends JPanel {

    Image backgroundImage = Toolkit.getDefaultToolkit().getImage("assets\\start_menu_bg.png");
    private ImageIcon startIcon = new ImageIcon("assets\\Start_button.png");
    private ImageIcon instructionsIcon = new ImageIcon("assets\\Instructions_button.png");
    private ImageIcon exitIcon = new ImageIcon("assets\\Exit_button.png");

    // Pass MusicPlayer as a parameter to the constructor
    public StartMenuPanel(JFrame frame) {
        
        setLayout(null);
        setBackground(Color.BLACK);


        // Buttons
        JButton startBtn = new JButton(startIcon);
        JButton helpBtn = new JButton(instructionsIcon);
        JButton exitBtn = new JButton(exitIcon);

        // Position the buttons
        startBtn.setBounds(375, 300, 300, 80);
        helpBtn.setBounds(375, 333, 315, 150);
        exitBtn.setBounds(377, 400, 277, 190);

        // Button Actions
        startBtn.addActionListener(e -> {
            GamePanel gamePanel = new GamePanel(frame);
            PanelSwitcher.switchPanel(frame, gamePanel, MusicPlayer.GAMEPLAY);
            gamePanel.requestFocusInWindow();
        });

        helpBtn.addActionListener(e -> {
            String message = "<html>" +
            "<h2 style='color:#00BFFF;'>🎮 <u>Controls</u>:</h2>" +
            "<ul>" +
            "<li><b>Move Paddle</b>: ← → Arrow Keys</li>" +
            "<li><b>Launch Ball</b>: SPACE</li>" +
            "<li><b>Pause Game</b>: ESC</li>" +
            "</ul>" +
        
            "<h2 style='color:#32CD32;'>⚔️ <u>Gameplay Info</u>:</h2>" +
            "<ul>" +
            "<li><b>Destroy Bricks</b> to earn points (10 pts each)</li>" +
            "<li><b>Score +100</b> → Ball speed increases</li>" +
            "<li>🔄 Infinite levels with random brick layout</li>" +
            "<li>💥 Realistic paddle-ball collisions</li>" +
            "</ul>" +
        
            "<h2 style='color:#FFA500;'>✨ <u>Power-Ups</u>:</h2>" +
            "<ul>" +
            "<li>🧡 <b>Extra Life</b>: +1 life</li>" +
            "<li>💜 <b>Lose Life</b>: -1 life</li>" +
            "<li>⚡ <b>Multi-ball</b>: Split into multiple balls</li>" +
            "<li>📏 <b>Expand Paddle</b>: +50% width</li>" +
            "<li>📉 <b>Shrink Paddle</b>: -50% width</li>" +
            "<li>🌟 <b>Star Bonus</b>: +200 pts & wide paddle</li>" +
            "<li>🧲 <b>Magnet</b>: Attracts power-ups to paddle</li>" +
            "<li>🧲 <b>Magnetic Paddle</b>: Pulls balls toward paddle</li>" +
            "<li><i>Power-ups last ~5 seconds</i></li>" +
            "</ul>" +
        
            "<h2 style='color:#FF4500;'>👹 <u>Boss Fights</u>:</h2>" +
            "<ul>" +
            "<li>⚠️ Appears every 5 levels</li>" +
            "<li>🏹 Needs multiple hits to destroy</li>" +
            "<li>📦 Drops random power-ups on hit (cooldown based)</li>" +
            "<li>⏱️ Time-limited fight mode</li>" +
            "<li>🏆 Defeating Boss = +500 pts</li>" +
            "</ul>" +
        
            "<p><b>💥 Break bricks, grab power-ups, defeat bosses & enjoy infinite fun!</b></p>" +
            "</html>";
        
            JOptionPane.showMessageDialog(this, message, "How to Play", JOptionPane.INFORMATION_MESSAGE);
        });

        exitBtn.addActionListener(e -> System.exit(0));

        // Make buttons transparent (remove borders & background)
        for (JButton btn : new JButton[]{startBtn, helpBtn, exitBtn}) {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
        }

        // Add buttons to panel
        add(startBtn);
        add(helpBtn);
        add(exitBtn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
