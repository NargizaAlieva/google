package org.example.model.cssom;

public class CssProperty {
    String name;
    String value;

    public CssProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }
}
