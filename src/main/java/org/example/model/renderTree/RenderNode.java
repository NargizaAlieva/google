package org.example.model.renderTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RenderNode {
    private String tagName;
    private List<RenderNode> children;
    private String textContent;
    private HashMap<String, String> appliedStyles; // Все применённые стили.
    private int x, y, width, height;
    private RenderNode parent;

    public RenderNode(String tagName) {
        this.tagName = tagName;
        this.children = new ArrayList<>();
        this.appliedStyles = new HashMap<>();
    }

    public void addChild(RenderNode child) {
        this.children.add(child);
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setStyles(HashMap<String, String> parentStyles) {
        if (parentStyles != null) {
            appliedStyles.putAll(parentStyles);
        }
    }

    public void setParent(RenderNode parent) {
        this.parent = parent;
    }

    public RenderNode getParent() {
        return parent;
    }

    public HashMap<String, String> getAppliedStyles() {
        return appliedStyles;
    }

    public String getTagName() {
        return tagName;
    }

    public List<RenderNode> getChildren() {
        return children;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "RenderNode{" +
                "tagName='" + tagName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", textContent='" + textContent + '\'' +
                ", appliedStyles=" + appliedStyles +
                '}';
    }
}
