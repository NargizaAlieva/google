package org.example.model;

import org.example.model.css.CssParser;
import org.example.model.css.cssom.CssRule;
import org.example.model.css.cssom.CssTree;
import org.example.model.html.HtmlElement;
import org.example.model.html.HtmlParser;
import org.example.model.renderTree.MergeCssomDom;
import org.example.model.renderTree.RenderNode;
import org.example.model.renderTree.RenderTree;
import org.example.model.socket.HttpResponse;
import org.example.model.socket.Socket;
import org.example.view.Viewer;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model {
    private final Viewer viewer;
    private final Socket socket;
    private final HtmlParser htmlParser;
    private final CssParser cssParser;
    private HttpResponse httpResponse;
    private MergeCssomDom mergeCssomDom;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        socket = new Socket();
        htmlParser = new HtmlParser();
        cssParser = new CssParser();
        mergeCssomDom = new MergeCssomDom();
    }

    public void getHtml(String siteUrl) {
        viewer.getController().clearLinkAreas();
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
            HtmlElement dom = htmlParser.parseHtml(extractBodyContent(getHttpResponse().getHtmlBody()), getHttpResponse().getUrl());
            CssTree cssTree = cssParser.parse(getHttpResponse().getCssResources());
            System.out.println(getHttpResponse().getCssResources());
            RenderTree renderTree = mergeCssomDom.mergeCssomDom(dom, cssTree);
            for (CssRule css : cssTree.getRules()) {
                cssParser.findCssOfHtml(dom, css.getSelector(), css);
            }
            for (RenderNode renderTree1 : renderTree.getRoot().getChildren()) {
            }
            return dom;
        }
        return htmlParser.parseHtml(extractBodyContent(getHtml()), getHttpResponse().getUrl());
    }

    public RenderTree parse() {
        HtmlElement dom;
        if (httpResponse != null) {
            dom = htmlParser.parseHtml(extractBodyContent(getHttpResponse().getHtmlBody()), getHttpResponse().getUrl());
        } else {
            dom = htmlParser.parseHtml(extractBodyContent(getHtml()), null);
        }
        CssTree cssTree = cssParser.parse(getHttpResponse().getCssResources());
        System.out.println(getHttpResponse().getCssResources());
        RenderTree renderTree = mergeCssomDom.mergeCssomDom(dom, cssTree);
        for (CssRule css : cssTree.getRules()) {
            System.out.println(css.toCssString());
            cssParser.findCssOfHtml(dom, css.getSelector(), css);
        }

        return renderTree;
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

    public List<String> extractLinks() {
        return HtmlParser.extractLinks(getHtml());
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public String getBaseUrl() {
        return socket.getBaseUrl();
    }
}
