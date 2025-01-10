package org.example.model.css.cssom;

import java.util.List;
import java.util.ArrayList;

public class CssTree {
    List<CssRule> rules;

    public CssTree() {
        this.rules = new ArrayList<>();
    }

    public void addRule(CssRule rule) {
        this.rules.add(rule);
    }

    public List<CssRule> getRules() {
        return rules;
    }

    public void printTree() {
        for (CssRule rule : rules) {
            System.out.println("Selector: " + rule.getFullSelector());
            for (CssProperty property : rule.properties) {
                System.out.println("  " + property.name + ": " + property.value);
            }
        }
    }
}