package org.example.model.socket;

import java.util.List;
import java.util.Map;

public class HttpResponse {
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final String htmlBody;
    private final List<CssResource> cssResources;

    public HttpResponse(int statusCode, String statusMessage, Map<String, String> headers, String htmlBody, List<CssResource> cssResources) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.htmlBody = htmlBody;
        this.cssResources = cssResources;
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
}
