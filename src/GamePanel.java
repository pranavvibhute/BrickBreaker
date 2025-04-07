import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;


public class GamePanel extends JPanel implements KeyListener {

    JFrame frame;
    ArrayList<Ball> balls = new ArrayList<>();
    ArrayList<PowerUp> powerUps = new ArrayList<>();
    Paddle paddle;
    Anim animate;
    Thread thread;
    BrickMap brickMap;
    Image background;
    Image heart;

    boolean moveLeft = false;
    boolean moveRight = false;
    boolean isGameOver = false;
    boolean isPaused = false;

    long paddleEffectEndTime = 0;
    boolean isExpanded = false;
    boolean isShrunk = false;

    private int lastSpeedIncreaseLevel = 0;
    private double lastDirectionX = 0.8;
    private double lastDirectionY = -0.6;
    private int lastSpeed = 5;
    private boolean magnetActive = false;
    private boolean not_magnetActive = false;
    private long magnetEndTime = 0;
    private long not_magnetEndTime = 0;
    private long magpadEndTime = 0;

    // Screen shake variables
    private int shakeDuration = 0;
    private int shakeMagnitude = 3;


    // Boss fight variables
    BossBrick bossBrick = null;
    boolean bossFight = false;
    long bossStartTime;
    int bossTimeLimit = 60; // seconds



    int paddleSpeed = 13;
    int score = 0;
    int lives = 3;
    int level = 1;

    Font hudFont = new Font("Arial", Font.BOLD, 24);
    PauseMenuPanel pauseMenu;

    public GamePanel(JFrame frame) {
        

        this.frame = frame;
        background = Toolkit.getDefaultToolkit().getImage("assets\\background.jpg");
        heart = Toolkit.getDefaultToolkit().getImage("assets\\heart.png");

        paddle = new Paddle(500, 650, 200, 25);
        brickMap = new BrickMap(3, 6, 1024, 768, this);

        Ball ball = new Ball(300, 300, 20, 20, "assets\\gold_ball.png");
        balls.add(ball);
        addKeyListener(this);
        setFocusable(true);
    }

    
    public void setPaused(boolean value) {
        isPaused = value;
    }

    public void addPauseMenu(JFrame frame) {
        pauseMenu = new PauseMenuPanel(this, frame);
        setLayout(null);
        pauseMenu.setBounds(0, 0, getWidth(), getHeight());
        this.add(pauseMenu);
        repaint();
        MusicPlayer.stopBackground();
        MusicPlayer.playPauseMenuMusic();
    }

