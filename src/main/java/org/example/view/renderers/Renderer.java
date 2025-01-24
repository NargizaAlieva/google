package org.example.view.renderers;

import org.example.model.html.HtmlElement;
import org.example.model.renderTree.RenderNode;
import org.example.model.renderTree.RenderTree;
import org.example.utils.Cursor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Renderer {
    private Graphics2D g2d;
    private List<LinkArea> linkAreas;
    private RenderTree renderTree;
    private Map<String, Consumer<RenderNode>> renderers;
    public Renderer() {
        this.linkAreas = new ArrayList<>();
        createRenderers();
    }

    public void renderElement(Graphics2D g2d, RenderTree renderTree) {
        this.g2d = g2d;
        this.renderTree = renderTree;
        renderElement(renderTree.getRoot(), renderTree.getRoot().getTagName());
    }

    private void renderElement(RenderNode node, String parentTag) {
        drowRect(node);
        String tagToUse = node.getTagName().equals("text") ? parentTag : node.getTagName();
        System.out.println(tagToUse);
        renderers.getOrDefault(tagToUse, this::renderDefault).accept(node);

        if (node.getTextContent().isEmpty() && !node.getChildren().isEmpty() && !node.getTagName().equals("ul")) {
            for (RenderNode child : node.getChildren()) {
                renderElement(child, node.getTagName());
            }
        }
    }

    private void createRenderers() {
        renderers = new HashMap<>();

        renderers.put("h1", this::renderText);
        renderers.put("h2", this::renderText);
        renderers.put("h3", this::renderText);
        renderers.put("p", this::renderText);
        renderers.put("span", this::renderText);
        renderers.put("a", this::renderLink);
        renderers.put("ul", this::renderList);
        renderers.put("li", this::renderText);
        renderers.put("strong", this::renderText);
        renderers.put("em", this::renderText);
        renderers.put("img", this::renderImage);
    }

    private void drowRect(RenderNode node) {
        int width = (int) node.getWidth();
        int height = (int) node.getHeight();

        g2d.drawRect(node.getX(), node.getY(), width, height);
    }

    private void renderText(RenderNode node) {
        FontMetrics fm = g2d.getFontMetrics();
        String content = node.getTextContent();

        StringBuilder lineBuilder = new StringBuilder();
        for (char c : content.toCharArray()) {
            lineBuilder.append(c);
            int lineWidth = fm.stringWidth(lineBuilder.toString());

            if (node.getX() + lineWidth > renderTree.getWindowWidth()) {
                g2d.drawString(lineBuilder.toString(), node.getX(), node.getY());
                lineBuilder.setLength(0);
                lineBuilder.append(c);
            }
        }

        if (!lineBuilder.isEmpty()) {
            g2d.drawString(lineBuilder.toString().trim(), node.getX(), node.getY());
        }
    }

    private void renderLink(RenderNode node) {
        String link = node.getTextContent();

        while (!node.getTagName().equals("text") && !node.getTagName().equals("img")
                && node.getChildren().size() == 1) {
            node = node.getChildren().get(0);
        }

        if (node.getTagName().equals("text")) {
            renderTextLink(node, link);
        } else if (node.getTagName().equals("img")) {
            renderImageLink(node, link);
        }
    }

    private void renderTextLink(RenderNode node, String linkContent) {
        g2d.setColor(Color.BLUE);
        Font underlineFont = g2d.getFont().deriveFont(Font.PLAIN);
        g2d.setFont(underlineFont);

        FontMetrics fm = g2d.getFontMetrics();

        g2d.drawString(node.getTextContent(), node.getX(), node.getY());
        g2d.drawLine(node.getX(), node.getY() + 2, node.getX() + fm.stringWidth(node.getTextContent()), node.getY() + 2);

        Rectangle clickableArea = new Rectangle(node.getX(), node.getY() - fm.getAscent(), fm.stringWidth(node.getTextContent()),
                fm.getHeight());
        linkAreas.add(new LinkArea(clickableArea, linkContent));
    }

    private void renderImageLink(RenderNode node, String linkContent) {
        try {
            Image img = renderImage(node);

            Rectangle clickableArea = new Rectangle(node.getX(), node.getY(), img.getWidth(null),
                    img.getHeight(null));
            linkAreas.add(new LinkArea(clickableArea, linkContent));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderList(RenderNode node) {
        for (RenderNode child : node.getChildren()) {
            g2d.drawString("•", node.getX(), node.getY());
            renderElement(child, child.getTagName());
        }
    }

    private Image renderImage(RenderNode node) {
        try {
            URL imgUrl = new URL(node.getTextContent());
            Image img = ImageIO.read(imgUrl);
            g2d.drawImage(img, node.getX(), node.getY(), null);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void renderDefault(RenderNode node) {
        // renderText(element, "Serif", Font.PLAIN, 16, Color.BLACK, 20);
    }

    public List<LinkArea> getLinkAreas() {
        return linkAreas;
    }
}
