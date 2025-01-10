package org.example.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CssRule {
    private List<String> selector;
    private HashMap<String, String> properties;

    public CssRule(List<String> selector) {
        this.selector = selector;
        this.properties = new HashMap<>();
    }

    public List<String> getSelector() {
        return selector;
    }

    public void setSelector(List<String> selector) {
        this.selector = selector;
    }

    public void addProperty(String property, String value) {
        this.properties.put(property, value);
    }

    public HashMap<String, String> getProperty() {
        return properties;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(selector + " {\n");
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            builder.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append(";\n");
        }
        builder.append("}");
        return builder.toString();
    }
}
