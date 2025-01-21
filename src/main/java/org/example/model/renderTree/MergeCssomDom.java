package org.example.model.renderTree;

import org.example.model.css.cssom.CssRule;
import org.example.model.css.cssom.CssTree;
import org.example.model.html.HtmlElement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MergeCssomDom {
    private RenderTree renderTree;
    private final Map<String, Integer> tagSize = new HashMap<>() {{
        put("h1", 32);
        put("h2", 24);
        put("h3", 18);
        put("h4", 16);
        put("h5", 13);
        put("h6", 10);
    }};

    public RenderTree mergeCssomDom(HtmlElement htmlElement, CssTree cssTree) {
        renderTree = new RenderTree();
        RenderNode rootRenderNode = new RenderNode(htmlElement.getTag());
        rootRenderNode.setWidth(renderTree.getWindowWidth());
        rootRenderNode.setHeight(renderTree.getWindowHeight());
        createRenderTree(htmlElement, rootRenderNode);

        for (CssRule cssRule : cssTree.getRules()) {
            findCssOfRenderTree(htmlElement, rootRenderNode, cssRule.getSelector(), cssRule);
        }

        setCssToChildren(rootRenderNode);
        setWidthHeightToChildren(rootRenderNode);
        hTagsInHeight(rootRenderNode);
        finalCalculationOfHeight(rootRenderNode);
        setXY(rootRenderNode);


        renderTree.setRoot(rootRenderNode);

        return renderTree;
    }
    private void setXY(RenderNode renderNode) {
        double parentRemainWidth = renderNode.getWidth();
        double lastX = renderNode.getX();
        double lastY = renderNode.getY();
        double rowHeight = 0;

        for (RenderNode child : renderNode.getChildren()) {
            if (parentRemainWidth >= child.getWidth()) {
                child.setX(lastX);
                child.setY(lastY);

                lastX += child.getWidth();
                parentRemainWidth -= child.getWidth();

                rowHeight = Math.max(rowHeight, child.getHeight());
            } else {
                lastX = renderNode.getX();
                lastY += rowHeight;
                parentRemainWidth = renderNode.getWidth();

                child.setX(lastX);
                child.setY(lastY);

                lastX += child.getWidth();
                parentRemainWidth -= child.getWidth();

                rowHeight = child.getHeight();
            }

            setXY(child);
        }
    }


    private void finalCalculationOfHeight(RenderNode renderNode) {
         for (RenderNode child : renderNode.getChildren()){
             calculateHeightOfChildren(child);
             finalCalculationOfHeight(child);
         }
    }
    private double calculateHeightOfChildren(RenderNode renderNode) {
        double height = 0;
        double remainingWidth = renderNode.getWidth();
        ArrayList<Double> rowHeights = new ArrayList<>();
        ArrayList<RenderNode> children = renderNode.getChildren();

        if (children == null) {
            return height;
        }

        for (RenderNode child : children) {
            if ("img".equals(child.getTagName())) {
                // child.setHeight(getImageHeight(child));
            } else if (child.getHeight() == -1) {
                child.setHeight(calculateHeightOfChildren(child));
            }

            if (remainingWidth >= child.getWidth()) {
                rowHeights.add(child.getHeight());
                remainingWidth -= child.getWidth();
            } else {
                if (!rowHeights.isEmpty()) {
                    height += getMaxHeight(rowHeights);
                    rowHeights.clear();
                }
                rowHeights.add(child.getHeight());
                remainingWidth = renderNode.getWidth() - child.getWidth();
            }
        }

        if (!rowHeights.isEmpty()) {
            height += getMaxHeight(rowHeights);
        }

        return height;
    }

    private int getImageHeight(RenderNode imgNode) {
        String imagePath = imgNode.getTextContent();

        if (imagePath == null || imagePath.isEmpty()) {
            return 0;
        }

        try {
            BufferedImage img;

            if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                URL url = new URL(imagePath);
                img = ImageIO.read(url);
            } else {
                img = ImageIO.read(new File(imagePath));
            }
            System.out.println("Sigma");
            if (img != null) {
                System.out.println(img.getHeight());
                imgNode.setWidth(img.getWidth());
            }
            System.out.println("Sigma");

            return img != null ? img.getHeight() : 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private double getMaxHeight(ArrayList<Double> heights) {
        double maxHeight = 0;
        for (double h : heights) {
            if (h > maxHeight) {
                maxHeight = h;
            }
        }
        return maxHeight;
    }


    private void hTagsInHeight(RenderNode rootRenderNode) {
        for (RenderNode child : rootRenderNode.getChildren()) {
            if (tagSize.get(child.getTagName()) != null) {
                calculateHTags(child, child.getTagName());
            }
            hTagsInHeight(child);
        }
    }

    private void calculateHTags(RenderNode renderNode, String tagNameForSize) {
        int size = tagSize.get(tagNameForSize);
        System.out.println("i Am here");
        System.out.println(size);
        if (size != -1) {
            renderNode.setHeight(size);
            for (RenderNode child : renderNode.getChildren()) {
                calculateHTags(child, tagNameForSize);
            }
        };
    }



    private void createRenderTree(HtmlElement htmlElement, RenderNode parentRenderNode) {
        for (HtmlElement child : htmlElement.getChildren()) {
            RenderNode renderNode = new RenderNode(child.getTag());
            parentRenderNode.addChild(renderNode);
            renderNode.setTextContent(child.getContent());
            renderNode.setStyles(child.getStyles());
            renderNode.setParent(parentRenderNode);
            createRenderTree(child, renderNode);
        }
    }



    private void setWidthHeightToChildren(RenderNode renderNode) {
        for (RenderNode child : renderNode.getChildren()) {
            setWidthToChildren(renderNode, child);
            setHeightToChildren(child);
            setWidthHeightToChildren(child);
        }
    }

    private void setHeightToChildren(RenderNode renderNode) {
        String strHeight = renderNode.getAppliedStyles().get("height");

        if (strHeight != null && !strHeight.isEmpty()){
            int intHeight = Integer.parseInt(strHeight.replace("px", "").replace("%", "").trim());
            if (strHeight.contains("px")){
                renderNode.setHeight(intHeight);
            } else if (strHeight.endsWith("%")) {
                renderNode.setHeight(getHeightFromParent(renderNode, intHeight));
            } else {
                renderNode.setHeight(intHeight);
            }
        } else if (renderNode.getTagName().equals("text")){
            setHeightOfText(renderNode);
        }
    }

    private void setHeightOfText(RenderNode renderNode) {
        // Получаем текст из узла
        String textContent = renderNode.getTextContent();
        if (textContent == null || textContent.isEmpty()) {
            return;
        }

        // Получаем имя тега
        String tagName = renderNode.getTagName();

        // Устанавливаем шрифт и размер по умолчанию
        String fontName = renderNode.getAppliedStyles().getOrDefault("font-family", "Arial");
        int fontSize;

        // Используем размер шрифта из стилей или значение по умолчанию
        fontSize = Integer.parseInt(renderNode.getAppliedStyles().getOrDefault("font-size", "12").replace("px", "").trim());


        // Устанавливаем стиль шрифта (по умолчанию - обычный)
        int fontStyle = Font.PLAIN;
        String fontWeight = renderNode.getAppliedStyles().getOrDefault("font-weight", "normal");
        if (fontWeight.equalsIgnoreCase("bold")) {
            fontStyle = Font.BOLD;
        }

        Font font = new Font(fontName, fontStyle, fontSize);

        Canvas dummyCanvas = new Canvas();
        FontMetrics metrics = dummyCanvas.getFontMetrics(font);

        int textHeight = metrics.getHeight();
        int textWidth = metrics.stringWidth(textContent);
        renderNode.setHeight(textHeight);
        renderNode.setWidth(textWidth);
        if (!Objects.equals(renderNode.getParent().getTagName(), "div")){
            double parentWidth = renderNode.getWidth();
            renderNode.getParent().setWidth(textWidth + parentWidth);
            renderNode.getParent().setHeight(textHeight);
        }
    }

    private Integer getHeightFromParent(RenderNode renderNode, int devider) {
        String strHeight = renderNode.getAppliedStyles().get("height");
        if (strHeight == null || devider == 0) {
            return renderTree.getWindowHeight() / (100 / Math.max(devider, 1));
        }
        int height = Integer.parseInt(strHeight.replace("px", "").replace("%", "").trim());
        if (renderNode.getParent() == null){
            if (renderNode.getAppliedStyles().get("height") != null){
                return height / (100 / devider);
            }
            return renderTree.getWindowHeight() / (100 / devider);
        }
        if (strHeight.contains("%")){
            getHeightFromParent(renderNode.getParent(), height / (100 / devider));
        } else if (strHeight.contains("px")) {
            return height / (100 / devider);
        }
        return 0;
    }

    private void setWidthToChildren(RenderNode renderNode, RenderNode child) {
        HashMap<String, String> styles = child.getAppliedStyles();

        if (styles.get("width") != null && !styles.get("width").isEmpty() || (styles.get("max-width") != null && !styles.get("max-width").isEmpty())) {
            if (styles.get("max-width") != null && !styles.get("max-width").isEmpty()) {
                if (styles.get("max-width").contains("px")) {
                    child.setWidth(Double.parseDouble(styles.get("max-width").replace("px", "").trim()));
                } else if (styles.get("max-width").contains("%")) {
                    double maxWidthPercent = Double.parseDouble(styles.get("max-width").replace("%", "").trim());
                    child.setWidth((renderNode.getWidth() / 100.0) * maxWidthPercent);
                }
            } else {
                if (styles.get("width").contains("px")) {
                    child.setWidth(Double.parseDouble(styles.get("width").replace("px", "").trim()));
                } else if (styles.get("width").contains("%")) {
                    double widthPercent = Double.parseDouble(styles.get("width").replace("%", "").trim());
                    child.setWidth((renderNode.getWidth() / 100.0) * widthPercent);
                }
            }
        } else {
            child.setWidth(child.getParent().getWidth());
        }
    }



    public void findCssOfRenderTree(HtmlElement htmlElement, RenderNode renderNode, List<String> selectors, CssRule cssRule) {
        if (htmlElement.getClasses() == null || selectors.isEmpty() || cssRule == null) {
            return;
        }

        Set<String> elementClasses = new HashSet<>(Arrays.asList(htmlElement.getClasses()));

        String currentSelector = selectors.get(0);
        if (htmlElement.getIds() != null){
            Set<String> elementIds = new HashSet<>(Arrays.asList(htmlElement.getIds()));
            if (currentSelector.startsWith("#") && elementIds.contains(currentSelector.substring(1))) {
                selectors = selectors.subList(1, selectors.size());
                if (selectors.isEmpty()) {
                    computeStyles(cssRule, renderNode);
                    return;
                }
            }
        }

        if (elementClasses.contains(currentSelector) || htmlElement.getTag().equals(currentSelector)) {
            selectors = selectors.subList(1, selectors.size());
            if (selectors.isEmpty()) {
                computeStyles(cssRule, renderNode);
                return;
            }
        }

        for (int i = 0; i < htmlElement.getChildren().size(); i++) {
            findCssOfRenderTree(htmlElement.getChildren().get(i), renderNode.getChildren().get(i), selectors, cssRule);
        }
    }

    private void setCssToChildren(RenderNode renderNode) {
        for (RenderNode child : renderNode.getChildren()) {
            HashMap<String, String> parentStyles = renderNode.getAppliedStyles();
            for (Map.Entry<String, String> entry : parentStyles.entrySet()) {
                if (entry.getKey().equals("height")){
                    continue;
                }
                child.getAppliedStyles().putIfAbsent(entry.getKey(), entry.getValue());
            }
            setCssToChildren(child);
        }
    }

    private void computeStyles(CssRule cssRule, RenderNode renderNode) {
        HashMap<String, String> styles = new HashMap<>(cssRule.getPropertiesMap());

        Set<String> allowedStyles = Set.of(
                // Размеры и отступы
                "width", "height", "min-width", "max-width", "min-height", "max-height",
                "margin", "margin-top", "margin-right", "margin-bottom", "margin-left",
                "padding", "padding-top", "padding-right", "padding-bottom", "padding-left",
                "border", "border-width", "border-color", "border-style",
                "border-top", "border-right", "border-bottom", "border-left",
                "border-radius",
                "outline", "outline-width", "outline-color", "outline-style",

                // Цвета и фон
                "color", "background", "background-color", "background-image",
                "background-size", "background-position", "background-repeat",

                // Шрифты и текст
                "font-size", "font-family", "font-weight", "font-style", "font-variant",
                "line-height", "letter-spacing", "word-spacing",
                "text-align", "text-indent", "text-decoration", "text-transform",
                "white-space", "vertical-align",

                // Расположение и отображение
                "display", "visibility", "z-index", "position",
                "top", "right", "bottom", "left", "overflow",
                "overflow-x", "overflow-y", "float", "clear",

                // Тени и градиенты
                "box-shadow", "text-shadow",

                // Переходы и анимации
                "transition", "transition-property", "transition-duration",
                "transition-timing-function", "transition-delay",
                "animation", "animation-name", "animation-duration",
                "animation-timing-function", "animation-delay",
                "animation-iteration-count", "animation-direction",

                // Flexbox
                "flex", "flex-grow", "flex-shrink", "flex-basis",
                "justify-content", "align-items", "align-self",
                "align-content", "order",

                // Grid
                "grid-template-rows", "grid-template-columns", "grid-template-areas",
                "grid-gap", "grid-row", "grid-column", "grid-area",
                "justify-items", "justify-self", "place-items",

                // Другие свойства
                "cursor", "clip", "opacity", "content", "quotes"
        );

        styles.keySet().retainAll(allowedStyles);

        renderNode.setStyles(styles);
    }

}

