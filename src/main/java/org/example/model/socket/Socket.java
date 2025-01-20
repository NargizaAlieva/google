package org.example.model.socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
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
            System.out.println("Using path: " + path);
            InetAddress address = InetAddress.getByName(host);
            System.out.println("Resolved IP: " + address.getHostAddress());

            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            for (int redirectCount = 0; redirectCount < MAX_REDIRECTS; redirectCount++) {
                try (SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port)) {
                    sslSocket.setSoTimeout(TIMEOUT_MS);
                    sslSocket.startHandshake();
                    sslSocket.setEnabledProtocols(new String[]{"TLSv1.2"});

                    sendHttpRequest(sslSocket, host, path);

                    HttpResponse response = readHttpResponse(sslSocket, url);

                    if (response.getStatusCode() == 301 || response.getStatusCode() == 302) {
                        String location = response.getHeaders().get("Location");
                        if (location == null) throw new IOException("Redirect location missing");

                        url = new URL(url, location);
                        host = url.getHost();
                        path = url.getPath().isEmpty() ? "/" : url.getPath();
                        port = url.getProtocol().equalsIgnoreCase("https") ? 443 : 80;
                        continue;
                    }

                    if (response.getStatusCode() == 200) return response;

                    throw new IOException(
                            "HTTP error: " + response.getStatusCode() + " " + response.getStatusMessage());
                }
            }
            throw new IOException("Too many redirects");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void sendHttpRequest(SSLSocket sslSocket, String host, String path) throws IOException {
        PrintWriter out = new PrintWriter(sslSocket.getOutputStream());
        out.print("GET " + path + " HTTP/1.1\r\n");
        out.print("Host: " + host + "\r\n");
        out.print("Connection: close\r\n");
        out.print("User-Agent: JavaClient/1.0\r\n");
        out.print("\r\n");
        out.flush();
    }





    private HttpResponse readHttpResponse(SSLSocket sslSocket, URL url) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        String statusLine = reader.readLine();

        if (statusLine == null || !statusLine.startsWith("HTTP/")) {
            throw new IOException("Invalid HTTP response: " + statusLine);
        }

        // Extract status code and message from the status line
        String[] statusParts = statusLine.split(" ", 3);
        if (statusParts.length < 3 || !statusParts[1].matches("\\d+")) {
            throw new IOException("Malformed status line: " + statusLine);
        }
        int statusCode = Integer.parseInt(statusParts[1]);
        String statusMessage = statusParts[2];

        // Parse headers
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            } else {
                throw new IOException("Malformed header: " + line);
            }
        }

        // Read HTML body
        StringBuilder body = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            body.append(line).append("\n");
        }

        // Placeholder for CSS resources, if any parsing for them is later added
        List<CssResource> cssResources = List.of(); // Empty list as a default

        // Construct and return the HttpResponse object
        return new HttpResponse(statusCode, statusMessage, headers, body.toString(), cssResources, url);
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
                            : baseUrl.getProtocol() + "://" + baseUrl.getHost()
                                    + (cssPath.startsWith("/") ? cssPath : "/" + cssPath);

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
