package org.example.view;

import org.example.utils.Cursor;
import org.example.model.Model;
import org.example.model.html.HtmlElement;
import org.example.view.renderers.HtmlRenderer;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Canvas extends JPanel {
    private final Model model;
    private final Cursor cursor;
    private final HtmlRenderer htmlRenderer;

    public Canvas(Model model) {
        this.model = model;
        cursor = new Cursor(getWidth() > 0 ? getWidth() : 1400);

        htmlRenderer = new HtmlRenderer(cursor);
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        int width = getWidth() > 0 ? getWidth() : 1400;
        int height = Math.max(cursor.getY() + 50, getHeight());
        return new Dimension(width, height);
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        cursor.resetCursor(getWidth());
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        HtmlElement dom = model.parseHtml();
        htmlRenderer.renderElement(g2d, dom);
        revalidate();
    }
}
