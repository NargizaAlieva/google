package org.example.view;

import org.example.model.Model;
import org.example.view.htmlRenderer.HtmlElement;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Canvas extends JPanel {
    private Model model;
    private HtmlElement root;

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
            y = renderElement(g2d, child, x, y);
        }
    }

    private int renderElement(Graphics2D g2d, HtmlElement element, int x, int y, String parentTag) {
        int lineHeight = 20;
        int blockPadding = 10;
        int defaultWidth = 400;

        if (element.getContent().isEmpty() && !element.getChildren().isEmpty()) {
            for (HtmlElement child : element.getChildren()) {
                y = renderElement(g2d, child, x, y, element.getTag());
            }
            return y;
        }

        String tagToUse = element.getTag().equals("text") ? parentTag : element.getTag();

        switch (tagToUse) {
            case "h1":
                g2d.setFont(new Font("Serif", Font.BOLD, 32));
                g2d.setColor(Color.BLACK);
                g2d.drawString(element.getContent(), x, y);
                y += lineHeight + 20;
                break;

            case "h2":
                g2d.setFont(new Font("Serif", Font.BOLD, 28));
                g2d.setColor(Color.BLACK);
                g2d.drawString(element.getContent(), x, y);
                y += lineHeight + 15;
                break;

            case "h3":
                g2d.setFont(new Font("Serif", Font.BOLD, 24));
                g2d.setColor(Color.BLACK);
                g2d.drawString(element.getContent(), x, y);
                y += lineHeight + 10;
                break;

            case "p":
                g2d.setFont(new Font("Serif", Font.PLAIN, 16));
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(element.getContent(), x, y);
                y += lineHeight + blockPadding;
                break;

            case "span":
                g2d.setFont(new Font("Serif", Font.PLAIN, 16));
                g2d.setColor(Color.BLACK);
                g2d.drawString(element.getContent(), x, y);
                y += lineHeight;
                break;

            case "a":
                g2d.setFont(new Font("Serif", Font.PLAIN, 16));
                g2d.setColor(Color.BLUE);
                g2d.drawString(element.getContent(), x, y);

                FontMetrics fm = g2d.getFontMetrics();
                int linkWidth = fm.stringWidth(element.getContent());
                g2d.drawLine(x, y + 2, x + linkWidth, y + 2);

                y += lineHeight;
                break;

            case "ul":
                for (HtmlElement child : element.getChildren()) {
                    y = renderElement(g2d, child, x + blockPadding, y, "li");
                }
                y += blockPadding;
                break;

            case "li":
                g2d.setFont(new Font("Serif", Font.PLAIN, 16));
                g2d.setColor(Color.BLACK);
                g2d.drawString("• " + element.getContent(), x, y);
                y += lineHeight;
                break;

            case "div":
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, defaultWidth, lineHeight + blockPadding);
                g2d.setColor(Color.BLACK);
                y += blockPadding;

//                for (HtmlElement child : element.getChildren()) {
//                    y = renderElement(g2d, child, x + blockPadding, y, "div");
//                }

                y += blockPadding;
                break;

            case "strong":
                g2d.setFont(new Font("Serif", Font.BOLD, 16));
                g2d.setColor(Color.BLACK);
                g2d.drawString(element.getContent(), x, y);
                y += lineHeight;
                break;

            case "em":
                g2d.setFont(new Font("Serif", Font.ITALIC, 16));
                g2d.setColor(Color.BLACK);
                g2d.drawString(element.getContent(), x, y);
                y += lineHeight;
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

            default:
                g2d.setFont(new Font("Serif", Font.PLAIN, 16));
                g2d.setColor(Color.BLACK);
                g2d.drawString(element.getContent(), x, y);
                y += lineHeight;
                break;
        }

        return y;
    }

    private int renderElement(Graphics2D g2d, HtmlElement element, int x, int y) {
        return renderElement(g2d, element, x, y, "root");
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
