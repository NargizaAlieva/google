package org.example.model.renderTree;

import org.example.model.css.cssom.CssRule;
import org.example.model.css.cssom.CssTree;
import org.example.model.html.HtmlElement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Merge_CSSOM_DOM {

    public RenderTree mergeCSSOM_DOM(HtmlElement htmlElement, CssTree cssTree) {
        RenderNode rootRenderNode = new RenderNode(htmlElement.getTag());
        createRenderTree(htmlElement, rootRenderNode);
        for (CssRule cssRule : cssTree.getRules()) {
            findCssOfRenderTree(htmlElement, rootRenderNode, cssRule.getSelector(), cssRule);
        }

        return new RenderTree(rootRenderNode);
    }

    private void createRenderTree(HtmlElement htmlElement, RenderNode parentRenderNode) {
        for (HtmlElement child : htmlElement.getChildren()) {
            RenderNode renderNode = new RenderNode(child.getTag());
            parentRenderNode.addChild(renderNode);
            renderNode.setTextContent(child.getContent());
            renderNode.setParent(parentRenderNode);
            createRenderTree(child, renderNode);
        }
    }

    public void findCssOfRenderTree(HtmlElement htmlElement, RenderNode renderNode, List<String> selectors, CssRule cssRule) {
        if (htmlElement.getClasses() == null) {
            return;
        }

        String currentSelector = selectors.get(0);
        List<HtmlElement> htmlChildren = htmlElement.getChildren();
        List<RenderNode> renderChildren = renderNode.getChildren();

        if (Arrays.asList(htmlElement.getClasses()).contains(currentSelector) || htmlElement.getTag().equals(currentSelector)) {
            selectors = selectors.subList(1, selectors.size());

            if (selectors.isEmpty()) {
                computeStyles(cssRule, htmlElement, renderNode);
                return;
            }
            for (int i = 0; i < htmlChildren.size(); i++) {
                findCssOfRenderTree(htmlChildren.get(i), renderChildren.get(i), selectors, cssRule);
            }
        }
        for (int i = 0; i < htmlChildren.size(); i++) {
            findCssOfRenderTree(htmlChildren.get(i), renderChildren.get(i), selectors, cssRule);
        }
    }
    private void computeStyles(CssRule cssRule, HtmlElement htmlElement, RenderNode renderNode) {
        HashMap<String, String> styles = new HashMap<>();
        styles.putAll(renderNode.getParent().getAppliedStyles());
        styles.putAll(cssRule.getPropertiesMap());
        styles.putAll(htmlElement.getStyles());

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
        renderNode.applyStyles(styles);
    }

}
