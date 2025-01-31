package org.example.model.renderTree;

import java.util.ArrayList;
import java.util.HashMap;

public class RenderNode {
    private String tagName;
    private ArrayList<RenderNode> children;
    private String textContent;
    private HashMap<String, String> appliedStyles;
    private double x, y, height, width;
    private RenderNode parent;

    public RenderNode(String tagName) {
        this.tagName = tagName;
        this.textContent = "";
        this.children = new ArrayList<>();
        this.appliedStyles = new HashMap<>();
        this.height = 0;
        this.width = 0;
        this.x = 10;
        this.y = 10;
    }
    public void setHeight(double height) {
        this.height = height;
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

    public ArrayList<RenderNode> getChildren() {
        return children;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
