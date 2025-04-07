import java.awt.*;

public class Ball extends Rectangle {
    private int speed = 7;   // Default speed
    private double directionX = 0.6; // Normalized direction (0 to 1)
    private double directionY = -0.6;

    Image image;

    public Ball(int x, int y, int width, int height, String imgPath) {
        super(x, y, width, height);
        image = Toolkit.getDefaultToolkit().getImage(imgPath);
        normalizeDirection();
        // updateVelocity();
    }

    public void draw(Graphics g, Component c) {
        g.drawImage(image, x, y, width, height, c);
    }

    public void move() {
        x += (int)(directionX * speed);
        y += (int)(directionY * speed);
    }

    public void reset() {
        x = 300;
        y = 300;
        speed = 5;
        directionX = 0.8;
        directionY = -0.6;
        normalizeDirection();
        // updateVelocity();
    }

    public void bounceHorizontal() {
        directionX = -directionX;
    }

    public void bounceVertical() {
        directionY = -directionY;
    }

    public void setSpeed(int newSpeed) {
        speed = newSpeed;
        // updateVelocity(); // Optional if dx/dy is used elsewhere
    }

    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }    

    public double getDirectionX() {
        return directionX;
    }
    
    public double getDirectionY() {
        return directionY;
    }

    public void setDirection(double dirX, double dirY) {
        this.directionX = dirX;
        this.directionY = dirY;
        normalizeDirection(); // Keeps direction unit-length
    }
    

    public int getSpeed() {
        return speed;
    }

    // Ensure (directionX, directionY) vector is unit length
    public void normalizeDirection() {
        double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
        if (magnitude != 0) {
            directionX /= magnitude;
            directionY /= magnitude;
        }
    }

//     private void updateVelocity() {
//         // For compatibility with old dx/dy methods if needed
//         dx = (int)(directionX * speed);
//         dy = (int)(directionY * speed);
//     }
}
