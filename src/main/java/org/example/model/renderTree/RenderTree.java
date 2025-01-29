package org.example.model.renderTree;

public class RenderTree {
    private RenderNode root;
    private int windowWidth = 1440;
    private int windowHeight = 900;

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

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void renderTree(RenderNode node) {
        if (node == null) {
            return;
        }
        System.out.println(node);
        for (RenderNode child : node.getChildren()) {
            renderTree(child);
        }
    }

    public void render() {
        System.out.println("Rendering the tree:");
        renderTree(root);
    }
}