    public void removePauseMenu() {
        this.remove(pauseMenu);
        this.revalidate();
        this.repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // Lives
        int heartWidth = 30, heartHeight = 30, heartPadding = 10;
        int startX = getWidth() - (lives * (heartWidth + heartPadding)) - 10;
        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, startX + i * (heartWidth + heartPadding), 10, heartWidth, heartHeight, this);
        }

         // Apply screen shake offset if active
        if (shakeDuration > 0) {
            int offsetX = (int) (Math.random() * shakeMagnitude * 2 - shakeMagnitude);
            int offsetY = (int) (Math.random() * shakeMagnitude * 2 - shakeMagnitude);
            g.translate(offsetX, offsetY);
            shakeDuration--;
    }

        // Draw the boss brick if in boss fight
        if (bossFight) {
            bossBrick.draw(g, this);
            long elapsed = (System.currentTimeMillis() - bossStartTime) / 1000;
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Boss Time Left: " + (bossTimeLimit - elapsed), 20, 60);
            g.drawString("Boss Hits Left: " + bossBrick.getHitsRemaining(), 20, 80);
        }
    

        // // Draw power-ups
        //  for (PowerUp p : powerUps) {
        //     p.draw(g, this); // Assuming you have a method to draw the power-up image
        // }

        brickMap.draw(g, this);
        paddle.draw(g, this);
        for (Ball b : balls) b.draw(g, this);
        synchronized (powerUps) {
            for (PowerUp p : new ArrayList<>(powerUps)) {
                p.draw(g, this);
            }
        }
        

        g.setFont(hudFont);
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 20, 30);
        g.setColor(Color.GREEN);
        g.drawString("LEVEL: " + level, 460, 30);
    }

    // Game Over method
    public void gameOver() {
        // Show current score in the pop-up window

        MusicPlayer.stopBackground();
        MusicPlayer.playSoundEffect("gameover");

        String message = "Game Over! Your Score: " + score;

        // Custom option dialog with "Return to Menu" button
        Object[] options = {"Return to Menu"};
        int choice = JOptionPane.showOptionDialog(
                this, 
                message, 
                "Game Over", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                options, 
                options[0]
        );

        // If the player presses "Return to Menu"
        if (choice == 0) {
            // Go back to the start menu
            PanelSwitcher.switchPanel(frame, new StartMenuPanel(frame), MusicPlayer.START_MENU);
            requestFocusInWindow();  
        }
    }

    
    public void update() {
        if (!isPaused && !isGameOver) {

            boolean bossHitThisFrame = false;
            List<Ball> toRemove = new ArrayList<>();
            for (Ball b : balls) {
                b.move();

                if (bossFight && bossBrick != null && b.intersects(bossBrick)) {

                    if (!bossHitThisFrame) {
                        Rectangle ballRect = b.getBounds();
                        Rectangle bossRect = bossBrick.getBounds();
                
                        int ballCenterX = ballRect.x + ballRect.width / 2;
                        int ballCenterY = ballRect.y + ballRect.height / 2;
                
                        int bossLeft = bossRect.x;
                        int bossRight = bossRect.x + bossRect.width;
                        int bossTop = bossRect.y;
                        int bossBottom = bossRect.y + bossRect.height;
                
                        boolean hitFromLeftOrRight = ballCenterX <= bossLeft || ballCenterX >= bossRight;
                        boolean hitFromTopOrBottom = ballCenterY <= bossTop || ballCenterY >= bossBottom;
                
                        // Bounce based on collision side
                        if (hitFromLeftOrRight) {
                            b.bounceHorizontal();
                            MusicPlayer.playSoundEffect("break");
                            //push ball outside boss brick horizontally
                            if (ballCenterX <= bossLeft) {
                                b.x = bossLeft - b.width; // Move ball to the left of the boss brick
                            } else {
                                b.x = bossRight + bossBrick.width; // Move ball to the right of the boss brick
                            }
                        } else if (hitFromTopOrBottom) {
                            b.bounceVertical();
                            MusicPlayer.playSoundEffect("break");
                            if (ballCenterY < bossBrick.y) {
                                b.y = bossBrick.y - b.height - 1 ;
                            } else {
                                b.y = bossBrick.y + bossBrick.height + 1;
                            }
                        } else {
                            b.bounceVertical(); // fallback
                        }
                
                         // Damage only if cooldown passed
                            long now = System.currentTimeMillis();
                            if (now - bossBrick.getLastHitTime() >= bossBrick.getHitCooldown()) {
                                boolean isDestroyed = bossBrick.tryHit();  // Just reduces hits and returns destroyed state
                                bossBrick.setLastHitTime(now);
                            
                                // Drop power-up on each valid hit
                                PowerUpType powerUpType = getRandomPowerUp(powerUpTypes2);
                                PowerUp p = new PowerUp(bossBrick.x + bossBrick.width / 2, bossBrick.y, powerUpType.type, powerUpType.image);
                                synchronized (powerUps) {
                                    powerUps.add(p);
                                }

                                // If destroyed
                                if (isDestroyed) {
                                    bossBrick = null;
                                    bossFight = false;
                                    score += 500;
                                    MusicPlayer.playSoundEffect("boss-defeat");
                                    checkLevelClear();
                                }
                            }
                
                        bossHitThisFrame = true; // prevent multiple hits this frame
                    }
                }
                


                if (paddle.isMagPadActive() && System.currentTimeMillis() > magpadEndTime) {
                    paddle.setMagPadActive(false);

                    if (Math.abs(b.getDirectionY()) < 0.2) {
                        b.setDirection(b.getDirectionX(), -0.6); // Give some downward angle
                    }
            
                    b.normalizeDirection(); 
                }

                if (paddle.isMagPadActive()) {
                    double attractionStrength = 0.15;  // Tweak this for stronger or weaker pull
                    double centerPaddle = paddle.getX() + paddle.getWidth() / 2.0;
                    double centerBall = b.getX() + b.getWidth() / 2.0;
                
                    double diff = centerPaddle - centerBall;
                    double directionX = b.getDirectionX() + diff * attractionStrength / getWidth();
                
                    // Limit the directionX to prevent erratic movement
                    double maxDir = 1.0;
                    directionX = Math.max(-maxDir, Math.min(maxDir, directionX));
                
                    // Recalculate directionY to maintain unit vector
                    double directionY = Math.sqrt(1 - directionX * directionX);
                    directionY = b.getDirectionY() < 0 ? -directionY : directionY;
                
                    b.setDirection(directionX, directionY);
                }

                // Wall bounce
                
                if(b.x <= 0) {
                    b.setX(1);
                    b.bounceHorizontal();
                }      
                if(b.x + b.width >= getWidth()) {
                    b.setX(getWidth() - b.width - 1);
                    b.bounceHorizontal();
                }
                if(b.y <= 0) {
                    b.setY(1);
                    b.bounceVertical();
                }

                // Paddle bounce (angular bounce)
                if (b.intersects(paddle)) {
                    // b.bounceVertical();
                    MusicPlayer.playSoundEffect("pop");

                    double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
                    double ballCenter = b.getX() + b.getWidth() / 2.0;
                    double relativeIntersect = (ballCenter - paddleCenter) / (paddle.getWidth() / 2.0);
                
                    // Clamp between -1 and 1 just in case
                    relativeIntersect = Math.max(-1, Math.min(1, relativeIntersect));
                
                    double bounceAngle = relativeIntersect * (Math.PI / 3); // Max 60 degrees angle
                
                    // Update direction
                    double directionX = Math.sin(bounceAngle);
                    double directionY = -Math.cos(bounceAngle); // Negative because upward
                
                    b.setDirection(directionX, directionY);
                }
               

                // Bottom wall
                if (b.y + b.height >= getHeight()) {
                    lastDirectionX = b.getDirectionX();
                    lastDirectionY = b.getDirectionY();
                    lastSpeed = b.getSpeed();
                    toRemove.add(b);
                }
            }

            balls.removeAll(toRemove);

            if (balls.isEmpty()) {
                lives--;
                if (lives <= 0) {
                    isGameOver = true;
                    thread = null;
                    // Delay before showing game over
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);  // Wait for 1 second before showing Game Over
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gameOver(); // Show the Game Over popup after 1 second
                }).start();
                } 
                else {
                    Ball newBall = new Ball(300, 300, 20, 20, "assets\\gold_ball.png");
                    newBall.setSpeed(lastSpeed);
                    double randomness = (Math.random() * 0.4) - 0.2; // range -0.2 to 0.2
                    newBall.setDirection(lastDirectionX + randomness, lastDirectionY);
                    balls.add(newBall);
                }
            }

            // Paddle movement
                // Smooth paddle movement
                if (moveLeft && paddle.x > 0) {
                    paddle.x -= paddleSpeed;
                }
                if (moveRight && paddle.x < getWidth() - paddle.width) {
                    paddle.x += paddleSpeed;
                }

            // Paddle effects expire
            if (System.currentTimeMillis() > paddleEffectEndTime) {
                if (isExpanded || isShrunk) {
                    paddle.width = 200;
                    isExpanded = isShrunk = false;
                }
            }

            checkBrickCollision();
            
       // Boss fight logic
            if (bossFight && bossBrick != null) {
                bossBrick.move(this);
            }

            if (bossFight) {
                long elapsed = (System.currentTimeMillis() - bossStartTime) / 1000;
                if (elapsed > bossTimeLimit) {
                    isGameOver = true;
                    bossFight = false;
                    MusicPlayer.playSoundEffect("gameover");
                }
            }       
            
            

            int maxSpeed = 15;
            if (level % 3 == 0 && level != lastSpeedIncreaseLevel) {
                for (Ball b : balls) {
                    if (b.getSpeed() < maxSpeed) {
                        b.setSpeed(b.getSpeed() + 1); // Increase only if under the cap
                    }
                }
                lastSpeedIncreaseLevel = level;
            }

            synchronized (powerUps) {
                ArrayList<PowerUp> caught = new ArrayList<>();
                for (PowerUp p : new ArrayList<>(powerUps)) {
                    p.move();
                    if (p.intersectsPaddle(paddle)) {
                        activatePowerUp(p.type);
                        caught.add(p);
                    } else if (p.y > getHeight()) {
                        caught.add(p);
                    }
                }
                powerUps.removeAll(caught);
            }
            
        }

        if (magnetActive && System.currentTimeMillis() > magnetEndTime) {
            magnetActive = false;
        }
        if (magnetActive) {
            for (PowerUp p : powerUps) {
                if (p.isPositive()) {
                    double diffX = paddle.getCenterX() - p.getCenterX();
                    double diffY = paddle.getCenterY() - p.getCenterY();
                    double distance = Math.sqrt(diffX * diffX + diffY * diffY);
                    double speed = 2.0;
        
                    if (distance > 0) {
                        p.x += (diffX / distance) * speed;
                        p.y += (diffY / distance) * speed;
                    }
                }
            }
        }

        if (not_magnetActive && System.currentTimeMillis() > magnetEndTime) {
            not_magnetActive = false;
        }
        if (not_magnetActive) {
            for (PowerUp p : powerUps) {
                if (p.isNegative()) {
                    double diffX = paddle.getCenterX() - p.getCenterX();
                    double diffY = paddle.getCenterY() - p.getCenterY();
                    double distance = Math.sqrt(diffX * diffX + diffY * diffY);
                    double speed = 3.0;
        
                    if (distance > 0) {
                        p.x += (diffX / distance) * speed;
                        p.y += (diffY / distance) * speed;
                    }
                }
            }
        }

        repaint();
    }

    public void deactivatePowerUp(String type) {
        switch (type) {
            case "expand":
                isExpanded = false;
                paddle.width = 200; // Reset to original width
                break;
            case "shrink":
                isShrunk = false;
                paddle.width = 200; // Reset to original width
                break;
        }
    }

    public boolean areAllBricksCleared() {
        for (int r = 0; r < brickMap.rows; r++) {
            for (int c = 0; c < brickMap.cols; c++) {
                if (brickMap.bricks[r][c] != null) return false;
            }
        }
        return true;
    }

    public class PowerUpType {
        String type;
        String image;
        double weight;
    
        PowerUpType(String type, String image, double weight) {
            this.type = type;
            this.image = image;
            this.weight = weight;
        }
    }

    List<PowerUpType> powerUpTypes = List.of(
        new PowerUpType("expand", "assets\\expand.png", 0.10), // 10% chance for "expand"
        new PowerUpType("multiball", "assets\\multiball.png", 0.19), // 19% chance for "multiball"
        new PowerUpType("life", "assets\\heart.png", 0.05), // 5% chance for "life"
        new PowerUpType("notlife", "assets\\notheart.png", 0.15), // 15% chance for "notlife"
        new PowerUpType("star_bonus", "assets\\star.png", 0.04), // 4% chance for "star_bonus"
        new PowerUpType("shrink", "assets\\shrink.png", 0.15), // 15% chance for "shrink"
        new PowerUpType("magnet", "assets\\magnet.png", 0.10), // 10% chance for "magnet"
        new PowerUpType("not-magnet", "assets\\not-magnet.png", 0.15),// 10% chance for "not-magnet"
        new PowerUpType("magnet-paddle", "assets\\mag-pad.png", 0.05)// 5% chance for "magnet-paddle"
    );

    // powerups for boss fight
    List<PowerUpType> powerUpTypes2 = List.of(
        new PowerUpType("expand", "assets\\expand.png", 0.20), // 20% chance for "expand"
        new PowerUpType("multiball", "assets\\multiball.png", 0.15), // 15% chance for "multiball"
        new PowerUpType("notlife", "assets\\notheart.png", 0.10), // 10% chance for "notlife"
        new PowerUpType("shrink", "assets\\shrink.png", 0.17), // 17% chance for "shrink"
        new PowerUpType("not-magnet", "assets\\not-magnet.png", 0.10),// 10% chance for "not-magnet"
        new PowerUpType("add-time", "assets\\clock.png", 0.15)// 15% chance for "add-time"
    );

    private PowerUpType getRandomPowerUp(List<PowerUpType> types) {
        double totalWeight = 0;
        for (PowerUpType pt : types) {
            totalWeight += pt.weight;
        }
    
        double r = Math.random() * totalWeight;
        double cumulative = 0.0;
        for (PowerUpType pt : types) {
            cumulative += pt.weight;
            if (r <= cumulative) {
                return pt;
            }
        }
        return types.get(types.size() - 1); // fallback in case of precision errors
    }
    

    public void checkBrickCollision() {
        for (Ball b : balls) {
            outer:
            for (int r = 0; r < brickMap.rows; r++) {
                for (int c = 0; c < brickMap.cols; c++) {
                    Brick brick = brickMap.bricks[r][c];
                    if (brick != null && b.intersects(brick)) {

                        boolean destroyed = brick.hit(); // Reduce hit count
                        b.bounceVertical(); // Bounce on hit
                        MusicPlayer.playSoundEffect("break");
    
                        if (destroyed) {
                            brickMap.bricks[r][c] = null; // Only remove if destroyed
                            score += 10;
                            shakeDuration = 5; // Set shake duration
                            checkLevelClear(); // Check if level is cleared

                            // Normal level
                                if (Math.random() < 0.3) {
                                    PowerUpType powerUpType = getRandomPowerUp(powerUpTypes);
                                    PowerUp p = new PowerUp(brick.x + brick.width / 2, brick.y, powerUpType.type, powerUpType.image);
                                    powerUps.add(p);
                                }

                            break outer;

                        }
                    }
                }
            }
        }
    }

    public void checkLevelClear() {
        if (areAllBricksCleared() && !bossFight) {
            level++;
                if (level % 5 == 0) {
                    bossFight = true;
                    bossStartTime = System.currentTimeMillis();
                    int calculateHits = 6 + 2*(level / 5); // Boss hits based on level
                    bossTimeLimit = 30 + (level / 5) * 3; // Boss time limit based on level
                    Image bossImage = Toolkit.getDefaultToolkit().getImage("assets\\boss-brick.png");
                    bossBrick = new BossBrick(200, 100, 300, 200, calculateHits, bossImage); // scales with level

                }
                else {
                     // 🎲 Random rows and cols
                        int rows = 3 + (int)(Math.random() * 4);  // 3 to 6
                        int cols = 5 + (int)(Math.random() * 5);  // 5 to 9

                    brickMap = new BrickMap(rows, cols, 1024, 768, this);
                }
        }
    }
    

    public void activatePowerUp(String type) {
        switch (type) {
            case "expand":
                if (!isExpanded) {
                    MusicPlayer.playSoundEffect("powerup2");
                    paddle.width += 50;
                    isExpanded = true;
                    paddleEffectEndTime = System.currentTimeMillis() + 7000;
                }
                break;

            case "shrink":
                MusicPlayer.playSoundEffect("powerup1");
                if (!isShrunk) {
                    paddle.width = Math.max(50, paddle.width - 50);
                    isShrunk = true;
                    paddleEffectEndTime = System.currentTimeMillis() + 18000;
                }
                break;
            case "life":
                MusicPlayer.playSoundEffect("powerup3");
                if (lives < 5) {
                    lives++;
                }
                break;

                case "notlife":
                    MusicPlayer.playSoundEffect("powerup1");
                    lives--;
                    if (lives <= 0) {
                        isGameOver = true;
                        if(isGameOver) {
                            gameOver();
                        }
                    }
                break;

            case "multiball":
                MusicPlayer.playSoundEffect("powerup2");
                Ball refBall = balls.get(0);
                Ball newBall = new Ball(paddle.x + paddle.width / 2, paddle.y - 20, 20, 20, "assets\\multiball.png");
                newBall.setSpeed(refBall.getSpeed());
                newBall.setDirection(-refBall.getDirectionX(), refBall.getDirectionY());
                balls.add(newBall);
                break;
            
            case "star_bonus":
                MusicPlayer.playSoundEffect("powerup3");
                score += 200; // Add bonus points
                if (!isExpanded) {
                    paddle.width += 150;
                    isExpanded = true;
                    paddleEffectEndTime = System.currentTimeMillis() + 12000;
                }
                break;

            case "magnet":
                MusicPlayer.playSoundEffect("powerup2");
                magnetActive = true;
                magnetEndTime = System.currentTimeMillis() + 7000; // 10 seconds duration
                break;
            
            case "not-magnet":
                MusicPlayer.playSoundEffect("powerup1");
                not_magnetActive = true;
                not_magnetEndTime = System.currentTimeMillis() + 10000; // 10 seconds duration
                break;

            case "magnet-paddle":
                MusicPlayer.playSoundEffect("powerup2");
                paddle.setMagPadActive(true);
                magpadEndTime = System.currentTimeMillis() + 11000; // 11 seconds duration
                break;
            
            case "add-time":
                MusicPlayer.playSoundEffect("powerup3");
                if (bossFight) {
                    bossTimeLimit += 20; // Add 20 seconds to boss time limit
                }
                break;
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (!isGameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (!isPaused) {
                    setPaused(true);
                    addPauseMenu((JFrame) SwingUtilities.getWindowAncestor(this));
                }
            }

            if (!isPaused) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && thread == null) {
                    animate = new Anim(this);
                    thread = new Thread(animate);
                    thread.start();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT ) {
                    // paddle.x -= paddleSpeed;
                    moveLeft = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT ) {
                    // paddle.x += paddleSpeed;
                    moveRight = true;
                }
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_Q) {
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) moveLeft = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) moveRight = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

}
