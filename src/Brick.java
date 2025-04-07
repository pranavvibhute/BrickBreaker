import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Brick extends Rectangle {
    private int hitsRequired;
    private int hitsTaken;
    private Image normalImage;
    private Image crackedImage;

    public Brick(int x, int y, int width, int height, int hitsRequired, String normalPath, String crackedPath) {
        super(x, y, width, height);
        this.hitsRequired = hitsRequired;
        this.hitsTaken = 0;
        this.normalImage = Toolkit.getDefaultToolkit().getImage(normalPath);
        this.crackedImage = Toolkit.getDefaultToolkit().getImage(crackedPath);
    }

    public void draw(Graphics g, Component c) {
        if (hitsRequired == 2 && hitsTaken == 1) {
            g.drawImage(crackedImage, x, y, width, height, c); // Show cracked after 1 hit
        } else {
            g.drawImage(normalImage, x, y, width, height, c);  // Normal for new or 1-hit bricks
        }
    }

    public boolean hit() {
        hitsTaken++;
        return hitsTaken >= hitsRequired;
    }
}
