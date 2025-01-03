package org.example.view.htmlRenderer;

import java.util.ArrayList;
import java.util.List;

public class HtmlElement {
    private String tag;
    private String content;
    private String[] classes;
    private String[] ids;
    private List<HtmlElement> children;

    public HtmlElement(String tag, String content) {
        this.tag = tag;
        this.content = content;
        this.children = new ArrayList<>();
    }

    public void addChild(HtmlElement child) {
        children.add(child);
    }

    public String getTag() {
        return tag;
    }

    public String getContent() {
        return content;
    }

    public String[] getClasses() {
        return classes;
    }

    public HtmlElement setClasses(String[] classes) {
        this.classes = classes;
        return this;
    }

    public String[] getIds() {
        return ids;
    }

    public HtmlElement setIds(String[] ids) {
        this.ids = ids;
        return this;
    }

    public List<HtmlElement> getChildren() {
        return children;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
