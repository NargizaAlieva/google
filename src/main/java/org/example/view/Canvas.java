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

    public Canvas(Model model) {
        this.model = model;

        renderer = new Renderer(model);
        linkAreas = new ArrayList<>();
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

        if (model.getHttpResponse() != null) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            linkAreas.clear();

            renderTree = model.parse();
            renderer.renderElement(g2d, renderTree);

            updateLinkAreas();
            revalidate();
        }
    }

    public Renderer getRenderer() {
        return renderer;
    }

    private void updateLinkAreas() {
        linkAreas.clear();
        linkAreas.addAll(renderer.getLinkAreas());
    }
}
