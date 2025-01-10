package org.example.model.cssom;

import java.util.ArrayList;
import java.util.List;

public class CssRule {
    List<String> selectors; // Селекторы как список
    List<CssProperty> properties;

    public CssRule(List<String> selectors) {
        this.selectors = selectors;
        this.properties = new ArrayList<>();
    }

    public void addProperty(String name, String value) {
        this.properties.add(new CssProperty(name, value));
    }

    public List<String> getSelector() {
        return selectors;
    }

    public List<CssProperty> getProperties() {
        return properties;
    }

    public String getFullSelector() {
        return String.join(" ", selectors); // Собрать обратно в строку
    }
}
