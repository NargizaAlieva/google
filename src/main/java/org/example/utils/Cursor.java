package org.example.utils;

public class Cursor {
    private int x, y = 10;
    private int maxWidth;
    private int lineHeight = 20;

    public Cursor(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void moveToNextLine() {
        y += lineHeight;
        x = 10;
    }

    public void updatePosition(int offsetX, int offsetY) {
        this.x += offsetX;
        this.y += offsetY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void resetCursor(int screenWidth) {
        this.y = 10;
        this.maxWidth = screenWidth - 30;
    }

    public int getMaxWidth() {
        return maxWidth;
    }
}
