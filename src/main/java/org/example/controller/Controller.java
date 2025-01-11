package org.example.controller;

import org.example.controller.commands.Command;
import org.example.controller.commands.SearchCommand;
import org.example.model.Model;
import org.example.view.Canvas;
import org.example.view.Viewer;
import org.example.view.renderers.HtmlRenderer;
import org.example.view.renderers.LinkArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Desktop;
import java.net.URI;
import java.util.HashMap;

public class Controller implements ActionListener {
    private final Model model;
    private final Viewer viewer;
    private HtmlRenderer htmlRenderer;
    private HashMap<String, Command> commandMap;

    public Controller(Viewer viewer) {
        this.viewer = viewer;
        model = new Model(viewer);
        setupCommands();
    }

    public void attachCanvasMouseEvents(Canvas canvas) {
        this.htmlRenderer = canvas.getHtmlRenderer();
        canvas.addMouseListener(new CanvasMouseListener(this));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        commandMap.get(actionCommand).execute();
    }

    private void setupCommands() {
        SearchCommand searchCommand = new SearchCommand(viewer, model);
        commandMap = new HashMap<>();
        commandMap.put("Search", searchCommand);
    }

    void handleMouseClick(MouseEvent e) {
        if (htmlRenderer == null) {
            return;
        }

        int x = e.getX();
        int y = e.getY();

        for (LinkArea linkArea : htmlRenderer.getLinkAreas()) {
            if (linkArea.getArea().contains(x, y)) {
                openUrlInBrowser(linkArea.getUrl());
                break;
            }
        }
    }

    private void openUrlInBrowser(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class CanvasMouseListener extends MouseAdapter {
        private final Controller controller;

        public CanvasMouseListener(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            controller.handleMouseClick(e);
        }
    }

    public Model getModel() {
        return model;
    }
}
