package org.example.model.css.cssom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CssRule {
    List<String> selectors;
    List<CssProperty> properties;
    String media = null;

    public CssRule(List<String> selectors) {
        this.selectors = selectors;
        this.properties = new ArrayList<>();
    }

    public void addProperty(String name, String value) {
        this.properties.add(new CssProperty(name, value));
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMedia() {
        return media;
    }

    public List<String> getSelector() {
        return selectors;
    }

    public HashMap<String, String> getPropertiesMap() {
        HashMap<String, String> map = new HashMap<>();
        for (CssProperty property : properties) {
            map.put(property.getName(), property.getValue());
        }
        return map;
    }

    public String getFullSelector() {
        return String.join(" ", selectors);
    }

    public String toCssString() {
        StringBuilder css = new StringBuilder();
        css.append("@media ").append(media);
        css.append(getFullSelector()).append(" {").append(System.lineSeparator());
        for (CssProperty property : properties) {
            css.append("    ").append(property.getName()).append(": ").append(property.getValue()).append(";")
                    .append(System.lineSeparator());
        }
        css.append("}");
        return css.toString();
    }

}
