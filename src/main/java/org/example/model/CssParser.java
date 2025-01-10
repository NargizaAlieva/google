package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class CssParser {
    List<CssRule> rules = new ArrayList<>();

    public void parse(List<Socket.CssResource> cssResources) {
        for (Socket.CssResource cssResource : cssResources) {
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

                rules.add(rule);
            }
        }
    }


    public List<CssRule> getRules() {
        return rules;
    }
}