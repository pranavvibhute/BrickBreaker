import java.awt.*;
import java.util.Random;

public class BossBrick extends Rectangle {
    private int hitsRemaining;
    private Image image;
    private int dx, dy; // movement direction
    private Random rand = new Random();
    private long lastHitTime = 0;
    private final int hitCooldown = 1000; // in milliseconds (1 sec)


    public BossBrick(int x, int y, int width, int height, int hits, Image image) {
        super(x, y, width, height);
        this.hitsRemaining = hits;
        this.image = image;
        dx = rand.nextBoolean() ? 2 : -2;
        dy = rand.nextBoolean() ? 1 : -1;
    }

    public void move(Component comp) {
        x += dx;
        y += dy;

        if (x <= 0 || x + width >= comp.getWidth()) dx *= -1;
        if (y <= 0 || y + height >= comp.getHeight() / 2) dy *= -1;
    }

    public void draw(Graphics g, Component c) {
        g.drawImage(image, x, y, width, height, c);
    }

    public boolean isHit() {
        hitsRemaining--;
        return hitsRemaining <= 0;
    }

    public boolean tryHit() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < hitCooldown) return false;

            lastHitTime = now;
            hitsRemaining--;
            return hitsRemaining <= 0;
    }
    
    public long getLastHitTime() {
        return lastHitTime;
    }

    public void setLastHitTime(long lastHitTime) {
        this.lastHitTime = lastHitTime;
    }

    public long getHitCooldown() {
        return hitCooldown;
    }

    public int getHitsRemaining() {
        return hitsRemaining;
    }
}
