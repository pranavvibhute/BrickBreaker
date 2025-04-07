import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class PowerUp extends Rectangle {
    String type;
    Image image;
    int speed = 4;
    long duration;
    long activationTime;


    public PowerUp(int x, int y, String type, String imagePath) {
        super(x, y, 30, 30);
        this.type = type;
        image = Toolkit.getDefaultToolkit().getImage(imagePath);
        this.activationTime = System.currentTimeMillis();
        this.duration = 5000; // 5 seconds duration
    }

    public void move() {
        y += speed;
    }

    public void draw(Graphics g, GamePanel panel) {
        g.drawImage(image, x, y, width, height, panel);
    }

    public boolean intersectsPaddle(Paddle paddle) {
        return this.intersects(paddle);
    }

    public String getType() {
        return type;
    }

    public boolean isPositive() {
        return type.equals("multiball") || type.equals("expand") || 
               type.equals("magnet") || type.equals("slowdown");
    }

    public boolean isNegative() {
        return type.equals("notlife") || type.equals("shrink") || 
               type.equals("not-magnet") || type.equals("speedup");
    }

    public boolean isActive() {
        return System.currentTimeMillis() - activationTime < duration;
    }
}
