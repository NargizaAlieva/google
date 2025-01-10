package org.example.model.socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Socket {
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_REDIRECTS = 5;

    public HttpResponse fetchHtmlWithCss(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost();
            int port = url.getProtocol().equalsIgnoreCase("https") ? 443 : 80;
            String path = url.getPath().isEmpty() ? "/" : url.getPath();

            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            for (int redirectCount = 0; redirectCount < MAX_REDIRECTS; redirectCount++) {
                try (SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port)) {
                    sslSocket.setSoTimeout(TIMEOUT_MS);
                    sslSocket.startHandshake();

                    sendHttpRequest(sslSocket, host, path);

                    HttpResponse response = readHttpResponse(sslSocket, url);

                    if (response.getStatusCode() == 301 || response.getStatusCode() == 302) {
                        String location = response.getHeaders().get("Location");
                        if (location == null) {
                            throw new IOException("Redirect location missing");
                        }
                        System.out.println("Redirecting to: " + location);
                        url = new URL(urlString + location);
                        host = url.getHost();
                        path = url.getPath().isEmpty() ? "/" : url.getPath();
                        port = url.getProtocol().equalsIgnoreCase("https") ? 443 : 80;
                        continue;
                    }

                    if (response.getStatusCode() == 200) {
                        return response;
                    }

                    throw new IOException("HTTP error: " + response.getStatusCode() + " " + response.getStatusMessage());
                }
            }
            throw new IOException("Too many redirects");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendHttpRequest(SSLSocket sslSocket, String host, String path) throws IOException {
        PrintWriter writer = new PrintWriter(sslSocket.getOutputStream(), true);
        writer.println("GET " + path + " HTTP/1.1");
        writer.println("Host: " + host);
        writer.println("Connection: close");
        writer.println();
    }

    private HttpResponse readHttpResponse(SSLSocket sslSocket, URL baseUrl) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        String statusLine = reader.readLine();
        if (statusLine == null) {
            throw new IOException("Empty response from server");
        }

        String[] statusParts = statusLine.split(" ", 3);
        int statusCode = Integer.parseInt(statusParts[1]);
        String statusMessage = statusParts[2];

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
        }

        StringBuilder body = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            body.append(line).append("\n");
        }

        String htmlBody = body.toString().trim();
        List<CssResource> cssResources = downloadCssFiles(htmlBody, baseUrl);

        return new HttpResponse(statusCode, statusMessage, headers, htmlBody, cssResources);
    }

    private List<CssResource> downloadCssFiles(String html, URL baseUrl) {
        List<CssResource> cssResources = new ArrayList<>();
        try {
            String[] lines = html.split("\n");
            for (String line : lines) {
                if (line.contains("<link") && line.contains("rel=\"stylesheet\"")) {
                    int hrefStart = line.indexOf("href=\"") + 6;
                    int hrefEnd = line.indexOf("\"", hrefStart);
                    String cssPath = line.substring(hrefStart, hrefEnd);

                    String cssUrl = cssPath.startsWith("http")
                            ? cssPath
                            : baseUrl.getProtocol() + "://" + baseUrl.getHost() + (cssPath.startsWith("/") ? cssPath : "/" + cssPath);

                    String cssContent = fetchCss(cssUrl);
                    if (cssContent != null) {
                        cssResources.add(new CssResource(cssUrl, cssContent));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cssResources;
    }

    private String fetchCss(String cssUrl) {
        return fetchHtmlWithCss(cssUrl).getHtmlBody();
    }
}
