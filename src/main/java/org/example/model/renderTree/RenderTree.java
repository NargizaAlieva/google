package org.example.model.renderTree;

public class RenderTree {
    private RenderNode root;
    private int windowWidth = 2000;
    private int windowHeight = 1000;

    public RenderTree() {
    }

    public RenderNode getRoot() {
        return root;
    }

    public void setRoot(RenderNode root) {
        this.root = root;
        this.root.setWidth(windowWidth);
        this.root.setHeight(windowHeight);
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        if (this.windowHeight < windowHeight)
            this.windowHeight = windowHeight;
    }
}