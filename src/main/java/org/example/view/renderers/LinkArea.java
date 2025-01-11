package org.example.view.renderers;

import java.awt.*;
// не знаю куда поместить этот класс, или может просто оставить методы в рендере? ( в классе HtmlRender )
public class LinkArea {
    private final Rectangle area;
    private final String url;

    public LinkArea(Rectangle area, String url) {
        this.area = area;
        this.url = url;
    }

    public Rectangle getArea() {
        return area;
    }

    public String getUrl() {
        return url;
    }
}
