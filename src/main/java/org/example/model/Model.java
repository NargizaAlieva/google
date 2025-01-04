package org.example.model;

import org.example.view.Viewer;

public class Model {
    Viewer viewer;
    Socket socket;
    String siteHtml;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        this.socket = new Socket();
    }

    public void getHtml(String siteUrl) {
        siteHtml = socket.connectWithSocket(siteUrl);
        System.out.println(siteHtml);
//        updateView();
    }

    public String getHtml() {
        siteHtml = """
            <body>
            <div>
                <h1>Welcome to Canvas Rendering</h1>
                <h2>A Simple HTML Renderer</h2>
                <p>
                    This is a simple renderer using <em>Java Canvas</em>. It can display various HTML elements.
                </p>
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
        return siteHtml;
    }

    public void updateView() {
        viewer.update();
    }
}
