package org.example.view;

import org.example.model.Model;
import org.example.model.renderTree.RenderTree;
import org.example.view.renderers.LinkArea;
import org.example.view.renderers.Renderer;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Canvas extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Model model;
    private RenderTree renderTree;
    private final Renderer renderer;
    private final List<LinkArea> linkAreas;
    private int scrollOffsetY;
    private int maxScrollOffsetY;


    public Canvas(Model model) {
        this.model = model;

        renderer = new Renderer(model);
        linkAreas = new ArrayList<>();
        scrollOffsetY = 0;

    }

    @Override
    public Dimension getPreferredSize() {
        int width = getWidth() > 0 ? getWidth() : 1400;
        if (renderTree != null)
            renderTree.setWindowWidth(width);
        int height = Math.max(renderer.getCanvasHeight() + 50, getHeight());
        return new Dimension(width, height);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.translate(0, -scrollOffsetY);
        if (model.getHttpResponse() != null) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            linkAreas.clear();

            if (model.getIsUrlChanged())
                renderTree = model.parse();
            renderer.renderElement(g2d, renderTree);

            updateLinkAreas();
            updateMaxScrollOffset();

            revalidate();
        }
    }

    private void updateLinkAreas() {
        linkAreas.clear();
        linkAreas.addAll(renderer.getLinkAreas());
    }

    private void updateMaxScrollOffset() {
        int contentHeight = renderer.getCanvasHeight();
        int visibleHeight = getHeight();
        maxScrollOffsetY = Math.max(0, contentHeight - visibleHeight);
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public int getMaxScrollOffsetY() {
        return maxScrollOffsetY;
    }

    public int getScrollOffsetY() {
        return scrollOffsetY;
    }

    public void setScrollOffsetY(int scrollOffsetY) {
        this.scrollOffsetY = scrollOffsetY;
    }
}
