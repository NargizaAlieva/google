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


//    private void renderLink(HtmlElement element) {
//        String content = element.getContent();
//        if (element.getChildren().size() == 1) {
//            element = element.getChildren().get(0);
//            while (!element.getTag().equals("text") && element.getChildren().size() == 1)
//                element = element.getChildren().get(0);
//        }
//        g2d.setColor(Color.BLUE);
//        Font underlineFont = g2d.getFont().deriveFont(Font.PLAIN);
//        g2d.setFont(underlineFont);
//
//        FontMetrics fm = g2d.getFontMetrics();
//        int x = cursor.getX();
//        int y = cursor.getY();
//
//        g2d.drawString(element.getContent(), x, y);
//        g2d.drawLine(x, y + 2, x + fm.stringWidth(element.getContent()), y + 2);
//
//        // Кликабельная область (ну то что было добавлено)
//        Rectangle clickableArea = new Rectangle(x, y - fm.getAscent(), fm.stringWidth(element.getContent()), fm.getHeight());
//        linkAreas.add(new LinkArea(clickableArea, content));
//
//        cursor.updatePosition(fm.stringWidth(content), 0);
//    }

    private void renderLink(HtmlElement element) {
        // Извлекаем контент ссылки (например, href или src)
        String linkContent = element.getContent();

        // Если ссылка содержит дочерние элементы, находим первый текстовый или изображенный элемент
        element = findRenderableChild(element);

        // Если элемент — текст, рендерим его
        if (element.getTag().equals("text")) {
            renderTextLink(g2d, element.getContent(), linkContent);
        }
        // Если элемент — изображение, рендерим его
        else if (element.getTag().equals("img")) {
            renderImageLink(g2d, element, linkContent);
        }
    }

    // Метод для поиска рендеримого дочернего элемента
    private HtmlElement findRenderableChild(HtmlElement element) {
        while (!element.getTag().equals("text") && !element.getTag().equals("img") && element.getChildren().size() == 1) {
            element = element.getChildren().get(0);
        }
        return element;
    }

    // Рендеринг текстовой ссылки
    private void renderTextLink(Graphics2D g2d, String text, String linkContent) {
        // Установка стиля текста
        g2d.setColor(Color.BLUE);
        Font underlineFont = g2d.getFont().deriveFont(Font.PLAIN);
        g2d.setFont(underlineFont);

        // Вычисляем метрики текста
        FontMetrics fm = g2d.getFontMetrics();
        int x = cursor.getX();
        int y = cursor.getY();

        // Рисуем текст и подчеркивание
        g2d.drawString(text, x, y);
        g2d.drawLine(x, y + 2, x + fm.stringWidth(text), y + 2);

        // Создаем кликабельную область
        Rectangle clickableArea = new Rectangle(x, y - fm.getAscent(), fm.stringWidth(text), fm.getHeight());
        linkAreas.add(new LinkArea(clickableArea, linkContent));

        // Обновляем позицию курсора
        cursor.updatePosition(fm.stringWidth(text), 0);
    }

    // Рендеринг изображения как ссылки
    private void renderImageLink(Graphics2D g2d, HtmlElement element, String linkContent) {
        try {
            // Загружаем изображение
            URL imgUrl = new URL(element.getContent());
            Image img = ImageIO.read(imgUrl);

            // Рендерим изображение
            int x = cursor.getX();
            int y = cursor.getY();
            g2d.drawImage(img, x, y, null);

            // Создаем кликабельную область
            Rectangle clickableArea = new Rectangle(x, y, img.getWidth(null), img.getHeight(null));
            linkAreas.add(new LinkArea(clickableArea, linkContent));

            // Обновляем позицию курсора
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

    private void renderImage(HtmlElement element) {
        try {
            URL imgUrl = new URL(element.getContent());
            Image img = ImageIO.read(imgUrl);
            cursor.moveToNextLine();
            g2d.drawImage(img, cursor.getX(), cursor.getY(), null);
            cursor.updatePosition(img.getWidth(null), img.getHeight(null));
            cursor.moveToNextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderDefault(HtmlElement element) {
//        renderText(element, "Serif", Font.PLAIN, 16, Color.BLACK, 20);
    }

    public List<LinkArea> getLinkAreas() {
        return linkAreas;
    }



}

