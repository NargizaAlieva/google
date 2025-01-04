package org.example.view;

import org.example.model.Model;
import org.example.view.htmlRenderer.HtmlElement;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Canvas extends JPanel {
    private final Map<String, Font> tagFonts = Map.of(
            "p", new Font("Serif", Font.PLAIN, 16),
            "em", new Font("Serif", Font.ITALIC, 16),
            "h1", new Font("Serif", Font.BOLD, 32),
            "h2", new Font("Serif", Font.BOLD, 28),
            "h3", new Font("Serif", Font.BOLD, 24),
            "strong", new Font("Serif", Font.BOLD, 16),
            "span", new Font("Serif", Font.PLAIN, 16),
            "a", new Font("Serif", Font.PLAIN, 16),
            "li", new Font("Serif", Font.PLAIN, 16)
    );
    private Model model;
    private HtmlElement root;
    private int x;
    private int y;
    private String parentTag;


    public Canvas(Model model) {
        this.model = model;
        setBackground(Color.WHITE);
    }


    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int x = 10; // Начальная координата X
        int y = 20; // Начальная координата Y

        root = parseHtml(model.getHtml());

        // Рендерим дерево HTML
        for (HtmlElement child : root.getChildren()) {
            if (child.getTag().equals("body")){
                System.out.println(child.getTag());
                y = renderElement(g2d, child, x, y, child.getTag());
            }
        }
    }

    private int renderElement(Graphics2D g2d, HtmlElement element, int x, int y, String parentTag) {
        int lineHeight = 20;
        int blockPadding = 10;
        int defaultWidth = 400;

        if (element.getTag().equals("p") || element.getTag().equals("em") || element.getTag().equals("h2") || element.getTag().equals("li") || element.getTag().equals("strong") || element.getTag().equals("a") || element.getTag().equals("h1") || element.getTag().equals("h3")) {
            Map<Integer, Integer> xy = renderText(g2d, element, x, y, parentTag, true);
            x = xy.get(0);  // Получаем новое значение x
            y = xy.get(1);  // Получаем новое значение y
            return y;
        }
        String tagToUse2 = element.getTag().equals("text") ? parentTag : element.getTag();
        switch (tagToUse2) {
            case "div":
                System.out.println("I AM HERE HGCVSHGVCHGSGHCVGDV");
                g2d.setColor(Color.BLUE);
                g2d.fillRect(x, y -10, defaultWidth, lineHeight + blockPadding +20);
                g2d.setColor(Color.BLACK);
                y += blockPadding;
                break;
            case "img":
                try {
                    URL imgUrl = new URL(element.getContent());
                    Image img = ImageIO.read(imgUrl);

                    g2d.drawImage(img, x, y, null);

                    y += img.getHeight(null) + blockPadding;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
        if (element.getContent().isEmpty() && !element.getChildren().isEmpty()) {
            for (HtmlElement child : element.getChildren()) {
                System.out.println(child.getTag());
                y = renderElement(g2d, child, x, y, element.getTag());
            }
            return y;
        }

        return y;
    }

    private Map<Integer, Integer> renderText(Graphics g, HtmlElement element, int X, int Y, String parentTag, boolean isFirstTime) {
        if (isFirstTime){
            this.x = X;
            this.y = Y;
            this.parentTag = element.getTag();
        }
        int lineHeight = 20;
        Map<Integer, Integer> coordinates = new HashMap<>();

        for (HtmlElement child : element.getChildren()) {
            String tagToUse2 = child.getTag().equals("text") ? element.getTag() : child.getTag();
            switch (tagToUse2) {
                case "li":
                    if (isFirstTime){
                        g.setFont(new Font("Serif", Font.PLAIN, 16));
                        FontMetrics fmLi = g.getFontMetrics();
                        g.setColor(Color.BLACK);
                        g.drawString("• " + child.getContent(), x, y);
                        x += fmLi.stringWidth("• " + child.getContent()) + 5;
                        isFirstTime = false;
                    } else {
                        renderText2(g, child, tagToUse2);
                    }
                    break;
                default:
                    renderText2(g, child, tagToUse2);
                    break;
            }
        }

        // Возвращаем координаты
        coordinates.put(0, x);
        coordinates.put(1, y + lineHeight);
        return coordinates;
    }

    public void renderLink(Graphics g, HtmlElement child) {
        String content = child.getContent().trim();
        g.setColor(Color.BLUE);
        g.drawString(content, x, y);
        FontMetrics fmLink = g.getFontMetrics();
        int linkWidth = fmLink.stringWidth(content);
        g.drawLine(x, y + 2, x + linkWidth, y + 2);
        x += linkWidth + 5;
    }

    public void renderText2(Graphics g, HtmlElement element, String tag){
        Font baseFont = tagFonts.get(tag);

        g.setFont(baseFont.deriveFont((float) tagFonts.get(parentTag).getSize()));
        if (tag.equals("h1") || tag.equals("h2") || tag.equals("h3")) {
            g.setFont(baseFont.deriveFont((float) tagFonts.get(tag).getSize()));
        }

        FontMetrics fmSpan = g.getFontMetrics();
        for (HtmlElement child : element.getChildren()) {

            if (!child.getTag().equals(tag)){
                renderText(g, child, x, y, child.getTag(), false);
            }

            String contentEm = child.getContent().trim();
            if (tag.equals("a")){
                renderLink(g, child);
            } else {
                g.setColor(Color.BLACK);
                g.drawString(contentEm, x, y);
            }
            x += fmSpan.stringWidth(contentEm) + 5;
        }
        if (element.getChildren().isEmpty()) {

            String contentEm = element.getContent().trim();
            g.drawString(contentEm, x, y);
            x += fmSpan.stringWidth(contentEm) + 5;

            if (tag.equals("a")){
                g.setColor(Color.BLUE);
                int linkWidth = fmSpan.stringWidth(contentEm);
                g.drawLine(x, y + 2, x + linkWidth, y + 2);
            }
        }
    }


    public static HtmlElement parseHtml(String html) {
        HtmlElement root = new HtmlElement("root", "");
        List<HtmlElement> stack = new ArrayList<>();
        stack.add(root);

        Pattern pattern = Pattern.compile("(<[^>]+>)|([^<]+)");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String tag = matcher.group(1);
            String text = matcher.group(2);

            if (tag != null) {
                if (tag.startsWith("<") && !tag.startsWith("</")) {
                    String tagName = tag.substring(1, tag.indexOf(" ") != -1 ? tag.indexOf(" ") : tag.length() - (tag.endsWith("/") ? 2 : 1)).trim();
                    HtmlElement element = new HtmlElement(tagName, "");

                    if (tagName.equals("img")) {
                        String src = extractAttribute(tag, "src");
                        element.setContent(src);
                    } else if (tagName.equals("a")) {
                        String href = extractAttribute(tag, "href");
                        element.setContent(href);
                    }

                    stack.get(stack.size() - 1).addChild(element);

                    if (!tag.endsWith("/>")) {
                        stack.add(element);
                    }
                } else if (tag.startsWith("</")) {
                    stack.remove(stack.size() - 1);
                }
            }

            if (text != null && !text.trim().isEmpty()) {
                HtmlElement textElement = new HtmlElement("text", text.trim());
                stack.get(stack.size() - 1).addChild(textElement);
            }
        }

        return root;
    }

    private static String extractAttribute(String tag, String attributeName) {
        String pattern = attributeName + "=\"([^\"]*)\"";
        Pattern attrPattern = Pattern.compile(pattern);
        Matcher attrMatcher = attrPattern.matcher(tag);
        if (attrMatcher.find()) {
            return attrMatcher.group(1);
        }
        return null;
    }
}
