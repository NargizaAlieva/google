package org.example.controller;

import org.example.controller.commands.Command;
import org.example.controller.commands.SearchCommand;
import org.example.model.CssParser;
import org.example.model.Model;
import org.example.model.Socket;
import org.example.view.Viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Controller implements ActionListener {
    private Model model;
    private Viewer viewer;
    private HashMap<String, Command> commandMap;
    private Socket socket;
    private CssParser cssParser;

    public Controller(Viewer viewer) {
        this.viewer = viewer;
        cssParser = new CssParser();
        socket = new Socket();
        model = new Model(viewer, socket);
        setupCommands();
    }

    public Model getModel() {
        return model;
    }

    public CssParser getCssParser() {
        return cssParser;
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
}
