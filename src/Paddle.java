import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Paddle extends Rectangle {

    Image pic;
    private boolean magpadActive = false;
    private Image normalImage;
    private Image magneticImage;

    public void setMagPadActive(boolean active) {
        this.magpadActive = active;
    }
    
    public boolean isMagPadActive() {
        return magpadActive;
    }

    public Paddle(int a, int b, int w, int h){
        x = a;
        y = b;
        width = w;
        height = h;
        normalImage = Toolkit.getDefaultToolkit().getImage("assets\\paddle.png");
        magneticImage = Toolkit.getDefaultToolkit().getImage("assets\\magnet-paddle.png");
    }

    public void reset(){
        x = 500;
        y = 650;
    }

    public void draw(Graphics g, Component c){
        if (isMagPadActive()) {
            g.drawImage(magneticImage, x, y, width, height, c);
        } else {
            g.drawImage(normalImage, x, y, width, height, c);
        }
    }
}
