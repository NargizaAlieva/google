package org.example.model.socket;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private final URL url;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final String htmlBody;
    private final List<CssResource> cssResources;

    public HttpResponse(int statusCode, String statusMessage, Map<String, String> headers, String htmlBody, List<CssResource> cssResources, URL url) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.htmlBody = htmlBody;
        this.cssResources = cssResources;
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public List<CssResource> getCssResources() {
        return cssResources;
    }

    public URL getUrl() {
        return url;
    }
}
