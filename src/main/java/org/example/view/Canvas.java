package org.example.view;

import org.example.model.CssParser;
import org.example.model.cssom.CssProperty;
import org.example.model.cssom.CssRule;
import org.example.model.Model;
import org.example.view.htmlRenderer.HtmlElement;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Canvas extends JPanel {
    private final Map<String, Font> tagFonts = new HashMap<>() {{
        put("p", new Font("Serif", Font.PLAIN, 16));
        put("em", new Font("Serif", Font.ITALIC, 16));
        put("h1", new Font("Serif", Font.BOLD, 32));
        put("h2", new Font("Serif", Font.BOLD, 28));
        put("h3", new Font("Serif", Font.BOLD, 24));
        put("strong", new Font("Serif", Font.BOLD, 16));
        put("span", new Font("Serif", Font.PLAIN, 16));
        put("a", new Font("Serif", Font.PLAIN, 16));
        put("li", new Font("Serif", Font.PLAIN, 16));
        put("b", new Font("Serif", Font.BOLD, 16));
        put("i", new Font("Serif", Font.ITALIC, 16));      // Тег <i> для наклонного текста
        put("u", new Font("Serif", Font.PLAIN, 16));       // Тег <u> для подчеркнутого текста
        put("small", new Font("Serif", Font.PLAIN, 12));   // Тег <small> для уменьшенного шрифта
        put("sub", new Font("Serif", Font.PLAIN, 16));     // Тег <sub> для индекса
        put("sup", new Font("Serif", Font.PLAIN, 16));     // Тег <sup> для суперскрипта
        put("mark", new Font("Serif", Font.BOLD, 16));
    }};
    private Model model;
    private HtmlElement root;
    private int x;
    private int y;
    private String parentTag;
    private String html;
    private CssParser cssParser;

    public Canvas(Model model, CssParser cssParser) {
        this.cssParser = cssParser;
        this.model = model;
        setBackground(Color.WHITE);
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int x = 10; // Начальная координата X
        int y = 20; // Начальная координата Y

        root = parseHtml(extractBodyContent(model.getHttpResponse().getHtmlBody()));
        cssParser.parse(model.getHttpResponse().getCssResources());
        for(CssRule css : cssParser.getCssTree().getRules()){
            System.out.println(css);
            findCssOfHtml(root, css.getSelector(), css);
        }
        for (HtmlElement child : root.getChildren()) {
            y = renderElement(g2d, child, x, y, child.getTag());
        }
    }

    private void findCssOfHtml(HtmlElement htmlElement, List<String> selectors, CssRule cssRule) {
        if (selectors.isEmpty()) {
            for (CssProperty cssProperty : cssRule.getProperties()) {
                htmlElement.setCssRule(cssProperty.getName(), cssProperty.getValue());
            }
            return;
        }

        if (htmlElement.getClasses() == null) {
            return;
        }

        String currentSelector = selectors.get(0);

        if (Arrays.asList(htmlElement.getClasses()).contains(currentSelector) || htmlElement.getTag().equals(currentSelector)) {
            selectors = selectors.subList(1, selectors.size());

            if (selectors.isEmpty()) {
                for (CssProperty cssProperty : cssRule.getProperties()) {
                    htmlElement.setCssRule(cssProperty.getName(), cssProperty.getValue());
                }
                return;
            }

            for (HtmlElement child : htmlElement.getChildren()) {
                findCssOfHtml(child, selectors, cssRule);
            }
        }

        for (HtmlElement child : htmlElement.getChildren()) {
            findCssOfHtml(child, selectors, cssRule);
        }
    }

    private String extractBodyContent(String html) {
        Pattern bodyPattern = Pattern.compile("<body[^>]*>(.*?)</body>", Pattern.DOTALL);
        Matcher matcher = bodyPattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "Body not found";
    }

    private int renderElement(Graphics2D g2d, HtmlElement element, int x, int y, String parentTag) {
        int lineHeight = 20;
        int blockPadding = 10;
        int defaultWidth = 400;

        if (tagFonts.get(element.getTag()) != null) {
            Map<Integer, Integer> xy = renderText(g2d, element, x, y, parentTag, true);
            x = xy.get(0);
            y = xy.get(1);
            return y;
        }
        if (element.getStyles() != null) {

        }

        String tagToUse2 = element.getTag().equals("text") ? parentTag : element.getTag();
        switch (tagToUse2) {
            case "div":
                g2d.setColor(Color.BLUE);
                g2d.fillRect(x, y -10, defaultWidth, lineHeight + blockPadding +20);
                g2d.setColor(Color.BLACK);
                y += blockPadding;
                break;
            case "img":
                try {
                    String imageUrl = element.getContent();
                    System.out.println("Rendering image: " + imageUrl);

                    URL imgUrl = new URL(imageUrl);
                    Image img = ImageIO.read(imgUrl);

                    if (img == null) {
                        System.err.println("Failed to load image from URL: " + imageUrl);
                        break;
                    }

                    g2d.drawImage(img, x, y, null);

                } catch (MalformedURLException e) {
                    System.err.println("Invalid URL for image: " + element.getContent());
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("Error loading image: " + element.getContent());
                    e.printStackTrace();
                }
                break;


        }
        if (element.getContent().isEmpty() && !element.getChildren().isEmpty()) {
            for (HtmlElement child : element.getChildren()) {
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
    }

    public void renderText2(Graphics g, HtmlElement element, String tag){
        Font baseFont = tagFonts.get(tag);
        if (baseFont == null) {
            baseFont = new Font("Serif", Font.PLAIN, 16);
        }
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
                if (tag.startsWith("<!--") || tag.startsWith("<!DOCTYPE") || tag.startsWith("<img")) {
                    continue;
                }
                else if (tag.startsWith("<") && !tag.startsWith("</")) {
                    String tagName = tag.substring(1, tag.indexOf(" ") != -1 ? tag.indexOf(" ") : tag.length() - (tag.endsWith("/") ? 2 : 1)).trim();
                    HtmlElement element = new HtmlElement(tagName, "");

                    element.setClasses(extractClasses(tag));
                    extractStyles(tag, element);
                    element.setIds(extractIds(tag));
                    element.setParent(stack.get(stack.size() - 1));

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
    private static String[] extractClasses(String tag) {
        String pattern = "class=\"([^\"]*)\"";
        Pattern classPattern = Pattern.compile(pattern);
        Matcher classMatcher = classPattern.matcher(tag);
        if (classMatcher.find()) {
            return classMatcher.group(1).split("\\s+");
        }
        return null;
    }

    private static String[] extractIds(String tag) {
        String pattern = "id=\"([^\"]*)\"";
        Pattern idPattern = Pattern.compile(pattern);
        Matcher idMatcher = idPattern.matcher(tag);
        if (idMatcher.find()) {
            return idMatcher.group(1).split("\\s+");
        }
        return null;
    }

    private static void extractStyles(String tag, HtmlElement element) {
        String pattern = "style=\"([^\"]*)\"";
        Pattern stylePattern = Pattern.compile(pattern);
        Matcher styleMatcher = stylePattern.matcher(tag);
        if (styleMatcher.find()) {
            String[] properties = styleMatcher.group(1).trim().split(";");
            for (String property : properties) {
                String[] keyValue = property.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    element.setStyles(key, value);
                }
            }
        }
    }

}
