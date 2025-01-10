package org.example.model.socket;

public class CssResource {
    private final String url;
    private final String content;

    public CssResource(String url, String content) {
        this.url = url;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
