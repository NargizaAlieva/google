package org.example.view;

import org.example.utils.Cursor;
import org.example.model.Model;
import org.example.model.html.HtmlElement;
import org.example.view.renderers.HtmlRenderer;
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
    private final Model model;
    private final Cursor cursor;
    private final HtmlRenderer htmlRenderer;
    private final Renderer renderer;
    private final List<LinkArea> linkAreas;

    public Canvas(Model model) {
        this.model = model;
        cursor = new Cursor(getWidth() > 0 ? getWidth() : 1400);
        renderer = new Renderer(model);
        htmlRenderer = new HtmlRenderer(cursor);
        setBackground(Color.WHITE);
        linkAreas = new ArrayList<>();
    }

    @Override
    public Dimension getPreferredSize() {
        int width = getWidth() > 0 ? getWidth() : 1400;
        int height = Math.max(cursor.getY() + 50, getHeight());
        return new Dimension(width, height);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        cursor.resetCursor(getWidth());
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        linkAreas.clear();
        renderer.renderElement(g2d, model.parse());
        //htmlRenderer.renderElement(g2d, dom);
        updateLinkAreas();
        revalidate();
    }

    public HtmlRenderer getHtmlRenderer() {
        return htmlRenderer;
    }

    private void updateLinkAreas() {
        linkAreas.clear();
        linkAreas.addAll(renderer.getLinkAreas());
    }

    public List<LinkArea> getLinkAreas() {
        return linkAreas;
    }
}
