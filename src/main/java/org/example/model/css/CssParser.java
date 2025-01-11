package org.example.model.css;

import org.example.model.css.cssom.CssProperty;
import org.example.model.css.cssom.CssRule;
import org.example.model.css.cssom.CssTree;
import org.example.model.html.HtmlElement;
import org.example.model.socket.CssResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CssParser {
    CssTree cssTree = new CssTree();

    public CssParser() {}

    public CssTree parse(List<CssResource> cssResources) {
        for (CssResource cssResource : cssResources) {
            String[] blocks = cssResource.getContent().split("}");

            for (String block : blocks) {
                block = block.trim();
                if (block.isEmpty()) continue;

                String[] parts = block.split("\\{", 2);
                if (parts.length != 2) continue;

                String selectorsPart = parts[0].trim();

                selectorsPart = selectorsPart.replaceAll("/\\*.*?\\*/", "").trim();

                if (selectorsPart.isEmpty()) continue;

                selectorsPart = selectorsPart.replace(".", "").trim();

                String[] rawSelectors = selectorsPart.split("\\s+");

                List<String> filteredSelectors = new ArrayList<>();
                for (String selector : rawSelectors) {
                    if (!selector.isEmpty() && selector.matches("[a-zA-Z0-9_-]+")) {
                        filteredSelectors.add(selector);
                    }
                }

                if (filteredSelectors.isEmpty()) continue;

                CssRule rule = new CssRule(filteredSelectors);

                String propertiesBlock = parts[1].trim();
                String[] properties = propertiesBlock.split(";");
                for (String property : properties) {
                    String[] keyValue = property.split(":", 2);
                    if (keyValue.length == 2) {
                        rule.addProperty(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
                cssTree.addRule(rule);
            }
        }
        return cssTree;
    }

    public void findCssOfHtml(HtmlElement htmlElement, List<String> selectors, CssRule cssRule) {
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

    public CssTree getCssTree() {
        return cssTree;
    }
}