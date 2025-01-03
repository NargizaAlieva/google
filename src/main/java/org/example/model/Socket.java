package org.example.model;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;

public class Socket {
    private String htmlCode;
    public String connectWithSocket(String urlString) {
        try {
            htmlCode = "";
            URL url = new URL(urlString);
            String host = url.getHost();
            int port = url.getProtocol().equals("https") ? 443 : 80;

            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port)) {

                sslSocket.startHandshake();

                OutputStream outputStream = sslSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

                writer.println("GET " + url.getPath() + " HTTP/1.1");
                writer.println("Host: " + host);
                writer.println("Connection: close");
                writer.println();

                String line;
                boolean isRedirect = false;
                String location = null;

                while ((line = reader.readLine()) != null) {
                    htmlCode += "\n" + line;
                    if (line.startsWith("HTTP/1.1 301") || line.startsWith("HTTP/1.1 302")) {
                        isRedirect = true;
                    }
                    if (line.startsWith("Location: ")) {
                        location = line.substring(10).trim();
                    }
                }

                if (isRedirect && location != null) {
                    System.out.println("Redirecting to: " + line + location);
                    connectWithSocket(url + location);
                }

                System.out.println(htmlCode);
                return htmlCode;
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
