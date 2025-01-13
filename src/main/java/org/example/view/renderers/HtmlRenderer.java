package org.example.view.renderers;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.example.model.html.HtmlElement;
import org.example.utils.Cursor;

public class HtmlRenderer {
    private final Cursor cursor;
    private Graphics2D g2d;
    private List<LinkArea> linkAreas;
    private Map<String, Consumer<HtmlElement>> renderers;

    public HtmlRenderer(Cursor cursor) {
        this.cursor = cursor;
        this.linkAreas = new ArrayList<>();
        createRenderers();
    }

    public void renderElement(Graphics2D g2d, HtmlElement dom) {
        this.g2d = g2d;
        renderElement(dom, dom.getTag());
    }

    private void renderElement(HtmlElement element, String parentTag) {
        String tagToUse = element.getTag().equals("text") ? parentTag : element.getTag();
        System.out.println(tagToUse);
        renderers.getOrDefault(tagToUse, this::renderDefault).accept(element);

        if (element.getContent().isEmpty() && !element.getChildren().isEmpty() && !element.getTag().equals("ul")) {
            for (HtmlElement child : element.getChildren()) {
                renderElement(child, element.getTag());
            }
        }
    }

    private void createRenderers() {
        renderers = new HashMap<>();

        renderers.put("h1", element -> renderText(element,"Serif", Font.BOLD, 32, Color.BLACK, 5));
        renderers.put("h2", element -> renderText(element, "Serif", Font.BOLD, 28, Color.BLACK, 4));
        renderers.put("h3", element -> renderText(element, "Serif", Font.BOLD, 24, Color.BLACK, 3));
        renderers.put("p", element -> renderText(element, "Serif", Font.PLAIN, 16, Color.DARK_GRAY, 1));
        renderers.put("span", element -> renderInlineText(element, "Serif", Font.PLAIN, 16, Color.BLACK));
        renderers.put("a", this::renderLink);
        renderers.put("ul", element -> renderList(element, "•"));
        renderers.put("li", element -> renderInlineText(element, "Serif", Font.PLAIN, 16, Color.BLACK));
        renderers.put("div", element -> renderDiv(10, 400));
        renderers.put("nav", element -> renderDiv(10, 400));
        renderers.put("strong", element -> renderInlineText(element, "Serif", Font.BOLD, 16, Color.BLACK));
        renderers.put("em", element -> renderInlineText(element, "Serif", Font.ITALIC, 16, Color.BLACK));
        renderers.put("img", this::renderImage);
    }

    private void renderText(HtmlElement element, String fontName, int fontStyle, int fontSize, Color color, int yIncrement) {
        g2d.setFont(new Font(fontName, fontStyle, fontSize));
        g2d.setColor(color);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(element.getContent());

        cursor.updatePosition(0, yIncrement);

        g2d.drawString(element.getContent(), cursor.getX(), cursor.getY());
        cursor.updatePosition(textWidth, 0);

        cursor.updatePosition(0, yIncrement);

        if (element.getContent().isEmpty())
            cursor.moveToNextLine();
    }

    private void renderInlineText(HtmlElement element, String fontName, int fontStyle, int fontSize, Color color) {
        g2d.setFont(new Font(fontName, fontStyle, fontSize));
        g2d.setColor(color);
        FontMetrics fm = g2d.getFontMetrics();

        String content = element.getContent();

        StringBuilder lineBuilder = new StringBuilder();
        for (char c : content.toCharArray()) {
            lineBuilder.append(c);
            int lineWidth = fm.stringWidth(lineBuilder.toString());

            if (cursor.getX() + lineWidth > cursor.getMaxWidth()) {
                g2d.drawString(lineBuilder.toString(), cursor.getX(), cursor.getY());
                cursor.moveToNextLine();
                lineBuilder.setLength(0);
                lineBuilder.append(c);
            }
        }

        if (!lineBuilder.isEmpty()) {
            g2d.drawString(lineBuilder.toString().trim(), cursor.getX(), cursor.getY());
            cursor.updatePosition(fm.stringWidth(lineBuilder.toString()), 0);
        }
    }

    private void renderLink(HtmlElement element) {
        String link = element.getContent();

        while (!element.getTag().equals("text") && !element.getTag().equals("img") && element.getChildren().size() == 1) {
            element = element.getChildren().get(0);
        }

        if (element.getTag().equals("text")) {
            renderTextLink(element.getContent(), link);
        }
        else if (element.getTag().equals("img")) {
            renderImageLink(element, link);
        }
    }

    private void renderTextLink(String text, String linkContent) {
        g2d.setColor(Color.BLUE);
        Font underlineFont = g2d.getFont().deriveFont(Font.PLAIN);
        g2d.setFont(underlineFont);

        FontMetrics fm = g2d.getFontMetrics();

        g2d.drawString(text, cursor.getX(), cursor.getY());
        g2d.drawLine(cursor.getX(), cursor.getY() + 2, cursor.getX() + fm.stringWidth(text), cursor.getY() + 2);

        Rectangle clickableArea = new Rectangle(cursor.getX(), cursor.getY() - fm.getAscent(), fm.stringWidth(text), fm.getHeight());
        linkAreas.add(new LinkArea(clickableArea, linkContent));

        cursor.updatePosition(fm.stringWidth(text), 0);
    }

    private void renderImageLink(HtmlElement element, String linkContent) {
        try {
            Image img = renderImage(element);

            Rectangle clickableArea = new Rectangle(cursor.getX(), cursor.getY(), img.getWidth(null), img.getHeight(null));
            linkAreas.add(new LinkArea(clickableArea, linkContent));

            cursor.updatePosition(img.getWidth(null), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void renderList(HtmlElement element, String prefix) {
        for (HtmlElement child : element.getChildren()) {
            cursor.moveToNextLine();
            g2d.drawString(prefix, cursor.getX(), cursor.getY());
            cursor.updatePosition(10, 0);
            renderElement(child, child.getTag());
        }
        cursor.moveToNextLine();
    }

    private void renderDiv(int blockPadding, int defaultWidth) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(cursor.getX(), cursor.getY(), defaultWidth, 20 + blockPadding);
        g2d.setColor(Color.BLACK);
    }

    private Image renderImage(HtmlElement element) {
        try {
            URL imgUrl = new URL(element.getContent());
            Image img = ImageIO.read(imgUrl);
            cursor.moveToNextLine();
            g2d.drawImage(img, cursor.getX(), cursor.getY(), null);
            cursor.updatePosition(img.getWidth(null), img.getHeight(null));
            cursor.moveToNextLine();
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void renderDefault(HtmlElement element) {
//        renderText(element, "Serif", Font.PLAIN, 16, Color.BLACK, 20);
    }

    public List<LinkArea> getLinkAreas() {
        return linkAreas;
    }



}

