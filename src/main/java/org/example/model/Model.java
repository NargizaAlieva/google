package org.example.model;

import org.example.model.css.CssParser;
import org.example.model.css.cssom.CssRule;
import org.example.model.html.HtmlElement;
import org.example.model.html.HtmlParser;
import org.example.model.socket.HttpResponse;
import org.example.model.socket.Socket;
import org.example.view.Viewer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model {
    private final Viewer viewer;
    private final Socket socket;
    private final HtmlParser htmlParser;
    private final CssParser cssParser;
    private HttpResponse httpResponse;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        this.socket = new Socket();
        htmlParser = new HtmlParser();
        cssParser = new CssParser();
    }

    public void getHtml(String siteUrl) {
        httpResponse = socket.fetchHtmlWithCss(siteUrl);
        viewer.update();
    }

    public String getHtml() {
        return """
            <body>
            <div>
                <h1>Welcome to Canvas Rendering</h1>
                <h2>A Simple HTML Renderer</h2>
                <p>This is a simple renderer using <em>Java Canvas</em>. It can display various HTML elements.</p>
                <ul>
                    <li>
                            This is the first item in a list.
                            </li>
                    <li>
                            This is the second item, with emphasis.
                            </li>
                    <li>
                        <a href="https://gratisography.com/wp-content/uploads/2024/10/gratisography-cool-cat-800x525.jpg">This is a link inside a list item.</a>
                    </li>
                </ul>
                <div>
                    <h3>Nested Content Inside a Div</h3>
                    <p>This is a nested paragraph inside a div.</p>
                    <ul>
                        <li>Nested list item one.</li>
                        <li>Nested list item two with a bold word.</li>
                    </ul>
                </div>
                <p>Here is an image:</p>
                <img src="https://gratisography.com/wp-content/uploads/2024/10/gratisography-cool-cat-800x525.jpg">
            </div>
            </body>
        """;
    }

    public HtmlElement parseHtml() {
        if (httpResponse != null) {
            HtmlElement dom = htmlParser.parseHtml(extractBodyContent(getHttpResponse().getHtmlBody()));
            cssParser.parse(getHttpResponse().getCssResources());
            for(CssRule css : cssParser.getCssTree().getRules()){
                System.out.println(css);
                cssParser.findCssOfHtml(dom, css.getSelector(), css);
            }
            return dom;
        }
        return htmlParser.parseHtml(extractBodyContent(getHtml()));
    }

    private String extractBodyContent(String html) {
        Pattern bodyPattern = Pattern.compile("<body[^>]*>(.*?)</body>", Pattern.DOTALL);
        Matcher matcher = bodyPattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        System.err.println("Body not found");
        return null;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
