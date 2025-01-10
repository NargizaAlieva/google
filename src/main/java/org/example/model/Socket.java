package org.example.model;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Socket {
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_REDIRECTS = 5;

    public String fetchHtml(String urlString) {
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

                    // Send request
                    sendHttpRequest(sslSocket, host, path);

                    // Read response
                    HttpResponse response = readHttpResponse(sslSocket);

                    // Check response code
                    if (response.getStatusCode() == 301 || response.getStatusCode() == 302) {
                        String location = response.getHeaders().get("Location");
                        if (location == null) {
                            throw new IOException("Redirect location missing");
                        }
                        System.out.println("Redirecting to: " + location);
                        url = new URL(location);
                        host = url.getHost();
                        path = url.getPath().isEmpty() ? "/" : url.getPath();
                        port = url.getProtocol().equalsIgnoreCase("https") ? 443 : 80;
                        continue;
                    }

                    // Return HTML for successful response
                    if (response.getStatusCode() == 200) {
                        return response.getBody();
                    }

                    // Handle non-successful responses
                    throw new IOException("HTTP error: " + response.getStatusCode() + " " + response.getStatusMessage());
                }
            }
            throw new IOException("Too many redirects");
        } catch (SocketTimeoutException e) {
            return "Connection timed out: " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private void sendHttpRequest(SSLSocket sslSocket, String host, String path) throws IOException {
        PrintWriter writer = new PrintWriter(sslSocket.getOutputStream(), true);
        writer.println("GET " + path + " HTTP/1.1");
        writer.println("Host: " + host);
        writer.println("Connection: close");
        writer.println();
    }

    private HttpResponse readHttpResponse(SSLSocket sslSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        String statusLine = reader.readLine();
        if (statusLine == null) {
            throw new IOException("Empty response from server");
        }

        // Parse status line
        String[] statusParts = statusLine.split(" ", 3);
        if (statusParts.length < 3) {
            throw new IOException("Invalid HTTP response: " + statusLine);
        }
        int statusCode = Integer.parseInt(statusParts[1]);
        String statusMessage = statusParts[2];

        // Read headers
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

        // Read body
        StringBuilder body = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            body.append(line).append("\n");
        }

        return new HttpResponse(statusCode, statusMessage, headers, body.toString().trim());
    }

    private static class HttpResponse {
        private final int statusCode;
        private final String statusMessage;
        private final Map<String, String> headers;
        private final String body;

        public HttpResponse(int statusCode, String statusMessage, Map<String, String> headers, String body) {
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            this.headers = headers;
            this.body = body;
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

        public String getBody() {
            return body;
        }
    }
}
