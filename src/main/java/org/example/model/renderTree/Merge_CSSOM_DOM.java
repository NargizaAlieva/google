package org.example.model.renderTree;

import org.example.model.css.cssom.CssRule;
import org.example.model.css.cssom.CssTree;
import org.example.model.html.HtmlElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Merge_CSSOM_DOM {

    public RenderTree mergeCSSOM_DOM(HtmlElement htmlElement, CssTree cssTree) {
        RenderNode rootRenderNode = new RenderNode(htmlElement.getTag());
        createRenderTree(htmlElement, rootRenderNode);
        for (CssRule cssRule : cssTree.getRules()) {
            matchCssAndHtml(htmlElement, rootRenderNode, cssRule.getSelector(), cssRule);
        }

        return new RenderTree(rootRenderNode);
    }

    private void createRenderTree(HtmlElement htmlElement, RenderNode parentRenderNode) {
        for (HtmlElement child : htmlElement.getChildren()) {
            RenderNode renderNode = new RenderNode(child.getTag());
            parentRenderNode.addChild(renderNode);
            createRenderTree(child, renderNode);
        }
    }

    private HashMap<String, String> computeStyles(CssRule cssRule, HtmlElement htmlElement, RenderNode parentRenderNode) {
        HashMap<String, String> styles = new HashMap<>();
        styles.putAll(parentRenderNode.getAppliedStyles());
        styles.putAll(htmlElement.getStyles());
        if (cssRule != null) {
            styles.putAll(cssRule.getPropertiesMap());
        }

        Set<String> allowedStyles = Set.of(
                "width", "height", "margin", "padding", "border-width", "color",
                "background-color", "font-size", "font-family", "text-align",
                "line-height", "display", "visibility", "z-index", "position"
        );

        styles.keySet().retainAll(allowedStyles);
        return styles;
    }
    private void matchCssAndHtml(HtmlElement htmlElement, RenderNode renderNode, List<String> selectors, CssRule cssRule) {
        if (selectors.isEmpty()) {
            if (cssRule != null) {
                renderNode.applyStyles(cssRule.getPropertiesMap());
            }
            return;
        }

        String selector = selectors.get(0);

        boolean matches = (htmlElement.getClasses() != null && Arrays.asList(htmlElement.getClasses()).contains(selector))
                || htmlElement.getTag().equals(selector);

        if (matches) {
            List<String> remainingSelectors = selectors.subList(1, selectors.size());

            // Если остались дочерние элементы, обходим их вместе с RenderTree
            List<HtmlElement> htmlChildren = htmlElement.getChildren();
            List<RenderNode> renderChildren = renderNode.getChildren();

            for (int i = 0; i < htmlChildren.size(); i++) {
                HtmlElement childHtml = htmlChildren.get(i);
                RenderNode childRender = renderChildren.get(i);
                // Рекурсивно проверяем дочерние элементы
                matchCssAndHtml(childHtml, childRender, new ArrayList<>(remainingSelectors), cssRule);
            }

            // Если правило совпало на текущем уровне, применяем его стили к текущему RenderNode
            if (cssRule != null) {
                renderNode.applyStyles(cssRule.getPropertiesMap());
            }
        } else {
            // Если текущий элемент не соответствует селектору, рекурсивно проверяем дочерние элементы
            List<HtmlElement> htmlChildren = htmlElement.getChildren();
            List<RenderNode> renderChildren = renderNode.getChildren();

            for (int i = 0; i < htmlChildren.size(); i++) {
                HtmlElement childHtml = htmlChildren.get(i);
                RenderNode childRender = renderChildren.get(i);
                // Рекурсивно проверяем дочерние элементы
                matchCssAndHtml(childHtml, childRender, new ArrayList<>(selectors), cssRule);
            }
        }
    }


}
