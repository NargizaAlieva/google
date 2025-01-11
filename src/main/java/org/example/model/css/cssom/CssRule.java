package org.example.model.css.cssom;

import java.util.ArrayList;
import java.util.HashMap;
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

    public HashMap<String, String> getPropertiesMap() {
        HashMap<String, String> map = new HashMap<>();
        for (CssProperty property : properties) {
            map.put(property.getName(), property.getValue());
        }
        return map;
    }

    public String getFullSelector() {
        return String.join(" ", selectors); // Собрать обратно в строку
    }
    public String toCssString() {
        StringBuilder css = new StringBuilder();
        css.append(getFullSelector()).append(" {").append(System.lineSeparator());
        for (CssProperty property : properties) {
            css.append("    ").append(property.getName()).append(": ").append(property.getValue()).append(";")
                    .append(System.lineSeparator());
        }
        css.append("}");
        return css.toString();
    }

}
