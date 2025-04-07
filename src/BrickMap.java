import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class BrickMap {

    public Brick[][] bricks;

    int rows, cols;
    int brickWidth, brickHeight;
    int hGap = 10;
    int vGap = 10;
    int topMargin = 50;
    int leftMargin;

    ArrayList<String> normalImagePaths = new ArrayList<>();
    ArrayList<String> crackedImagePaths = new ArrayList<>();

    Component comp;

    public BrickMap(int rows, int cols, int panelWidth, int panelHeight, Component comp) {
        this.rows = rows;
        this.cols = cols;
        this.bricks = new Brick[rows][cols];
        this.comp = comp;

        brickWidth = (panelWidth - ((cols + 1) * hGap)) / cols;
        brickHeight = 40;

        int totalGridWidth = (brickWidth + hGap) * cols;
        leftMargin = (panelWidth - totalGridWidth) / 2;

        loadImagePaths();
        generateBricks();
    }

    private void loadImagePaths() {
        // Add matching normal and cracked image paths
        normalImagePaths.add("assets\\red_tile.png");
        crackedImagePaths.add("assets\\red_tile_cracked.png");

        normalImagePaths.add("assets\\yellow_tile.png");
        crackedImagePaths.add("assets\\yellow_tile_cracked.png");

        normalImagePaths.add("assets\\orange_tile.png");
        crackedImagePaths.add("assets\\orange_tile_cracked.png");

        normalImagePaths.add("assets\\blue_tile.png");
        crackedImagePaths.add("assets\\blue_tile_cracked.png");

        normalImagePaths.add("assets\\light_blue_tile.png");
        crackedImagePaths.add("assets\\light_blue_tile_cracked.png");

        normalImagePaths.add("assets\\green_tile.png");
        crackedImagePaths.add("assets\\green_tile_cracked.png");

        normalImagePaths.add("assets\\light_green_tile.png");
        crackedImagePaths.add("assets\\light_green_tile_cracked.png");

        normalImagePaths.add("assets\\gray_tile.png");
        crackedImagePaths.add("assets\\gray_tile_cracked.png");

        normalImagePaths.add("assets\\brown_tile.png");
        crackedImagePaths.add("assets\\brown_tile_cracked.png");

        normalImagePaths.add("assets\\purple_tile.png");
        crackedImagePaths.add("assets\\purple_tile_cracked.png");
    }

    private void generateBricks() {
        Random rand = new Random();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = leftMargin + col * (brickWidth + hGap);
                int y = topMargin + row * (brickHeight + vGap);

                int index = rand.nextInt(normalImagePaths.size());
                String normalPath = normalImagePaths.get(index);
                String crackedPath = crackedImagePaths.get(index);

                int hitsRequired = rand.nextBoolean() ? 1 : 2; // Randomly decide hit count

                bricks[row][col] = new Brick(x, y, brickWidth, brickHeight, hitsRequired, normalPath, crackedPath);
            }
        }
    }

    public void draw(Graphics g, Component c) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (bricks[row][col] != null) {
                    bricks[row][col].draw(g, c);
                }
            }
        }
    }

    public void breakBrick(int row, int col) {
        bricks[row][col] = null;
    }
}
