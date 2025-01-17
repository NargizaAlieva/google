package org.example.view.renderers;

import java.awt.Rectangle;
import java.awt.Desktop;
import java.net.URI;

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

    public boolean contains(int x, int y) {
        return area.contains(x, y);
    }

    public void openUrlInBrowser() {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
